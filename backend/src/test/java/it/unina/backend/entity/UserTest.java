package it.unina.backend.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static final String VALID_EMAIL = "cris.rana@example.com";
    private static final String VALID_USER = "crisrana";
    private static final String VALID_PASS = "passwordSegreta";
    private static final String VALID_NAME = "Christian";
    private static final String VALID_SURNAME = "Ranavolo";

    private static final String VALID_ROLE_NORMAL = "Normal";


    // --- TC1: WEAK - ROLE ADMIN ---

    @Test
    @DisplayName("TC1 (CE1, CE5): Ruolo 'Admin' deve essere accettato")
    void testValidate_RoleAdmin() {
        boolean result = User.validateUserData(VALID_EMAIL, VALID_USER, VALID_PASS, VALID_NAME, VALID_SURNAME, "Admin");
        assertTrue(result, "Il ruolo Admin dovrebbe essere valido");
    }

    // --- TC2: WEAK - ROLE NORMAL ---

    @Test
    @DisplayName("TC2 (CE1, CE6): Ruolo 'Normal' deve essere accettato")
    void testValidate_RoleNormal() {
        boolean result = User.validateUserData(VALID_EMAIL, VALID_USER, VALID_PASS, VALID_NAME, VALID_SURNAME, "Normal");
        assertTrue(result, "Il ruolo Normal dovrebbe essere valido");
    }

    // --- TC3: WEAK - ROLE EXTERNAL ---

    @Test
    @DisplayName("TC3 (CE1, CE7): Ruolo 'External' deve essere accettato")
    void testValidate_RoleExternal() {
        boolean result = User.validateUserData(VALID_EMAIL, VALID_USER, VALID_PASS, VALID_NAME, VALID_SURNAME, "External");
        assertTrue(result, "Il ruolo External dovrebbe essere valido");
    }

    // --- TC4, TC5, TC6: ROBUST - INVALID EMAIL ---

    @Test
    @DisplayName("TC4 (CE2): Email Null -> False")
    void testValidate_EmailNull() {
        assertFalse(User.validateUserData(null, VALID_USER, VALID_PASS, VALID_NAME, VALID_SURNAME, VALID_ROLE_NORMAL));
    }

    @Test
    @DisplayName("TC5 (CE3): Email Vuota -> False")
    void testValidate_EmailEmpty() {
        assertFalse(User.validateUserData("", VALID_USER, VALID_PASS, VALID_NAME, VALID_SURNAME, VALID_ROLE_NORMAL));
    }

    @Test
    @DisplayName("TC6 (CE4): Email Blank -> False")
    void testValidate_EmailBlank() {
        assertFalse(User.validateUserData("   ", VALID_USER, VALID_PASS, VALID_NAME, VALID_SURNAME, VALID_ROLE_NORMAL));
    }

    // --- TC7, TC8, TC9: ROBUST - INVALID USERNAME ---

    @Test
    @DisplayName("TC7 (CE2): Username Null -> False")
    void testValidate_UsernameNull() {
        assertFalse(User.validateUserData(VALID_EMAIL, null, VALID_PASS, VALID_NAME, VALID_SURNAME, VALID_ROLE_NORMAL));
    }

    @Test
    @DisplayName("TC8 (CE3): Username Vuoto -> False")
    void testValidate_UsernameEmpty() {
        assertFalse(User.validateUserData(VALID_EMAIL, "", VALID_PASS, VALID_NAME, VALID_SURNAME, VALID_ROLE_NORMAL));
    }

    @Test
    @DisplayName("TC9 (CE4): Username Blank -> False")
    void testValidate_UsernameBlank() {
        assertFalse(User.validateUserData(VALID_EMAIL, "   ", VALID_PASS, VALID_NAME, VALID_SURNAME, VALID_ROLE_NORMAL));
    }

    // --- TC10, TC11, TC12: ROBUST - INVALID PASSWORD ---

    @Test
    @DisplayName("TC10 (CE2): Password Null -> False")
    void testValidate_PasswordNull() {
        assertFalse(User.validateUserData(VALID_EMAIL, VALID_USER, null, VALID_NAME, VALID_SURNAME, VALID_ROLE_NORMAL));
    }

    @Test
    @DisplayName("TC11 (CE3): Password Vuota -> False")
    void testValidate_PasswordEmpty() {
        assertFalse(User.validateUserData(VALID_EMAIL, VALID_USER, "", VALID_NAME, VALID_SURNAME, VALID_ROLE_NORMAL));
    }

    @Test
    @DisplayName("TC12 (CE4): Password Blank -> False")
    void testValidate_PasswordBlank() {
        assertFalse(User.validateUserData(VALID_EMAIL, VALID_USER, "   ", VALID_NAME, VALID_SURNAME, VALID_ROLE_NORMAL));
    }

    // --- TC13, TC14, TC15: ROBUST - INVALID NAME ---

    @Test
    @DisplayName("TC13 (CE2): Name Null -> False")
    void testValidate_NameNull() {
        assertFalse(User.validateUserData(VALID_EMAIL, VALID_USER, VALID_PASS, null, VALID_SURNAME, VALID_ROLE_NORMAL));
    }

    @Test
    @DisplayName("TC14 (CE3): Name Vuoto -> False")
    void testValidate_NameEmpty() {
        assertFalse(User.validateUserData(VALID_EMAIL, VALID_USER, VALID_PASS, "", VALID_SURNAME, VALID_ROLE_NORMAL));
    }

    @Test
    @DisplayName("TC15 (CE4): Name Blank -> False")
    void testValidate_NameBlank() {
        assertFalse(User.validateUserData(VALID_EMAIL, VALID_USER, VALID_PASS, "   ", VALID_SURNAME, VALID_ROLE_NORMAL));
    }

    // --- TC16, TC17, TC18: ROBUST - INVALID SURNAME ---

    @Test
    @DisplayName("TC16 (CE2): Surname Null -> False")
    void testValidate_SurnameNull() {
        assertFalse(User.validateUserData(VALID_EMAIL, VALID_USER, VALID_PASS, VALID_NAME, null, VALID_ROLE_NORMAL));
    }

    @Test
    @DisplayName("TC17 (CE3): Surname Vuoto -> False")
    void testValidate_SurnameEmpty() {
        assertFalse(User.validateUserData(VALID_EMAIL, VALID_USER, VALID_PASS, VALID_NAME, "", VALID_ROLE_NORMAL));
    }

    @Test
    @DisplayName("TC18 (CE4): Surname Blank -> False")
    void testValidate_SurnameBlank() {
        assertFalse(User.validateUserData(VALID_EMAIL, VALID_USER, VALID_PASS, VALID_NAME, "   ", VALID_ROLE_NORMAL));
    }

    // --- TC19, TC20: ROBUST - INVALID ROLE ---

    @Test
    @DisplayName("TC19 (CE8): Ruolo sconosciuto -> False")
    void testValidate_RoleUnknown() {
        String invalidRole = "SuperUser";
        assertFalse(User.validateUserData(VALID_EMAIL, VALID_USER, VALID_PASS, VALID_NAME, VALID_SURNAME, invalidRole));
    }

    @Test
    @DisplayName("TC20 (CE9): Ruolo Null -> NullPointerException")
    void testValidate_RoleNull() {
        assertThrows(NullPointerException.class, () ->
                User.validateUserData(VALID_EMAIL, VALID_USER, VALID_PASS, VALID_NAME, VALID_SURNAME, null)
        );
    }
}