package com.hibernateRealworldRelations.realworldRelations.API.services;

import com.hibernateRealworldRelations.realworldRelations.auxiliary.*;
import com.hibernateRealworldRelations.realworldRelations.dto.Author;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.ArticleCreationRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.ArticleUpdateRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.CommentCreationRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.*;
import com.hibernateRealworldRelations.realworldRelations.entity.Article;
import com.hibernateRealworldRelations.realworldRelations.entity.Comment;
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
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ArticleServiceHTTP {

    private final AuthenticationFacade authenticationFacade;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;
    private final ArticleResponseMapperWithAuthenticatedUser articleResponseMapperWithAuthenticatedUser;
    private final CommentResponseMapperWithAuthenticatedUser commentResponseMapperWithAuthenticatedUser;

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
        User authenticatedUser = checkIfAuthenticated();
        String user_id = authenticatedUser.getId().toString();
        Page<Article> articles = articleRepository.findArticlesFromFavoritesUsers(user_id, PageRequest.of(offset, limit));

        List<ArticleResponse> articleResponses = articles.stream().
                map(article -> articleResponseMapperWithAuthenticatedUser.apply(authenticatedUser, article))
                .toList();
        long articlesCount = articleRepository.countArticlesFromFavoritesUsers(user_id);
        return new MultipleArticleResponse(articleResponses, articlesCount);
    }

    public ArticleResponse getArticle(String slug) {
        Article article = articleRepository.findBySlug(slug).orElseThrow();
        User authenticated = checkIfAuthenticated();
        if (authenticated == null) {
            return new ArticleResponseMapper().apply(article);
        }
        String emailFrom = authenticated.getEmail();
        System.out.println(emailFrom);
        if (!emailFrom.equals("anonymousUser")) {
            return articleResponseMapperWithAuthenticatedUser.apply(authenticated, article);
        }
        return new ArticleResponseMapper().apply(article);
    }

    public ArticleResponse createArticle(ArticleCreationRequest request) {
        User authenticated = checkIfAuthenticated();
        List<String> tagList = request.getTagList();
        // create new article with id
        Article savedArticle = articleRepository.save(Article.builder()
                .author(authenticated)
                .title(request.getTitle())
                .slug(request.getTitle().toLowerCase().replace(' ', '-'))
                .description(request.getDescription())
                .body(request.getBody())
                .createdAt(LocalDateTime.now().toString())
                .build());
        if (tagList == null) {
            return new ArticleResponseMapper().apply(savedArticle);
        }
        // check if there are existing tags with name from stringList in tagRepository
        Set<Tag> existingTags = tagList
                .stream()
                .map(string -> tagRepository.findByName(string)
                        .orElseGet(() -> new Tag(string)))
                .collect(Collectors.toSet());
        // update tags with savedArticle
        existingTags.forEach(tag -> tag.getArticles().add(savedArticle));
        existingTags = existingTags.stream().map(tagRepository::save).collect(Collectors.toSet());

        savedArticle.setTagList(existingTags);
        articleRepository.save(savedArticle);
        return new ArticleResponseMapper().apply(savedArticle);
    }

    public ArticleResponse updateArticle(String slug, ArticleUpdateRequest request) {
        Article article = articleRepository.findBySlug(slug).orElseThrow();
        if (request.getTitle() != null) {
            article.setTitle(request.getTitle());
            article.setSlug(request.getTitle().toLowerCase().replace(' ', '-'));
        }
        if (request.getDescription() != null) {
            article.setDescription(request.getDescription());
        }
        if (request.getBody() != null) {
            article.setBody(request.getBody());
        }

        article.setUpdatedAt(LocalDateTime.now().toString());
        article = articleRepository.save(article);
        return new ArticleResponseMapper().apply(article);
    }

    public void deleteArticle(String slug) {
        Article article = articleRepository.findBySlug(slug).orElseThrow();
        articleRepository.delete(article);
        System.out.println("Article deleted");
    }

    @Transactional
    public CommentResponse addCommentsToAnArticle(String slug, CommentCreationRequest request) {
        // todo fix isFollowing in CommentResponseMapper
        Article article = articleRepository.findBySlug(slug).orElseThrow();

        User author = checkIfAuthenticated();
        Comment comment = Comment.builder()
                .author(author)
                .article(article)
                .createdAt(LocalDateTime.now().toString())
                .body(request.getBody())
                .build();
        comment = commentRepository.save(comment);
        List<Comment> comments = article.getComments();
        List<Comment> userComments = author.getComments();
        comments.add(comment);
        userComments.add(comment);
        article.setComments(comments);
        author.setComments(userComments);

        articleRepository.save(article);
        userRepository.save(author);

        return commentResponseMapperWithAuthenticatedUser.apply(author, comment);
    }

    public MultipleCommentResponse getCommentsFromAnArticle(String slug) {
        Article article = articleRepository.findBySlugWithComments(slug).orElseThrow();
        List<Comment> comments = article.getComments();


        List<CommentResponse> commentResponses = new ArrayList<>();

        User authenticated = checkIfAuthenticated();
        if (authenticated == null) {
            commentResponses = comments.stream()
                    .map(c -> new CommentResponseMapper().apply(c))
                    .toList();
        } else {
            for (Comment comment : comments) {
                CommentResponse commentResponse = commentResponseMapperWithAuthenticatedUser.apply(authenticated, comment);
                commentResponses.add(commentResponse);
            }
        }

        return new MultipleCommentResponse(commentResponses);
    }

    @Transactional
    public void deleteComment(String slug, String id) {
        long commentId = Integer.parseInt(id);
        Article article = articleRepository.findBySlug(slug).orElseThrow();
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        User author = userRepository.findById(comment.getAuthor().getId()).orElseThrow();

        User authenticated = checkIfAuthenticated();
        if (authenticated != null && authenticated.equals(comment.getAuthor())) {
            List<Comment> comments = article.getComments();
            List<Comment> userComments = author.getComments();
            comments.remove(comment);
            userComments.remove(comment);
            article.setComments(comments);
            author.setComments(userComments);

            articleRepository.save(article);
            userRepository.save(author);
            commentRepository.delete(comment);
            System.out.println("comment deleted");
        }
    }

    @Transactional
    public ArticleResponse favoriteArticle(String slug) {
        User authenticated = checkIfAuthenticated();
        Article article = articleRepository.findBySlug(slug).orElseThrow();

        Set<Article> favoritesArticles = authenticated.getFavoriteArticles();
        Set<User> followingUsers = article.getFollowingUsers();
        favoritesArticles.add(article);
        followingUsers.add(authenticated);
        article.setFollowingUsers(followingUsers);
        authenticated.setFavoriteArticles(favoritesArticles);

        article = articleRepository.save(article);
        userRepository.save(authenticated);

        return articleResponseMapperWithAuthenticatedUser.apply(authenticated, article);
    }

    @Transactional
    public ArticleResponse unfavoriteArticle(String slug) {
        // auth required and no additional parameters required
        User authenticated = checkIfAuthenticated();
        Article article = articleRepository.findBySlug(slug).orElseThrow();

        Set<Article> favoritesArticles = authenticated.getFavoriteArticles();
        Set<User> followingUsers = article.getFollowingUsers();
        favoritesArticles.remove(article);
        followingUsers.remove(authenticated);
        article.setFollowingUsers(followingUsers);
        authenticated.setFavoriteArticles(favoritesArticles);

        article = articleRepository.save(article);
        userRepository.save(authenticated);

        return articleResponseMapperWithAuthenticatedUser.apply(authenticated, article);
    }

    public MultipleTagResponse getTags() {
        Iterable<Tag> tags = tagRepository.findAll();
        List<String> tagList = StreamSupport.stream(tags.spliterator(), false)
                .map(Tag::getName).toList();
        return new MultipleTagResponse(tagList);
    }


    private User checkIfAuthenticated() {
        String userEmail = authenticationFacade.getAuthentication().getName();
        return userRepository.findByEmail(userEmail).orElse(null);
    }
}