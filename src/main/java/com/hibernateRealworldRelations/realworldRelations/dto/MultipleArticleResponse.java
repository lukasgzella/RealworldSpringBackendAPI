package com.hibernateRealworldRelations.realworldRelations.dto;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class MultipleArticleResponse {

    private List<ArticleResponse> articles;
    private long articlesCount;

}