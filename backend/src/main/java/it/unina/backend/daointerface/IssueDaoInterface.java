package it.unina.backend.daointerface;

import it.unina.backend.entity.Issue;

public interface IssueDaoInterface {
    public boolean insertIssue(Issue issue);

    public Issue findIssueById(Integer id);
}
