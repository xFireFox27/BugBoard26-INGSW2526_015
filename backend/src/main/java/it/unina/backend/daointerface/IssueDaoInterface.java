package it.unina.backend.daointerface;

import it.unina.backend.entity.Issue;
import java.util.List;

public interface IssueDaoInterface {
    public boolean insertIssue(Issue issue);

    public Issue findIssueById(Integer id);

    public List<Issue> findAllIssues();
}
