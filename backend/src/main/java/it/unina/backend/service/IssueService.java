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

    public List<Issue> getIssues() {
        try {
            return issueDao.findAllIssues();
        }
        catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public List<Issue> getIssuesFilteredAndSorted(String filter, String sortBy) {
        List<Issue> issues = getIssues();
        issues = applyFiltering(issues, filter);
        issues = applySorting(issues, sortBy);
        return issues;
    }

    public List<Issue> applyFiltering(List<Issue> issues, String filter) {
        if (filter != null && !filter.isEmpty()) {
            issues = issues.stream()
                     .filter(i -> i.getStatus().equalsIgnoreCase(filter))
                    .collect(Collectors.toCollection(java.util.ArrayList::new))
            ;
        }
        return issues;
    }

    public List<Issue> applySorting(List<Issue> issues, String sortBy) {
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
