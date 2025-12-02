package it.unina.backend.service;

import it.unina.backend.connection.DatabaseConnection;
import it.unina.backend.dao.ChangeDao;
import it.unina.backend.dao.IssueDao;
import it.unina.backend.entity.Change;
import it.unina.backend.entity.Issue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IssueService {
    private static IssueService instance;
    private final IssueDao issueDao = IssueDao.getInstance();
    private final ChangeDao changeDao = ChangeDao.getInstance();
    private final DatabaseConnection databaseConnection = DatabaseConnection.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(IssueService.class);


    private IssueService() {}

    public static IssueService getInstance() {
        if (instance == null) {
            instance = new IssueService();
        }
        return instance;
    }

    public List<Issue> getIssuesFilteredAndSorted(String status,
                                                  String type,
                                                  String sortBy) throws SQLException{
        List<Issue> issues = getIssues();
        issues = applyFiltering(issues, status, type);
        return applySorting(issues, sortBy);

    }

    private List<Issue> getIssues() throws SQLException {
        return issueDao.findAllIssues();
    }

    private List<Issue> applyFiltering(List<Issue> issues, String status, String type) {
        return issues.stream()
            .filter(i -> (status == null || status.isBlank()) || i.getStatus().equalsIgnoreCase(status))
            .filter(i -> (type == null || type.isBlank()) || i.getType().equalsIgnoreCase(type))
            .collect(Collectors.toCollection(java.util.ArrayList::new))
        ;
    }

    private List<Issue> applySorting(List<Issue> issues, String sortBy) {
        String sortKey = (sortBy != null && !sortBy.isBlank()) ? sortBy.toLowerCase() : "id";
        Comparator<Issue> comparator = switch (sortKey) {
            case "title" -> Comparator.comparing(Issue::getTitle);
            case "creation time" -> Comparator.comparing(Issue::getCreatedOn).reversed();
            default -> Comparator.comparing(Issue::getId);
        };
        issues.sort(comparator);
        return issues;
    }

    public void insertIssueWithChange(Issue issue){
        Connection connection = null;
        try{
            connection = databaseConnection.getConnection();
            connection.setAutoCommit(false);

            issueDao.insertIssue(connection, issue);
            Change change = new Change();
            change.setIssueId(issue.getId());
            change.setAction("Creazione Issue");
            change.setDetails(issue.getDescription());
            change.setCreatedBy(issue.getCreatedBy());
            changeDao.insertChange(connection, change);
            connection.commit();
        }
        catch(Exception e){
            if(connection != null){
                try{
                connection.rollback();
                logger.warn("Transaction ended in error: rollback");
                }
                catch(SQLException ex){
                    logger.error("impossible to establish connection to database while rollback", ex);                }
            }
            throw new RuntimeException("Transaction error", e);
        }
        finally{
            if(connection != null){
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                }
                catch(SQLException e){
                    logger.error("Error while closing connection to database", e);                }
            }
        }
    }
}
