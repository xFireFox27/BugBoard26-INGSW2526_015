package it.unina.backend.daointerface;

import java.sql.SQLException;
import it.unina.backend.entity.Comment;
import java.util.List;

public interface CommentDaoInterface {

    public List<Comment> findCommentsByIssueId(int issueId) throws SQLException;
}
