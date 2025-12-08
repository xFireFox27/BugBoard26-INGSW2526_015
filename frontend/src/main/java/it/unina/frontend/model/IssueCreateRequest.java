package it.unina.frontend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueCreateRequest {
    private String title;
    private String description;
    private String type;
    private String priority;
    private String status;
}