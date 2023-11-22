package com.hibernateRealworldRelations.realworldRelations;

import com.hibernateRealworldRelations.realworldRelations.auxiliary.ArticleResponseMapper;
import com.hibernateRealworldRelations.realworldRelations.auxiliary.CommentResponseMapper;
import com.hibernateRealworldRelations.realworldRelations.dto.*;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.ArticleCreationRequest;
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
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;
    private final FollowerRepository followerRepository;

    public void getArticles() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("You are in article service mock, find article by: ");
        LOOP:
        while (true) {
            System.out.println("1 - author");
            System.out.println("2 - tag");
            System.out.println("3 - favorited");
            System.out.println("4 - author/tag/favorited");
            System.out.println("5 - page - author/tag/favorited/limit/offset");
            System.out.println("6 - page - author/tag/favorited/limit/offset - ordered by most recent first");

            System.out.println("e - exit");

            switch (scanner.nextLine()) {
                case "1": {
                    System.out.println("enter author name:");
                    String author = scanner.nextLine();
                    getArticleListByAuthor(author);
                    break;
                }
                case "2": {
                    System.out.println("enter tag name:");
                    String tag = scanner.nextLine();
                    getArticleListByTag(tag);
                    break;
                }
                case "3": {
                    System.out.println("enter favorited username:");
                    String name = scanner.nextLine();
                    getArticleListByfavorited(name);
                    break;
                }
                case "4": {
                    System.out.println("enter author name:");
                    String author = scanner.nextLine();
                    if (author.equals("null")) {
                        author = null;
                    }
                    System.out.println("enter tag name:");
                    String tag = scanner.nextLine();
                    if (tag.equals("null")) {
                        tag = null;
                    }
                    System.out.println("enter favorited username:");
                    String name = scanner.nextLine();
                    if (name.equals("null")) {
                        name = null;
                    }
                    getArticleListByManyParams(author, tag, name);
                    break;
                }
                case "5": {
                    System.out.println("enter author name:");
                    String author = scanner.nextLine();
                    if (author.equals("null")) {
                        author = null;
                    }
                    System.out.println("enter tag name:");
                    String tag = scanner.nextLine();
                    if (tag.equals("null")) {
                        tag = null;
                    }
                    System.out.println("enter favorited username:");
                    String name = scanner.nextLine();
                    if (name.equals("null")) {
                        name = null;
                    }
                    System.out.println("Limit? ");
                    int limit = Integer.parseInt(scanner.nextLine());
                    System.out.println("Offset? ");
                    int offset = Integer.parseInt(scanner.nextLine());
                    getArticlePageByManyParams(author, tag, name, limit, offset);
                    break;
                }
                case "6": {
                    System.out.println("enter author name:");
                    String author = scanner.nextLine();
                    if (author.equals("null")) {
                        author = null;
                    }
                    System.out.println("enter tag name:");
                    String tag = scanner.nextLine();
                    if (tag.equals("null")) {
                        tag = null;
                    }
                    System.out.println("enter favorited username:");
                    String name = scanner.nextLine();
                    if (name.equals("null")) {
                        name = null;
                    }
                    System.out.println("Limit? ");
                    int limit = Integer.parseInt(scanner.nextLine());
                    System.out.println("Offset? ");
                    int offset = Integer.parseInt(scanner.nextLine());
                    getArticlePageByManyParamsOrderedByMostRecentFirst(author, tag, name, limit, offset);
                    break;
                }
                case "e":
                    break LOOP;
            }
        }
    }

    private void getArticlePageByManyParamsOrderedByMostRecentFirst(String author, String tag, String name, int limit, int offset) {
        Page<Article> page = articleRepository.findArticlesByParamsPageOrderedByMostRecentFirst(author, tag, name, PageRequest.of(offset, limit));
        long articlesCount = articleRepository.countArticlesByParams(author, tag, name);
        page.forEach(article -> System.out.println("Article{" +
                "id=" + article.getId() +
                "articlesCount=" + articlesCount
        ));
        List<ArticleResponse> articles = page.map(article -> new ArticleResponseMapper().apply(article)).toList();
        var multi = new MultipleArticleResponse(articles, articlesCount);
        System.out.println(multi);
    }

    @Transactional
    public void feedArticles() {
//  Authentication required, will return multiple articles created by followed users, ordered by most recent first.
        Scanner scanner = new Scanner(System.in);
        System.out.println("Limit? ");
        int limit = Integer.parseInt(scanner.nextLine());
        System.out.println("Offset? ");
        int offset = Integer.parseInt(scanner.nextLine());
        System.out.println("Current user_id? ");
        String user_id = scanner.nextLine();
        Page<Article> articles = articleRepository.findArticlesFromFavoritesUsers(user_id, PageRequest.of(offset, limit));
        articles.forEach(article -> System.out.println("Article{" +
                "id=" + article.getId()
        ));
        List<ArticleResponse> articleResponses = articles.stream().
                map(article -> new ArticleResponseMapper()
                        .apply(article))
                .toList();
        long articlesCount = articleRepository.countArticlesFromFavoritesUsers(user_id);
        var multi = new MultipleArticleResponse(articleResponses, articlesCount);
        System.out.println(multi);
    }


    private void getArticlePageByManyParams(String author, String tag, String name, int limit, int offset) {
        Page<Article> page = articleRepository.findArticlesByParamsPage(author, tag, name, PageRequest.of(offset, limit));
        long articlesCount = articleRepository.countArticlesByParams(author, tag, name);
        page.forEach(article -> System.out.println("Article{" +
                "id=" + article.getId() +
                "articlesCount=" + articlesCount
        ));
        List<ArticleResponse> articles = page.map(article -> new ArticleResponseMapper().apply(article)).toList();
        var multi = new MultipleArticleResponse(articles, articlesCount);
        System.out.println(multi);
    }

    private void getArticleListByManyParams(String author, String tag, String name) {
        List<Article> articles = articleRepository.findArticlesByParams(author, tag, name);
        int articlesCount = articles.size();
        articles.forEach(article -> System.out.println("Article{" +
                "id=" + article.getId() +
                "articlesCount=" + articlesCount
        ));
    }

    private void getArticleListByfavorited(String name) {
        List<Article> articles = articleRepository.findArticlesByFavorited(name);
        int articlesCount = articles.size();
        articles.forEach(article -> System.out.println("Article{" +
                "id=" + article.getId() +
                "articlesCount=" + articlesCount
        ));
    }

    private void getArticleListByTag(String tag) {
        List<Article> articles = articleRepository.findArticlesByTag(tag);
        int articlesCount = articles.size();
        articles.forEach(article -> System.out.println("Article{" +
                "id=" + article.getId() +
                "articlesCount=" + articlesCount
        ));
    }

    public void getArticleListByAuthor(String author) {
        List<Article> articles = articleRepository.findArticlesByAuthor(author);
        int articlesCount = articles.size();
        articles.forEach(article -> System.out.println("Article{" +
                "id=" + article.getId() +
                ", tagList=" + article.getTagList().stream().map(Tag::getName).toList() +
                ", author=" + article.getAuthor().getUsername() + "}" +
                "articlesCount=" + articlesCount
        ));
    }

    public void createArticle() {
        //auth required
        Scanner scanner = new Scanner(System.in);
        System.out.println("Current user_id? ");
        String userId = scanner.nextLine();
        User user = userRepository.findById(Long.parseLong(userId));

        //article creation request - retrieving data from user
        System.out.println("title?"); //required
        String title = scanner.nextLine();
        System.out.println("description?"); //required
        String description = scanner.nextLine();
        System.out.println("body?"); //required
        String body = scanner.nextLine();
        System.out.println("taglist? y/n"); //optional
        String confirmation = scanner.nextLine();
        List<String> tagList = new ArrayList<>();
        if (confirmation.equals("y")) {
            System.out.println("Enter tags separated by spaces\"");
            tagList = Arrays.stream(scanner.nextLine().split("\s")).toList();
        }

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

    @Transactional
    public void getArticle() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter slug? GET /api/articles/:slug");
        String slug = scanner.nextLine();
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
        // instead of printing, need to apply mapping to DTOs: ArticleResponse and Author
        ArticleResponse res = new ArticleResponseMapper().apply(article);
        System.out.println(res);
    }

    public void updateArticle() {
//        Authentication required, returns the updated Article
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter slug? PUT /api/articles/:slug");
        String slug = scanner.nextLine();
        Article article = articleRepository.findBySlug(slug).orElseThrow();
        System.out.println("Enter title to change?");
        // in spec optional title, description or body
        String title = scanner.nextLine();
        article.setTitle(title);
        // DRY principle violation
        article.setSlug(title.toLowerCase().replace(' ', '-'));
        article = articleRepository.save(article);
        ArticleResponse res = new ArticleResponseMapper().apply(article);
        System.out.println(res);
    }

    public void deleteArticle() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter slug? DELETE /api/articles/:slug");
        String slug = scanner.nextLine();
        Article article = articleRepository.findBySlug(slug).orElseThrow();
        articleRepository.delete(article);
        System.out.println("Deleted article{" +
                "id=" + article.getId() +
                ", title=" + article.getTitle() +
                ", slug=" + article.getSlug() +
                '}');
    }

    @Transactional
    public void addCommentsToAnArticle() {
        // todo fix isFollowing in CommentResponseMapper
        // in spec request body with Comment
        // Auth required
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter slug? POST /api/articles/:slug/comments");
        String slug = scanner.nextLine();
        Article article = articleRepository.findBySlug(slug).orElseThrow();
        System.out.println("Enter authenticated userId:");
        int userId = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter required field: body?");
        String body = scanner.nextLine();

        User author = userRepository.findById(userId);
        Comment comment = Comment.builder().article(article).author(author).body(body).build();
        comment = commentRepository.save(comment);
        List<Comment> comments = article.getComments();
        List<Comment> userComments = author.getComments();
        comments.add(comment);
        userComments.add(comment);
        article.setComments(comments);
        author.setComments(userComments);

        articleRepository.save(article);
        userRepository.save(author);

        System.out.println(comment);
        CommentResponse res = new CommentResponseMapper().apply(comment);
        System.out.println(res);
    }

    public void getCommentsFromAnArticle() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter slug? GET /api/articles/:slug/comments");
        String slug = scanner.nextLine();
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
                boolean isFollowing = isFollowing(authenticated, comment.getAuthor());
                CommentResponse commentResponse = new CommentResponseMapper().apply(comment);
                Author author = commentResponse.getAuthor();
                author.setFollowing(isFollowing);
                commentResponse.setAuthor(author);
                commentResponses.add(commentResponse);
            }
        }

        var multi = new MultipleCommentResponse(commentResponses);
        System.out.println(multi);

    }

    @Transactional
    public void deleteComment() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter slug? DELETE /api/articles/:slug/comments/:id");
        String slug = scanner.nextLine();
        System.out.println("Enter comment_id? ");
        long commentId = Integer.parseInt(scanner.nextLine());
        Article article = articleRepository.findBySlug(slug).orElseThrow();
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        User author = userRepository.findById(comment.getAuthor().getId()).orElseThrow();

        User authenticated = checkIfAuthenticated();
        if (authenticated != null) {
            List<Comment> comments = article.getComments();
            List<Comment> userComments = author.getComments();
            comments.remove(comment);
            userComments.remove(comment);
            article.setComments(comments);
            author.setComments(userComments);

            articleRepository.save(article);
            userRepository.save(author);
            commentRepository.delete(comment);
            System.out.println("comment deleted");;
        }
    }

    @Transactional
    public void favoriteArticle() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter slug? POST /api/articles/:slug/favorite");
        String slug = scanner.nextLine();
        // auth required and no additional parameters required
        System.out.println("Enter userId (user who like article):");
        int userId = Integer.parseInt(scanner.nextLine());
        User authenticated = userRepository.findById(userId);
        Article article = articleRepository.findBySlug(slug).orElseThrow();

        Set<Article> favoritesArticles = authenticated.getFavoriteArticles();
        Set<User> followingUsers = article.getFollowingUsers();
        favoritesArticles.add(article);
        followingUsers.add(authenticated);
        article.setFollowingUsers(followingUsers);
        authenticated.setFavoriteArticles(favoritesArticles);

        article = articleRepository.save(article);
        userRepository.save(authenticated);
        ArticleResponse res = new ArticleResponseMapper().apply(article);
        System.out.println(res);
    }

    @Transactional
    public void unfavoriteArticle() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter slug? /api/articles/:slug/favorite");
        String slug = scanner.nextLine();
        // auth required and no additional parameters required
        System.out.println("Enter userId(to unlike article):");
        int userId = Integer.parseInt(scanner.nextLine());
        User user = userRepository.findById(userId);
        Article article = articleRepository.findBySlug(slug).orElseThrow();

        Set<Article> favoritesArticles = user.getFavoriteArticles();
        Set<User> followingUsers = article.getFollowingUsers();
        favoritesArticles.remove(article);
        followingUsers.remove(user);
        article.setFollowingUsers(followingUsers);
        user.setFavoriteArticles(favoritesArticles);

        article = articleRepository.save(article);
        userRepository.save(user);

        ArticleResponse res = new ArticleResponseMapper().apply(article);
        System.out.println(res);
    }

    public void getTags() {
        Iterable<Tag> tags = tagRepository.findAll();
        List<String> tagList = StreamSupport.stream(tags.spliterator(), false)
                .map(Tag::getName).toList();
        MultipleTagResponse multi = new MultipleTagResponse(tagList);
        System.out.println(multi);
    }

    private User checkIfAuthenticated() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("authenticated? y/n");
        if (scanner.nextLine().equals("y")) {
            System.out.println("Enter userId?");
            String userId = scanner.nextLine();
            return userRepository.findById(Long.parseLong(userId));
        }
        return null;
    }

    private boolean isFavorited(Article article, User authenticated) {
        return authenticated.getFavoriteArticles().contains(article);
    }

    private boolean isFollowing(User userFrom, User userTo) {
        return followerRepository.existsByFromTo(userFrom.getUsername(), userTo.getUsername());
    }
}