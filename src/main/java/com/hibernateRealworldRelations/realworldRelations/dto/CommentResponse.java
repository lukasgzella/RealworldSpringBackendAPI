package com.hibernateRealworldRelations.realworldRelations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {

    private long id;
    private String createdAt;
    private String updatedAt;
    private String body;
    private Author author;

}