package it.unina.backend.daointerface;

import it.unina.backend.entity.Issue;

import java.sql.Connection;
import java.util.List;

import java.sql.SQLException;

public interface IssueDaoInterface {
    boolean insertIssue(Connection connection, Issue issue) throws SQLException;

    Issue findIssueById(Integer id) throws SQLException;

    public List<Issue> findAllIssues() throws SQLException;
}
