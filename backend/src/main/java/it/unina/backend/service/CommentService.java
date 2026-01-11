package it.unina.backend.service;

import it.unina.backend.dao.CommentDao;
import it.unina.backend.dao.UserDao;
import it.unina.backend.dto.CommentDto;
import it.unina.backend.entity.Comment;
import it.unina.backend.entity.User;
import java.sql.SQLException;

public class CommentService {

    private static CommentService instance;

    private final CommentDao commentDao;
    private final UserDao userDao;

    protected CommentService(CommentDao commentDao, UserDao userDao) {
        this.commentDao = commentDao;
        this.userDao = userDao;
    }

    public static synchronized CommentService getInstance() {
        if (instance == null) {
            instance = new CommentService(CommentDao.getInstance(), UserDao.getInstance());
        }
        return instance;
    }

    private void validateCommentInput(String text, Integer issueId) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment text cannot be null or empty");
        }

        if (issueId == null || issueId == 0 || issueId < 0) {
            throw new IllegalArgumentException("Invalid issue ID");
        }
    }

    public Comment addComment(CommentDto dto, String username) throws SQLException, IllegalArgumentException {
        if (dto == null) {
            throw new IllegalArgumentException("Comment data cannot be null");
        }

        validateCommentInput(dto.getText(), dto.getIssueId());

        User user = userDao.findUserByUsername(username);
        if(user == null) {
            throw new IllegalArgumentException("User not found" + username);
        }

        Comment comment = new Comment(dto);
        comment.setUser(user);
        commentDao.insertComment(comment);

        return comment;
    }
}