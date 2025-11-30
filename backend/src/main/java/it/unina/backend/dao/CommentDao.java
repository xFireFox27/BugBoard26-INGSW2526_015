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

    private CommentDao(){}

    public static CommentDao getInstance(){

        if(instance == null){
            instance = new CommentDao();
        }
        return instance;
    }

    public List<Comment> findCommentsByIssueId(int issueId) throws SQLException {

        String sql = "select c.id, c.text, c.created_on," +
                "u.username, u.email, u.password_hash, u.name, u.surname, u.role, u.created_on " +
                "FROM Comment AS c JOIN \"User\" AS u ON c.writtenBy = u.username " +
                "WHERE c.related_to = ?";

        List<Comment> comments = new ArrayList<>();

        try(Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement st = connection.prepareStatement(sql)){

            st.setInt(1, issueId);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    OffsetDateTime writtenOn = getTimestamp(rs, "created_on");

                    comments.add(new Comment(
                            rs.getInt("comment_id"),
                            rs.getString("text"),
                            writtenOn,
                            issueId,
                            createUserFromResultSet(rs)
                    ));
                }
            }
            return comments;
        }
    }
}
