package it.unina.backend.entity;

import java.time.OffsetDateTime;

public class Attachment {
    private int id;
    private final String fileName;
    private String url;
    private final OffsetDateTime uploadedOn;
    private final String createdBy;
    private final int relatedTo;

    public Attachment(int id, String fileName, String url, OffsetDateTime uploadedOn, String createdBy, int relatedTo) {
        this.id = id;
        this.fileName = fileName;
        this.url = url;
        this.uploadedOn = uploadedOn;
        this.createdBy = createdBy;
        this.relatedTo = relatedTo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUrl() {
        return url;
    }

    public void  setUrl(String url) {
        this.url = url;
    }

    public OffsetDateTime getUploadedOn() {
        return uploadedOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public int getRelatedTo() {
        return relatedTo;
    }
}
