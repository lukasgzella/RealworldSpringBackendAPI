package com.hibernateRealworldRelations.realworldRelations;

import com.hibernateRealworldRelations.realworldRelations.auxiliary.ArticleResponseMapper;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.ArticleCreationRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.ArticleResponse;
import com.hibernateRealworldRelations.realworldRelations.entity.*;
import com.hibernateRealworldRelations.realworldRelations.exceptions.NoSuchUserException;
import com.hibernateRealworldRelations.realworldRelations.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final TagRepository tagRepository;

    public void addUser(String username) {
        User user = User.builder().username(username).build();
        User addedUser = userRepository.save(user);
        System.out.println("user with username: " + username + " added to repository");
        System.out.println(addedUser.toString());
    }

    @Transactional
    public void followUserById(long userIdFrom, long userIdTo) throws InterruptedException {
        User userFrom = userRepository.findById(userIdFrom);
        System.out.println(userFrom);
        User userTo = userRepository.findById(userIdTo);
        Follower follower = new Follower(userFrom,userTo);
        follower = followerRepository.save(follower);
        Set<Follower> following = userFrom.getFollowing();
        following.add(follower);
        Set<Follower> followers = userTo.getFollowers();
        followers.add(follower);

        userRepository.save(userFrom);
        userRepository.save(userTo);
    }


    @Transactional
    public void addArticleByUserId(long id) {
        User author = userRepository.findById(id);
        String title = "Title" + id;
        String description = "Description" + id;
        String body = "Body" + id;
        Article article = Article.builder()
                .author(author)
                .title(title)
                .slug(title.toLowerCase().replace(' ', '-'))
                .description(description)
                .body(body)
                .createdAt(LocalDateTime.now().toString())
                .build();
        article = articleRepository.save(article);
        List<Article> articles = author.getArticles();
        articles.add(article);
        author.setArticles(articles);

        userRepository.save(author);
        System.out.println("article with id: " + article.getId() + " added to repository");
        System.out.println(article);
    }

    @Transactional
    public void printUserById(long userId) {
        User author = userRepository.findById(userId);
        System.out.println(author);
    }

    @Transactional
    public void addCommentToArticle(long articleId, long userId) {
        Article article = articleRepository.findById(articleId);
        User author = userRepository.findById(userId);
        Comment comment = Comment.builder().article(article).author(author).build();
        comment = commentRepository.save(comment);
        List<Comment> comments = article.getComments();
        List<Comment> userComments = author.getComments();
        comments.add(comment);
        userComments.add(comment);
        article.setComments(comments);
        author.setComments(userComments);

        articleRepository.save(article);
        userRepository.save(author);
        System.out.println("comment with article_id: " + article.getId() + " added to repository");
        System.out.println(comment);
    }

    @Transactional
    public void printArticleById(long articleId) {
        Article article = articleRepository.findById(articleId);
        System.out.println(article);
    }

    @Transactional
    public void makeArticleFavorite(long articleId, long userId) {
        Article article = articleRepository.findById(articleId);
        User user = userRepository.findById(userId);
        Set<Article> favoritesArticles = user.getFavoriteArticles();
        Set<User> followingUsers = article.getFollowingUsers();
        favoritesArticles.add(article);
        followingUsers.add(user);
        article.setFollowingUsers(followingUsers);
        user.setFavoriteArticles(favoritesArticles);

        articleRepository.save(article);
        userRepository.save(user);
    }

    @Transactional
    public void addTagToArticle(long articleId, String tagName) {
        System.out.println("Feature temporary disabled");

//        Article article = articleRepository.findById(articleId);
//        Tag tag = Tag.builder().article(article).name(tagName).build();
//        tag = tagRepository.save(tag);
//        Set<Tag> tags = article.getTagList();
//        tags.add(tag);
//        article.setTagList(tags);
//
//        articleRepository.save(article);
    }

    public void addTestArticlesByRequest() {
        createArticleByRequest(
                "1",
                "u1t1",
                "desc1",
                "body1",
                List.of("t1","t2"));
        createArticleByRequest(
                "1",
                "u1t2",
                "desc1",
                "body1",
                List.of("t3","t4"));
        createArticleByRequest(
                "2",
                "u2t1",
                "desc1",
                "body1",
                List.of("t1","t2","t3","t4"));
        createArticleByRequest(
                "2",
                "u2t2",
                "desc1",
                "body1",
                List.of("t1","t2","t3","t4"));
        createArticleByRequest(
                "3",
                "u3t1",
                "desc1",
                "body1",
                List.of("t5","t6","t7","t8"));
        createArticleByRequest(
                "3",
                "u3t2",
                "desc1",
                "body1",
                List.of("t1","t2","t3","t4"));
        createArticleByRequest(
                "4",
                "u4t1",
                "desc1",
                "body1",
                List.of("t1","t2","t3","t4"));
        createArticleByRequest(
                "4",
                "u4t2",
                "desc1",
                "body1",
                List.of("t1","t2","t3","t4"));
        createArticleByRequest(
                "5",
                "u5t1",
                "desc1",
                "body1",
                List.of("t1","t2","t3","t4"));
        createArticleByRequest(
                "5",
                "u5t2",
                "desc1",
                "body1",
                List.of("t1","t2","t3","t4"));
        createArticleByRequest(
                "6",
                "u6t1",
                "desc1",
                "body1",
                List.of("t1","t2","t3","t4"));
        createArticleByRequest(
                "1",
                "u6t2",
                "desc1",
                "body1",
                List.of("t1","t2","t3","t4"));
        createArticleByRequest(
                "7",
                "u7t1",
                "desc1",
                "body1",
                List.of("t1","t2","t3","t4"));
        createArticleByRequest(
                "8",
                "u8t1",
                "desc1",
                "body1",
                List.of("t1","t2","t3","t4"));
        createArticleByRequest(
                "8",
                "u8t2",
                "desc1",
                "body1",
                List.of("t1","t2","t3","t4"));
        createArticleByRequest(
                "9",
                "u9t1",
                "desc1",
                "body1",
                List.of("t1","t2","t3","t4"));
        createArticleByRequest(
                "9",
                "u9t2",
                "desc1",
                "body1",
                List.of("t1","t2","t3","t4"));
        createArticleByRequest(
                "10",
                "u10t1",
                "desc1",
                "body1",
                List.of("t1","t2","t3","t4"));
        createArticleByRequest(
                "1",
                "u10t2",
                "desc1",
                "body1",
                List.of("t1","t2","t3","t4"));
    }

    private void createArticleByRequest(
            String userId, String title, String description, String body, List<String> tagList
    ) {
        Scanner scanner = new Scanner(System.in);

        User user = userRepository.findById(Long.parseLong(userId));

        //building article creation request object
        var request = ArticleCreationRequest.builder()
                .title(title)
                .description(description)
                .body(body)
                .tagList(tagList)
                .build();

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
    }

    public void addTestUsersWithArticles() {
        addUser("john"); // user_id = 1
        addUser("mike"); // user_id = 2
        addUser("bob"); // user_id = 3
        addUser("rob"); // user_id = 4
        addUser("albrecht"); // user_id = 5
        addUser("szczepan"); // user_id = 6
        addUser("steven"); // user_id = 7
        addUser("james"); // user_id = 8
        addUser("peter"); // user_id = 9
        addUser("jason"); // user_id = 10
        addArticleByUserId(1); // article_id = 1
        addArticleByUserId(2); // article_id = 2
        addArticleByUserId(3); // article_id = 3
        addArticleByUserId(4); // article_id = 4
        addArticleByUserId(5); // article_id = 5
        addArticleByUserId(6); // article_id = 6
        addArticleByUserId(7); // article_id = 7
        addArticleByUserId(8); // article_id = 8
        addArticleByUserId(9); // article_id = 9
        addArticleByUserId(10); // article_id = 10
    }

    public void findFollowers(String from, String to) {
        boolean isFollowing = followerRepository.existsByFromTo(from, to);
        System.out.println("existsBy method resulting: " + isFollowing);
        Follower follower = null;
        try {
            follower = followerRepository.findByFromTo(from, to)
                    .orElseThrow(NoSuchUserException::new);
        } catch (NoSuchUserException e) {
            System.out.println("is following: false");
        }
        System.out.println(follower);
    }

    public void select() {
        User user = userRepository.findByEmail("jake@jake.jake").orElseThrow();
        System.out.println(user.getEmail());
    }
}