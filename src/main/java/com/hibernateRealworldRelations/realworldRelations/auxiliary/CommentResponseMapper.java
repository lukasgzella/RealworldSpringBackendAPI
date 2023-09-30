package com.hibernateRealworldRelations.realworldRelations.auxiliary;

import com.hibernateRealworldRelations.realworldRelations.ArticleService;
import com.hibernateRealworldRelations.realworldRelations.dto.ArticleResponse;
import com.hibernateRealworldRelations.realworldRelations.dto.Author;
import com.hibernateRealworldRelations.realworldRelations.dto.CommentResponse;
import com.hibernateRealworldRelations.realworldRelations.entity.Comment;
import com.hibernateRealworldRelations.realworldRelations.entity.Tag;
import com.hibernateRealworldRelations.realworldRelations.entity.User;
import com.hibernateRealworldRelations.realworldRelations.repository.FollowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.function.Function;


@Service
@RequiredArgsConstructor
public class CommentResponseMapper implements Function<Comment, CommentResponse> {

    private final FollowerRepository followerRepository;

    @Override
    public CommentResponse apply(Comment comment, authenticatedUser User) {
        return CommentResponse.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .body(comment.getBody())
                .author(new Author(
                        comment.getAuthor().getUsername(),
                        comment.getAuthor().getBio(),
                        comment.getAuthor().getImage(),
                        followerRepository.existsByFromTo(authenticatedUser.getUsername(), comment.getAuthor().getUsername())
                        ))
                .build();
    }
}
