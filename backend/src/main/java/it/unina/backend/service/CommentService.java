package it.unina.backend.service;

import it.unina.backend.dao.CommentDao;
import it.unina.backend.dao.UserDao;
import it.unina.backend.dto.CommentDto;
import it.unina.backend.entity.Comment;
import it.unina.backend.entity.User;
import java.sql.SQLException;

public class CommentService {

    private static CommentService instance;
    private final CommentDao commentDao = CommentDao.getInstance();
    private final UserDao userDao = UserDao.getInstance();

    private CommentService() {}

    public static CommentService getInstance() {
        if (instance == null) {
            instance = new CommentService();
        }
        return instance;
    }

    private void validateCommentInput(String text, Integer issueId) {
        if (text == null || text.trim().isEmpty()){
            throw new IllegalArgumentException("Text of the comment must be provided.");
        }
        if (issueId == null || issueId == 0 || issueId < 0){
            throw new IllegalArgumentException("Issue ID must be valid.");
        }
    }

    public Comment addComment(CommentDto dto, String username) throws SQLException, IllegalArgumentException {
        if (dto == null){
            throw new IllegalArgumentException("Comment data must be provided.");
        }
        validateCommentInput(dto.getText(), dto.getIssueId());

        User user = userDao.findUserByUsername(username);

        Comment comment = new Comment(dto);
        comment.setUser(user);

        commentDao.insertComment(comment);

        return comment;
    }
}