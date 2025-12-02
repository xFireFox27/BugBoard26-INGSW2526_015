package it.unina.backend.service;

import it.unina.backend.dao.CommentDao;
import it.unina.backend.dao.UserDao;
import it.unina.backend.dto.CommentDto;
import it.unina.backend.entity.Comment;
import it.unina.backend.entity.User;
import java.sql.SQLException;

public class CommentService {

    private static final CommentService instance = new CommentService();
    private final CommentDao commentDao = CommentDao.getInstance();
    private final UserDao userDao = UserDao.getInstance();

    private CommentService() {}

    public static CommentService getInstance() { return instance; }

    private void validateCommentInput(String text, Integer issueId) {
        if (text == null || text.trim().isEmpty()) throw new IllegalArgumentException("Il testo del commento è obbligatorio.");
        if (issueId == null) throw new IllegalArgumentException("L'ID della issue è obbligatorio.");
    }


    public Comment addComment(CommentDto dto, String username) throws SQLException, IllegalArgumentException {
        if (dto == null) throw new IllegalArgumentException("Il corpo della richiesta è vuoto.");
        validateCommentInput(dto.getText(), dto.getIssueId());

        User user = userDao.findUserByUsername(username);

        Comment comment = new Comment(dto);
        comment.setUser(user);

        commentDao.insertComment(comment);

        return comment;
    }
}