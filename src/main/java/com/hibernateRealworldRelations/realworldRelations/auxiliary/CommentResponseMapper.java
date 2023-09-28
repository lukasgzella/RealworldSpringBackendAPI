package com.hibernateRealworldRelations.realworldRelations.auxiliary;

import com.hibernateRealworldRelations.realworldRelations.dto.ArticleResponse;
import com.hibernateRealworldRelations.realworldRelations.dto.Author;
import com.hibernateRealworldRelations.realworldRelations.dto.CommentResponse;
import com.hibernateRealworldRelations.realworldRelations.entity.Comment;
import com.hibernateRealworldRelations.realworldRelations.entity.Tag;

import java.util.function.Function;

public class CommentResponseMapper implements Function<Comment, CommentResponse> {
    @Override
    public CommentResponse apply(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .body(comment.getBody())
                .author(new Author(
                        comment.getAuthor().getUsername(),
                        comment.getAuthor().getBio(),
                        comment.getAuthor().getImage(),
                        comment.getArticle().isFollowing()
                        ))
                .build();
    }
}
