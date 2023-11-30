package com.hibernateRealworldRelations.realworldRelations.API;

import com.hibernateRealworldRelations.realworldRelations.auxiliary.ArticleResponseMapper;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.ArticleResponse;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.MultipleArticleResponse;
import com.hibernateRealworldRelations.realworldRelations.entity.Article;
import com.hibernateRealworldRelations.realworldRelations.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleServiceHTTP {

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


}