package it.unina.backend.daointerface;

import it.unina.backend.entity.Change;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ChangeDaoInterface {

    List<Change> findChangesByIssueId(int issueId) throws SQLException;
    boolean insertChange(Connection connection, Change change) throws SQLException;
}
