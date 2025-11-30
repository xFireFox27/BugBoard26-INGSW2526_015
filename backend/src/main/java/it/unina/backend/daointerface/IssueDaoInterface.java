package it.unina.backend.daointerface;

import it.unina.backend.entity.Issue;

import java.sql.SQLException;

public interface IssueDaoInterface {
    public boolean insertIssue(Issue issue) throws SQLException;

    public Issue findIssueById(Integer id) throws SQLException;
}
