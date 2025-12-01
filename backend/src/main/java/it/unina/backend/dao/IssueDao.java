package it.unina.backend.dao;

import it.unina.backend.daointerface.IssueDaoInterface;
import it.unina.backend.entity.Issue;
import it.unina.backend.connection.DatabaseConnection;
import static it.unina.backend.util.DaoUtil.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class IssueDao implements IssueDaoInterface {
    private static IssueDao instance;

    private IssueDao() {}

    public static IssueDao getInstance() {
        if (instance == null) {
            instance = new IssueDao();
        }
        return instance;
    }

    @Override
    public boolean insertIssue(Issue issue) throws SQLException {
        String sql = "INSERT INTO issue (title, description, type, priority, created_by) " +
                    "values (?, ?, ?, ?, ?) RETURNING issue_id";

        try(Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, issue.getTitle());
            st.setString(2, issue.getDescription());
            st.setString(3, issue.getType());
            st.setString(4, issue.getUserUsername());
            try(ResultSet rs = st.executeQuery()){
                if(rs.next()){
                    issue.setId(rs.getInt("issue_id"));
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public Issue findIssueById(Integer id) throws SQLException{
        String sql = "SELECT i.issue_id, i.title, i.description, i.type, i.priority, i.status, i.created_on, " +
                "u.username, u.email, u.password_hash, u.name, u.surname, u.role, u.created_on " +
                "FROM issue AS i JOIN \"user\" AS u ON i.created_by = u.username " +
                "WHERE i.issue_id = ?";

        try(Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new Issue(
                        rs.getInt("issue_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("type"),
                        rs.getString("priority"),
                        rs.getString("status"),
                        createUserFromResultSet(rs),
                        rs.getObject("created_on", OffsetDateTime.class)
                );
            }
            return null;
        }
    }

    @Override
    public List<Issue> findAllIssues() throws SQLException{
        String sql = "SELECT i.issue_id, i.title, i.description, i.type, i.priority, i.status, i.created_on, " +
                "u.username, u.email, u.password_hash, u.name, u.surname, u.role, u.created_on " +
                "FROM issue AS i JOIN \"user\" AS u ON i.created_by = u.username";

        List<Issue> issues = new ArrayList<>();

        try(Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement st = connection.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                issues.add(new Issue(
                    rs.getInt("issue_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("type"),
                    rs.getString("priority"),
                    rs.getString("status"),
                    createUserFromResultSet(rs),
                    rs.getObject("created_on", OffsetDateTime.class)
                ));
            }
        }
        return issues;
    }
}
