package it.unina.backend.service;

import it.unina.backend.dao.IssueDao;
import it.unina.backend.entity.Issue;

import java.util.Comparator;
import java.util.List;

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
        return issueDao.findAllIssues();
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
                     .toList()
            ;
        }
        return issues;
    }

    public List<Issue> applySorting(List<Issue> issues, String sortBy) {
        Comparator<Issue> comparator = switch (sortBy.toLowerCase()) {
            case "title" -> Comparator.comparing(Issue::getTitle);
            case "creation time" -> Comparator.comparing(Issue::getCreatedOn).reversed();
            default -> Comparator.comparing(Issue::getId);
        };
        issues.sort(comparator);
        return issues;
    }
}
