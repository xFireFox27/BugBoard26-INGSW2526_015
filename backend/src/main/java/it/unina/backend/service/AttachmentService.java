package it.unina.backend.service;

import it.unina.backend.dao.AttachmentDao;
import it.unina.backend.entity.Attachment;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class AttachmentService {

    private final AttachmentDao attachmentDao = AttachmentDao.getInstance();
    private static AttachmentService instance;

    private AttachmentService() {}

    public static AttachmentService getInstance() {
        if(instance == null) {
            instance = new AttachmentService();
        }
        return instance;
    }

    public Attachment createAndSaveAttachment(
            String fileName,
            String s3Url,
            String createdBy,
            int relatedTo
    ) throws SQLException {
        Attachment attachment = new Attachment(
            0,
            fileName,
            s3Url,
            OffsetDateTime.now(),
            createdBy != null ? createdBy : "system",
            relatedTo
        );

        if (!attachmentDao.insertAttachment(attachment)) {
            throw new SQLException();
        }

        return attachment;
    }
}
