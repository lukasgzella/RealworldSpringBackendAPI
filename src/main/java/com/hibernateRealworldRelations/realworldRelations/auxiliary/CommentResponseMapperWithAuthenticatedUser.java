package com.hibernateRealworldRelations.realworldRelations.auxiliary;

import com.hibernateRealworldRelations.realworldRelations.dto.Author;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.CommentResponse;
import com.hibernateRealworldRelations.realworldRelations.entity.Comment;
import com.hibernateRealworldRelations.realworldRelations.entity.User;
import com.hibernateRealworldRelations.realworldRelations.repository.FollowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class CommentResponseMapperWithAuthenticatedUser implements BiFunction<User, Comment, CommentResponse> {

    private final FollowerRepository followerRepository;

    @Override
    public CommentResponse apply(User user, Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .body(comment.getBody())
                .author(new Author(
                        comment.getAuthor().getUsername(),
                        comment.getAuthor().getBio(),
                        comment.getAuthor().getImage(),
                        isFollowing(user, comment.getAuthor())
                ))
                .build();
    }

    private boolean isFollowing(User userFrom, User userTo) {
        return followerRepository.existsByFromTo(userFrom.getUsernameDB(), userTo.getUsernameDB());
    }
}