package it.unina.backend.service;

import it.unina.backend.dao.IssueDao;
import it.unina.backend.entity.Issue;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class IssueService {
    private static IssueService instance;
    private final IssueDao issueDao;

    private IssueService() {
        this.issueDao = IssueDao.getInstance();
    }

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
}
