package it.unina.backend.service;
import it.unina.backend.dao.CommentDao;
import it.unina.backend.dao.UserDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import it.unina.backend.entity.User;
import it.unina.backend.entity.Comment;
import it.unina.backend.dto.CommentDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private CommentDao commentDao;

    @InjectMocks
    private CommentService commentService;

    private User createValidUser(String username) {
        return new User(
                "test@example.com", // email
                username,           // username
                "hashedPassword",   // passwordHash
                "Mario",            // name
                "Rossi",            // surname
                "Normal"            // role (Admin, Normal, o External)
        );
    }

    // --- TC1: WEAK - HAPPY PATH ---

    @Test
    @DisplayName("TC1 (CE1, CE3, CE7, CE11): Flusso Valido - DTO e User corretti -> Salva commento")
    void testAddComment_Success() throws Exception {
        // ARRANGE
        String username = "mario.rossi";

        CommentDto validDto = new CommentDto();
        validDto.setText("Ciao mondo");
        validDto.setIssueId(10);

        // CREAZIONE CORRETTA DELL'UTENTE
        User mockUser = createValidUser(username);

        // Simuliamo che il DAO restituisca questo utente
        when(userDao.findUserByUsername(username)).thenReturn(mockUser);

        // ACT
        Comment result = commentService.addComment(validDto, username);

        // ASSERT
        assertNotNull(result);
        assertEquals(mockUser, result.getUser()); // Verifica che l'utente sia stato associato
        assertEquals("Ciao mondo", result.getText());

        verify(commentDao, times(1)).insertComment(any(Comment.class));
    }

    // --- TC2: ROBUST - DTO NULL ---

    @Test
    @DisplayName("TC2 (CE2): DTO Null -> IllegalArgumentException")
    void testAddComment_DtoNull() {
        assertThrows(IllegalArgumentException.class, () ->
                commentService.addComment(null, "user")
        );
        verifyNoInteractions(userDao);
        verifyNoInteractions(commentDao);
    }

    // --- TC3, TC4, TC5: ROBUST - INVALID TEXT ---

    @Test
    @DisplayName("TC3 (CE1, CE4, CE7): Text Null -> IllegalArgumentException")
    void testAddComment_TextNull() {
        CommentDto dto = new CommentDto();
        dto.setText(null);
        dto.setIssueId(10);

        assertThrows(IllegalArgumentException.class, () ->
                commentService.addComment(dto, "user")
        );
        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("TC4 (CE1, CE5, CE7): Text Vuoto -> IllegalArgumentException")
    void testAddComment_TextEmpty() {
        CommentDto dto = new CommentDto();
        dto.setText("");
        dto.setIssueId(10);

        assertThrows(IllegalArgumentException.class, () ->
                commentService.addComment(dto, "user")
        );
        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("TC5 (CE1, CE6, CE7): Text Blank -> IllegalArgumentException")
    void testAddComment_TextBlank() {
        CommentDto dto = new CommentDto();
        dto.setText("   ");
        dto.setIssueId(10);

        assertThrows(IllegalArgumentException.class, () ->
                commentService.addComment(dto, "user")
        );
        verifyNoInteractions(userDao);
    }

    // --- TC6, TC7, TC8: ROBUST - INVALID ISSUE ID ---

    @Test
    @DisplayName("TC6 (CE1, CE3, CE8): ID Null -> IllegalArgumentException")
    void testAddComment_IdNull() {
        CommentDto dto = new CommentDto();
        dto.setText("Valid Text");
        dto.setIssueId(null);

        assertThrows(IllegalArgumentException.class, () ->
                commentService.addComment(dto, "user")
        );
        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("TC7 (CE1, CE3, CE9): ID Zero -> IllegalArgumentException")
    void testAddComment_IdZero() {
        CommentDto dto = new CommentDto();
        dto.setText("Valid Text");
        dto.setIssueId(0);

        assertThrows(IllegalArgumentException.class, () ->
                commentService.addComment(dto, "user")
        );
        verifyNoInteractions(userDao);
    }

    @Test
    @DisplayName("TC8 (CE1, CE3, CE10): ID Negativo -> IllegalArgumentException")
    void testAddComment_IdNegative() {
        CommentDto dto = new CommentDto();
        dto.setText("Valid Text");
        dto.setIssueId(-5);

        assertThrows(IllegalArgumentException.class, () ->
                commentService.addComment(dto, "user")
        );
        verifyNoInteractions(userDao);
    }

    // --- TC9: ROBUST - USER NOT FOUND ---

    @Test
    @DisplayName("TC9 (CE1, CE3, CE7, CE12): Utente non trovato nel DB -> IllegalArgumentException")
    void testAddComment_UserNotFound() throws Exception {
        // ARRANGE
        CommentDto validDto = new CommentDto();
        validDto.setText("Valid Text");
        validDto.setIssueId(10);
        String username = "unknown_user";

        when(userDao.findUserByUsername(username)).thenReturn(null);

        // ACT & ASSERT
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                commentService.addComment(validDto, username)
        );

        assertTrue(ex.getMessage().contains("User not found"));
        verify(userDao).findUserByUsername(username);
        verifyNoInteractions(commentDao);
    }
}