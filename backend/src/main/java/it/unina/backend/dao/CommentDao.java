package it.unina.backend.dao;

import it.unina.backend.daointerface.CommentDaoInterface;
import it.unina.backend.entity.Comment;
import it.unina.backend.connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import static it.unina.backend.util.DaoUtil.*;

public class CommentDao implements CommentDaoInterface {

    private static CommentDao instance;
    private static UserDao userDao = UserDao.getInstance();

    private CommentDao(){}

    public static CommentDao getInstance(){

        if(instance == null){
            instance = new CommentDao();
        }
        return instance;
    }

    @Override
    public List<Comment> findCommentsByIssueId(int issueId) throws SQLException {

        String sql = "select c.comment_id, c.text, c.created_on," +
                "u.username, u.email, u.password_hash, u.name, u.surname, u.role, u.created_on " +
                "FROM Comment AS c JOIN \"user\" AS u ON c.written_by = u.username " +
                "WHERE c.related_to = ?";

        List<Comment> comments = new ArrayList<>();

        try(Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement st = connection.prepareStatement(sql)){

            st.setInt(1, issueId);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    comments.add(new Comment(
                            rs.getInt("comment_id"),
                            rs.getString("text"),
                            rs.getObject("created_on", OffsetDateTime.class),
                            issueId,
                            createUserFromResultSet(rs)
                    ));
                }
            }
            return comments;
        }
    }

    @Override
    public boolean insertComment(Comment comment, String username) throws SQLException {
        String sql = "INSERT INTO Comment(text, related_to, written_by) VALUES (?, ?, ?) " +
                        "RETURNING comment_id, created_on";

        try(Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement st = connection.prepareStatement(sql)){
            st.setString(1, comment.getText());
            st.setInt(2, comment.getIssueId());
            st.setString(3, username);

            try(ResultSet rs = st.executeQuery()){
                if(rs.next()){
                    comment.setUser(userDao.findUserByUsername(username));
                    comment.setId(rs.getInt("comment_id"));
                    comment.setCreatedOn(rs.getObject("created_on", OffsetDateTime.class));
                    return true;
                }
            }
        }
        return false;
    }
}
