package com.hibernateRealworldRelations.realworldRelations.API.services;

import com.hibernateRealworldRelations.realworldRelations.auxiliary.ArticleResponseMapper;
import com.hibernateRealworldRelations.realworldRelations.auxiliary.ArticleResponseMapperWithAuthenticatedUser;
import com.hibernateRealworldRelations.realworldRelations.auxiliary.AuthenticationFacade;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.ArticleCreationRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.ArticleResponse;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.MultipleArticleResponse;
import com.hibernateRealworldRelations.realworldRelations.entity.Article;
import com.hibernateRealworldRelations.realworldRelations.entity.Tag;
import com.hibernateRealworldRelations.realworldRelations.entity.User;
import com.hibernateRealworldRelations.realworldRelations.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceHTTP {

    private final AuthenticationFacade authenticationFacade;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;
    private final ArticleResponseMapperWithAuthenticatedUser articleResponseMapperWithAuthenticatedUser;

    public MultipleArticleResponse getArticles(String tag, String author, String favorited, int limit, int offset) {

        List<ArticleResponse> articleResponses = new ArrayList<>();
        long articlesCount = articleRepository.countArticlesByParams(author, tag, favorited);

        Page<Article> page = articleRepository
                .findArticlesByParamsPageOrderedByMostRecentFirst(author, tag, favorited, PageRequest.of(offset, limit));

        User authenticated = checkIfAuthenticated();
        if (authenticated != null) {
            articleResponses = page.map(
                    article -> articleResponseMapperWithAuthenticatedUser.apply(authenticated, article))
                    .toList();
        } else {
            articleResponses = page.map(article -> new ArticleResponseMapper().apply(article)).toList();
        }
        return new MultipleArticleResponse(articleResponses, articlesCount);
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
        if (authenticated != null) {
            return articleResponseMapperWithAuthenticatedUser.apply(authenticated, article);
        }
        return new ArticleResponseMapper().apply(article);
    }

    public ArticleResponse createArticle(ArticleCreationRequest request) {
        User user = checkIfAuthenticated();
        List<String> tagList = request.getTagList();
        // create new article with id
        Article savedArticle = articleRepository.save(Article.builder()
                .author(user)
                .title(request.getTitle())
                .slug(request.getTitle().toLowerCase().replace(' ', '-'))
                .description(request.getDescription())
                .body(request.getBody())
                .createdAt(LocalDateTime.now().toString())
                .build());
        // check if there are existing tags with name from stringList in tagRepository
        Set<Tag> existingTags = tagList
                .stream()
                .map(s -> tagRepository.findByName(s)
                        .orElseGet(() -> new Tag(s)))
                .collect(Collectors.toSet());
        // update tags with savedArticle
        existingTags.forEach(tag -> tag.getArticles().add(savedArticle));
        existingTags = existingTags.stream().map(tagRepository::save).collect(Collectors.toSet());

        savedArticle.setTagList(existingTags);
        articleRepository.save(savedArticle);
        ArticleResponse res = new ArticleResponseMapper().apply(savedArticle);
        System.out.println(res);
        return res;
    }







    private User checkIfAuthenticated() {
        String userEmail = authenticationFacade.getAuthentication().getName();
        return userRepository.findByEmail(userEmail).orElse(null);
    }
}