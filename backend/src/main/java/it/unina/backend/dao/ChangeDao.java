package it.unina.backend.dao;

import it.unina.backend.daointerface.ChangeDaoInterface;
import it.unina.backend.entity.Change;
import it.unina.backend.entity.User;
import it.unina.backend.entity.Issue;
import it.unina.backend.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChangeDao implements ChangeDaoInterface {

    private static ChangeDao instance;

    private ChangeDao() {}

    public static ChangeDao getInstance() {
        if (instance == null) {
            instance = new ChangeDao();
        }
        return instance;
    }

    public List<Change> findChangesByIssue(Issue issue) throws SQLException{
        String sql = "SELECT * FROM change JOIN \"User\" AS u ON u.username = change.created_by WHERE related_to = ?";
        List<Change> changes = new ArrayList<Change>();

        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, issue.getId());

            ResultSet rs = st.executeQuery();

            while(rs.next()) {
                OffsetDateTime madeOn = rs.getTimestamp("made_on")
                        .toLocalDateTime()
                        .atOffset(java.time.ZoneOffset.UTC);

                OffsetDateTime createdOn = rs.getTimestamp("created_on")
                        .toLocalDateTime()
                        .atOffset(java.time.ZoneOffset.UTC);

                changes.add(new Change(
                        rs.getInt("change_id"),
                        rs.getString("action"),
                        rs.getString("details"),
                        madeOn,
                        new User(rs.getString(
                                "email"),
                                rs.getString("username"),
                                rs.getString("passwordHash"),
                                rs.getString("name"),
                                rs.getString("surname"),
                                rs.getString("role"),
                                createdOn),
                        issue
                ));
            }
            return changes;
        }
    }

    public void insertChange(String action, String details, User user, Issue issue) throws SQLException{

        String sql = "INSERT INTO Change (action, details, created_by, related_to) VALUES (?, ?, ?, ?)";

        try(Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement st = connection.prepareStatement(sql)){
            st.setString(1, action);
            st.setString(2, details);
            st.setString(3, user.getUsername());
            st.setInt(4, issue.getId());
            st.executeUpdate();
        }
    }
}
