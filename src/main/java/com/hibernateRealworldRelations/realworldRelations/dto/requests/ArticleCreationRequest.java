package com.hibernateRealworldRelations.realworldRelations.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCreationRequest {

    private String title;
    private String description;
    private String body;
    private List<String> tagList;

}