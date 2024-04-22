package com.hibernateRealworldRelations.realworldRelations.api;

import com.hibernateRealworldRelations.realworldRelations.API.services.ArticleServiceHTTP;
import com.hibernateRealworldRelations.realworldRelations.auxiliary.ArticleResponseMapper;
import com.hibernateRealworldRelations.realworldRelations.auxiliary.ArticleResponseMapperWithAuthenticatedUser;
import com.hibernateRealworldRelations.realworldRelations.auxiliary.AuthenticationFacade;
import com.hibernateRealworldRelations.realworldRelations.auxiliary.CommentResponseMapperWithAuthenticatedUser;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.RegistrationRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.ArticleResponse;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.LoginResponse;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.MultipleArticleResponse;
import com.hibernateRealworldRelations.realworldRelations.entity.Article;
import com.hibernateRealworldRelations.realworldRelations.entity.Role;
import com.hibernateRealworldRelations.realworldRelations.entity.User;
import com.hibernateRealworldRelations.realworldRelations.repository.ArticleRepository;
import com.hibernateRealworldRelations.realworldRelations.repository.CommentRepository;
import com.hibernateRealworldRelations.realworldRelations.repository.TagRepository;
import com.hibernateRealworldRelations.realworldRelations.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ArticleServiceHTTPTest {

    private AuthenticationFacade authenticationFacade;
    private ArticleRepository articleRepository;
    private UserRepository userRepository;
    private TagRepository tagRepository;
    private CommentRepository commentRepository;
    private ArticleResponseMapperWithAuthenticatedUser articleResponseMapperWithAuthenticatedUser;
    private CommentResponseMapperWithAuthenticatedUser commentResponseMapperWithAuthenticatedUser;
    private ArticleServiceHTTP articleServiceHTTP;

    @BeforeEach
    public void mockFields() {
        authenticationFacade = mock(AuthenticationFacade.class);
        articleRepository = mock(ArticleRepository.class);
        userRepository = mock(UserRepository.class);
        tagRepository = mock(TagRepository.class);
        commentRepository = mock(CommentRepository.class);
        articleResponseMapperWithAuthenticatedUser = mock(ArticleResponseMapperWithAuthenticatedUser.class);
        commentResponseMapperWithAuthenticatedUser = mock(CommentResponseMapperWithAuthenticatedUser.class);
        articleServiceHTTP = new ArticleServiceHTTP(
                authenticationFacade,
                articleRepository,
                userRepository,
                tagRepository,
                commentRepository,
                articleResponseMapperWithAuthenticatedUser,
                commentResponseMapperWithAuthenticatedUser
        );
    }

    private List<Article> initArticles() {
        return List.of(
                Article.builder()
                        .author(new User())
                        .title("title 1")
                        .slug("title-1")
                        .description("desc 1")
                        .body("body 1")
                        .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")))
                        .build(),
                Article.builder()
                        .author(new User())
                        .title("title 2")
                        .slug("title-2")
                        .description("desc 2")
                        .body("body 2")
                        .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")))
                        .build(),
                Article.builder()
                        .author(new User())
                        .title("title 3")
                        .slug("title-3")
                        .description("desc 3")
                        .body("body 3")
                        .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")))
                        .build()
        );
    }



    @Test
    public void getArticles_allParamsOk_returnsMultipleArticleResponse() {
        //given
        String tag = "";
        String author = "";
        String favorited = "";
        int limit = 1;
        int offset = 1;

        List<ArticleResponse> articleResponses = new ArrayList<>();
        long articlesCount = 1;

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

        MultipleArticleResponse expected =
        when(userRepository.save(user)).thenReturn(user);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateToken(user)).thenReturn("generatedToken");
        //when
        LoginResponse actual = userService.registerUser(request);
        //then
        assertEquals(expected, actual);
        verify(userRepository).save(user);
    }
}
