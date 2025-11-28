package it.unina.backend.daointerface;

import it.unina.backend.entity.Change;

import java.sql.SQLException;
import java.util.List;
import it.unina.backend.entity.User;
import it.unina.backend.entity.Issue;

public interface ChangeDaoInterface {

    public List<Change> findChangesByIssue(Issue issue) throws SQLException;
    public void insertChange(String action, String details, User user, Issue issue) throws SQLException;
}
