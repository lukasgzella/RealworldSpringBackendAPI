package com.hibernateRealworldRelations.realworldRelations.dto.requests;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonRootName("article")
public class ArticleUpdateRequest {

    private String title;
    private String description;
    private String body;

}