package it.unina.backend.dao;

import it.unina.backend.daointerface.AttachmentDaoInterface;
import it.unina.backend.entity.Attachment;
import it.unina.backend.connection.DatabaseConnection;
import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class AttachmentDao implements AttachmentDaoInterface {

    private static final String ATTACHMENT_ID_COLUMN = "attachment_id";
    private static AttachmentDao instance;
    private AttachmentDao() {}

    public static AttachmentDao getInstance() {
        if (instance == null) {
            instance = new AttachmentDao();
        }
        return instance;
    }

    @Override
    public boolean insertAttachment(Attachment attachment) throws SQLException {
        String sql = "INSERT INTO attachment (file_name, file_url, uploaded_on, uploaded_by, related_to) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING " + ATTACHMENT_ID_COLUMN;

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement st = connection.prepareStatement(sql)) {

            st.setString(1, attachment.getFileName());
            st.setString(2, attachment.getUrl());
            st.setObject(3, attachment.getUploadedOn());
            st.setString(4, attachment.getCreatedBy());
            st.setInt(5, attachment.getRelatedTo());

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    attachment.setId(rs.getInt(ATTACHMENT_ID_COLUMN));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Attachment findAttachmentById(int id) throws SQLException {
        String sql = "SELECT attachment_id, file_name, file_url, uploaded_on, uploaded_by, related_to " +
                        "FROM attachment WHERE " + ATTACHMENT_ID_COLUMN + " = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement st = connection.prepareStatement(sql)) {

            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new Attachment(
                            rs.getInt(ATTACHMENT_ID_COLUMN),
                            rs.getString("file_name"),
                            rs.getString("file_url"),
                            rs.getObject("uploaded_on", OffsetDateTime.class),
                            rs.getString("uploaded_by"),
                            rs.getInt("related_to")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<Attachment> findAttachmentsByRelatedId(int relatedId) throws SQLException {
        List<Attachment> attachments = new ArrayList<>();
        String sql = "SELECT attachment_id, file_name, file_url, uploaded_on, uploaded_by, related_to " +
                     "FROM attachment WHERE related_to = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement st = connection.prepareStatement(sql)) {

            st.setInt(1, relatedId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    attachments.add(new Attachment(
                            rs.getInt(ATTACHMENT_ID_COLUMN),
                            rs.getString("file_name"),
                            rs.getString("file_url"),
                            rs.getObject("uploaded_on", OffsetDateTime.class),
                            rs.getString("uploaded_by"),
                            rs.getInt("related_to")
                    ));
                }
            }
        }
        return attachments;
    }
}
