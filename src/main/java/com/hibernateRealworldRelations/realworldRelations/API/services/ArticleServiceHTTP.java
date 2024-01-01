package com.hibernateRealworldRelations.realworldRelations.API.services;

import com.hibernateRealworldRelations.realworldRelations.auxiliary.ArticleResponseMapper;
import com.hibernateRealworldRelations.realworldRelations.auxiliary.IAuthenticationFacade;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.ArticleResponse;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.MultipleArticleResponse;
import com.hibernateRealworldRelations.realworldRelations.entity.Article;
import com.hibernateRealworldRelations.realworldRelations.entity.User;
import com.hibernateRealworldRelations.realworldRelations.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Scanner;

@Service
@RequiredArgsConstructor
public class ArticleServiceHTTP {

    private IAuthenticationFacade authenticationFacade;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;
    private final FollowerRepository followerRepository;

    public MultipleArticleResponse getArticles(String tag, String author, String favorited, int limit, int offset) {
        Page<Article> page = articleRepository.findArticlesByParamsPage(author, tag, favorited, PageRequest.of(offset, limit));
        long articlesCount = articleRepository.countArticlesByParams(author, tag, favorited);
        List<ArticleResponse> articles = page.map(article -> new ArticleResponseMapper().apply(article)).toList();
        return new MultipleArticleResponse(articles, articlesCount);
    }

    @Transactional
    public MultipleArticleResponse feedArticles(int limit, int offset) {
        User authenticatedUser = userRepository.findByUsername(authenticationFacade.getAuthentication().getName()).orElseThrow();
        String user_id = authenticatedUser.getId().toString();
        Page<Article> articles = articleRepository.findArticlesFromFavoritesUsers(user_id, PageRequest.of(offset, limit));
        articles.forEach(article -> System.out.println("Article{" +
                "id=" + article.getId()
        ));
        List<ArticleResponse> articleResponses = articles.stream().
                map(article -> new ArticleResponseMapper()
                        .apply(article))
                .toList();
        long articlesCount = articleRepository.countArticlesFromFavoritesUsers(user_id);
        return new MultipleArticleResponse(articleResponses, articlesCount);
    }

    public ArticleResponse getArticle(String slug) {
        Article article = articleRepository.findBySlug(slug).orElseThrow();
        User authenticated = checkIfAuthenticated();
        boolean favorited = false;
        boolean following = false;
        if (authenticated != null) {
            favorited = isFavorited(article, authenticated);
            following = isFollowing(authenticated, article.getAuthor());
        }
        article.setFavorited(favorited);
        article.setFollowing(following);
        return new ArticleResponseMapper().apply(article);
    }
    private User checkIfAuthenticated() {
        String username = authenticationFacade.getAuthentication().getName();
        return userRepository.findByUsername(username).orElse(null);
    }
    private boolean isFavorited(Article article, User authenticated) {
        return authenticated.getFavoriteArticles().contains(article);
    }

    private boolean isFollowing(User userFrom, User userTo) {
        return followerRepository.existsByFromTo(userFrom.getUsername(), userTo.getUsername());
    }
}