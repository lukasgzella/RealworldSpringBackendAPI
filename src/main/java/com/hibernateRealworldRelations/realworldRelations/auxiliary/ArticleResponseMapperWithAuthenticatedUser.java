package com.hibernateRealworldRelations.realworldRelations.auxiliary;

import com.hibernateRealworldRelations.realworldRelations.dto.Author;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.ArticleResponse;
import com.hibernateRealworldRelations.realworldRelations.entity.Article;
import com.hibernateRealworldRelations.realworldRelations.entity.Tag;
import com.hibernateRealworldRelations.realworldRelations.entity.User;
import com.hibernateRealworldRelations.realworldRelations.repository.FollowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class ArticleResponseMapperWithAuthenticatedUser implements BiFunction<User, Article, ArticleResponse> {

    private final FollowerRepository followerRepository;

    @Override
    public ArticleResponse apply(User user, Article article) {
        return ArticleResponse.builder()
                .slug(article.getSlug())
                .title(article.getTitle())
                .description(article.getDescription())
                .body(article.getBody())
                .tagList(article.getTagList().stream().map(Tag::getName).toList())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())

                .favorited(isFavorited(article, user))

                .favoritesCount(article.getFollowingUsers().size()) // followingUsers Hashset.size()
                .author(new Author(
                        article.getAuthor().getUsernameDB(),
                        article.getAuthor().getBio(),
                        article.getAuthor().getImage(),

                        isFollowing(user, article.getAuthor())

                ))
                .build();
    }
    private boolean isFavorited(Article article, User authenticated) {
        return authenticated.getFavoriteArticles().contains(article);
    }

    private boolean isFollowing(User userFrom, User userTo) {
        return followerRepository.existsByFromTo(userFrom.getUsernameDB(), userTo.getUsernameDB());
    }
}