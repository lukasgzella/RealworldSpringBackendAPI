package com.hibernateRealworldRelations.realworldRelations.API.controllers;

import com.hibernateRealworldRelations.realworldRelations.API.services.ArticleServiceHTTP;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.ArticleCreationRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.ArticleUpdateRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.CommentCreationRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.ArticleResponse;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.CommentResponse;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.MultipleArticleResponse;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.MultipleCommentResponse;
import com.hibernateRealworldRelations.realworldRelations.entity.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/articles")
@RestController
@RequiredArgsConstructor
public class ArticleControllerHTTP {

    private final ArticleServiceHTTP articleServiceHTTP;

    //    List Articles
    //    Authentication optional, will return multiple articles, ordered by most recent first
    @GetMapping
    public ResponseEntity<Map<String, Object>> listArticles(
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "favorited", required = false) String favorited,
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset
    ) {
        MultipleArticleResponse res = articleServiceHTTP.getArticles(tag, author, favorited, limit, offset);
        return ResponseEntity.ok(Map.of("articles", res.getArticles(), "articlesCount", res.getArticlesCount()));
    }

    //   Feed Articles
    //   Authentication required, will return multiple articles created by followed users,
    //   ordered by most recent first.
    @GetMapping("/feed")
    public ResponseEntity<MultipleArticleResponse> feedArticles(
            @RequestParam(value = "limit", required = false, defaultValue = "20") int limit,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset
    ) {
        return ResponseEntity.ok(articleServiceHTTP.feedArticles(limit, offset));
    }

    //      Get Article
    //   permitAll, will return single article
    @GetMapping("/{slug}")
    public ResponseEntity<Map<String, ArticleResponse>> getArticle(@PathVariable("slug") String slug) {
        return ResponseEntity.ok(Map.of("article", articleServiceHTTP.getArticle(slug)));
    }


    //      Create Article
    //   Authentication required, will return an Article
    @PostMapping
    public ResponseEntity<Map<String, ArticleResponse>> createArticle(
            @RequestBody ArticleCreationRequest request
    ) {
        return ResponseEntity.ok(Map.of("article", articleServiceHTTP.createArticle(request)));
    }

    //      Update Article
    //   Authentication required, will return the updated Article
    @PutMapping("/{slug}")
    public ResponseEntity<Map<String, ArticleResponse>> updateArticle(
            @PathVariable("slug") String slug,
            @RequestBody ArticleUpdateRequest request
    ) {
        return ResponseEntity.ok(Map.of("article", articleServiceHTTP.updateArticle(slug, request)));
    }

    //      Delete Article
    //  Auth required
    @DeleteMapping("/{slug}")
    public void deleteArticle(@PathVariable("slug") String slug) {
        articleServiceHTTP.deleteArticle(slug);
    }

    //  Add Comments to an Article
    //  Auth required
    @PostMapping("/{slug}/comments")
    public ResponseEntity<Map<String,CommentResponse>> addCommentsToAnArticle(
            @PathVariable("slug") String slug,
            @RequestBody CommentCreationRequest request
    ) {
        return ResponseEntity.ok(Map.of("comment", articleServiceHTTP.addCommentsToAnArticle(slug, request)));
    }

    //    Get Comments from an Article
    //  Auth optional
    @GetMapping("/{slug}/comments")
    public ResponseEntity<MultipleCommentResponse> getCommentsFromAnArticle(
            @PathVariable("slug") String slug
    ) {
        return ResponseEntity.ok(articleServiceHTTP.getCommentsFromAnArticle(slug));
    }

    //    Delete Comment
    //  Auth required
    @DeleteMapping("/{slug}/comments/{id}")
    public void deleteComment(
            @PathVariable("slug") String slug,
            @PathVariable("id") String id
    ) {
        articleServiceHTTP.deleteComment(slug, id);
    }

    //    Favorite Article
    //  Auth required
    @PostMapping("/{slug}/favorite")
    public ResponseEntity<Map<String,ArticleResponse>> favoriteArticle(
            @PathVariable("slug") String slug
    ) {
        return ResponseEntity.ok(Map.of("article", articleServiceHTTP.favoriteArticle(slug)));
    }

    //    Unfavorite Article
    //  Auth required
    @DeleteMapping("/{slug}/favorite")
    public ResponseEntity<Map<String,ArticleResponse>> unfavoriteArticle(
            @PathVariable("slug") String slug
    ) {
        return ResponseEntity.ok(Map.of("article", articleServiceHTTP.unfavoriteArticle(slug)));
    }
}