package it.unina.backend.daointerface;

import it.unina.backend.entity.Attachment;
import java.sql.SQLException;
import java.util.List;

public interface AttachmentDaoInterface {
    public boolean insertAttachment(Attachment attachment) throws SQLException;
    public Attachment findAttachmentById(int id) throws SQLException;
    List<Attachment> findAttachmentsByRelatedId(int relatedId) throws SQLException;
}
