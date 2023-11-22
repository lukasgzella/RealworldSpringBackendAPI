package com.hibernateRealworldRelations.realworldRelations.dto.responses;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class MultipleArticleResponse {

    private List<ArticleResponse> articles;
    private long articlesCount;

    @Override
    public String toString() {
        return "MultipleArticleResponse{" +
                "articles=" + articles.toString() +
                ", articlesCount=" + articlesCount +
                '}';
    }
}