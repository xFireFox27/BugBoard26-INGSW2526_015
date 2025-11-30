package it.unina.backend.dao;

import it.unina.backend.daointerface.IssueDaoInterface;
import it.unina.backend.entity.Issue;
import it.unina.backend.connection.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class IssueDao implements IssueDaoInterface {
    private static IssueDao instance;

    private IssueDao() {
    }

    public static IssueDao getInstance() {
        if (instance == null) {
            instance = new IssueDao();
        }
        return instance;
    }

    @Override
    public boolean insertIssue(Issue issue) {
        String sql = "INSERT INTO issue (issue_id, title, description, type, created_by) values (?, ?, ?, ?, ?)";

        try (
                Connection connection = DatabaseConnection.getInstance().getConnection();
                PreparedStatement st = connection.prepareStatement(sql)
        ) {
            st.setInt(1, issue.getId());
            st.setString(2, issue.getTitle());
            st.setString(3, issue.getDescription());
            st.setString(4, issue.getType());
            st.setString(5, issue.getCreatedBy());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public Issue findIssueById(Integer id) {
        String sql = "SELECT * FROM issue WHERE issue_id = ?";

        try (
                Connection connection = DatabaseConnection.getInstance().getConnection();
                PreparedStatement st = connection.prepareStatement(sql)
        ) {
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new Issue(
                    rs.getInt("issue_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("type"),
                    rs.getString("status"),
                    rs.getString("created_by"),
                    rs.getObject("created_on", OffsetDateTime.class)
                );
            } else return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Issue> findAllIssues() {
        String sql = "SELECT * from issue";
        List<Issue> issues = new ArrayList<>();

        try(
                Connection connection = DatabaseConnection.getInstance().getConnection();
                PreparedStatement st = connection.prepareStatement(sql)
        ) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                issues.add(new Issue(
                    rs.getInt("issue_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("type"),
                    rs.getString("status"),
                    rs.getString("created_by"),
                    rs.getObject("created_on", OffsetDateTime.class)
                ));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return issues;
    }
}
