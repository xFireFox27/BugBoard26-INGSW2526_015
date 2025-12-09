package it.unina.frontend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class Attachment {
    private int id;
    private String fileName;
    private String url;
    private OffsetDateTime uploadedOn;
    private String createdBy;
    private int relatedTo;
}