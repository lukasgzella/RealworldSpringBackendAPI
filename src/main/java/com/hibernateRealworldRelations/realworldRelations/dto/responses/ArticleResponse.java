package com.hibernateRealworldRelations.realworldRelations.dto.responses;

import com.hibernateRealworldRelations.realworldRelations.dto.Author;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleResponse {

    private String slug;
    private String title;
    private String description;
    private String body;
    private List<String> tagList;
    private String createdAt;
    private String updatedAt;
    private boolean favorited;
    private int favoritesCount;
    private Author author;

    @Override
    public String toString() {
        return "ArticleResponse{" +
                "slug='" + slug + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", body='" + body + '\'' +
                ", tagList=" + tagList +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", favorited=" + favorited +
                ", favoritesCount=" + favoritesCount +
                ", author=" + author +
                '}';
    }
}