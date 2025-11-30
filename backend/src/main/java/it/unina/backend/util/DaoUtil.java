package it.unina.backend.util;

import it.unina.backend.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class DaoUtil {

    private DaoUtil(){};

    public static OffsetDateTime getTimestamp(ResultSet row, String column) throws SQLException {
        return row.getTimestamp(column)
                .toLocalDateTime()
                .atOffset(java.time.ZoneOffset.UTC);
    }

    public static User createUserFromResultSet(ResultSet rs) throws SQLException{

        OffsetDateTime createdOn = getTimestamp(rs, "created_on");

        return new User(
                rs.getString("email"),
                rs.getString("username"),
                null,
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("role"),
                createdOn
        );
    }
}
