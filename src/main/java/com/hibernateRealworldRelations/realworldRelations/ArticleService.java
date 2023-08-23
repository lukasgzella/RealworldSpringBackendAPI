package com.hibernateRealworldRelations.realworldRelations;

import com.hibernateRealworldRelations.realworldRelations.entity.Article;
import com.hibernateRealworldRelations.realworldRelations.entity.Comment;
import com.hibernateRealworldRelations.realworldRelations.entity.Tag;
import com.hibernateRealworldRelations.realworldRelations.entity.User;
import com.hibernateRealworldRelations.realworldRelations.repository.ArticleRepository;
import com.hibernateRealworldRelations.realworldRelations.repository.CommentRepository;
import com.hibernateRealworldRelations.realworldRelations.repository.TagRepository;
import com.hibernateRealworldRelations.realworldRelations.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;

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
                case "e":
                    break LOOP;
            }
        }
    }

    public void feedArticles() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Limit? ");
        int limit = Integer.parseInt(scanner.nextLine());
        System.out.println("Offset? ");
        int offset = Integer.parseInt(scanner.nextLine());
        System.out.println("Current user_id? ");
        String user_id = scanner.nextLine();
        List<Article> articles = articleRepository.findByFollowingUser(user_id, PageRequest.of(offset, limit));
        articles.forEach(article -> System.out.println("Article{" +
                "id=" + article.getId()
        ));

    }

    private void getArticlePageByManyParams(String author, String tag, String name, int limit, int offset) {
        Page<Article> page = articleRepository.findArticlesByParamsPage(author, tag, name, PageRequest.of(offset, limit));
        long articlesCount = page.getTotalElements();
        page.forEach(article -> System.out.println("Article{" +
                "id=" + article.getId() +
                "articlesCount=" + articlesCount
        ));
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
        Scanner scanner = new Scanner(System.in);
        System.out.println("Current user_id? ");
        String userId = scanner.nextLine();
        User user = userRepository.findById(Long.parseLong(userId));
        System.out.println("title?");
        String title = scanner.nextLine();
        // in spec: title, description, body, optional taglist
        // description, body omitted for shortening

        // get tag names from user
        System.out.println("taglist? Enter tags separated by spaces");
        List<String> stringList = Arrays.stream(scanner.nextLine().split("\s")).toList();
        // create new article with id
        Article savedArticle = articleRepository.save(Article.builder()
                .author(user)
                .title(title)
                .slug(title.toLowerCase().replace(' ','-'))
                .build());
        // check if there are existing tags with name from stringList in tagRepository
        Set<Tag> existingTags = stringList
                .stream()
                .map(s -> tagRepository.findByName(s)
                        .orElseGet(() -> new Tag(s))).collect(Collectors.toSet());
        // update tags with savedArticle
        existingTags.forEach(tag -> tag.getArticles().add(savedArticle));
        existingTags = existingTags.stream().map(tagRepository::save).collect(Collectors.toSet());
        savedArticle.setTagList(existingTags);
        articleRepository.save(savedArticle);
    }

    public void getArticle() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter slug? GET /api/articles/:slug");
        String slug = scanner.nextLine();
        Article article = articleRepository.findBySlug(slug).orElseThrow();
        System.out.println("Article{" +
                "id=" + article.getId() +
                ", title=" + article.getTitle() +
                ", slug=" + article.getSlug() +
                '}');
    }

    public void updateArticle() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter slug? PUT /api/articles/:slug");
        String slug = scanner.nextLine();
        Article article = articleRepository.findBySlug(slug).orElseThrow();
        System.out.println("Enter title to change?");
        // in spec optional title, description or body
        String title = scanner.nextLine();
        article.setTitle(title);
        // DRY principle violation
        article.setSlug(title.toLowerCase().replace(' ','-'));
        article = articleRepository.save(article);
        System.out.println("Article{" +
                "id=" + article.getId() +
                ", title=" + article.getTitle() +
                ", slug=" + article.getSlug() +
                '}');
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
        System.out.println("comment with article_id: " + article.getId() + " added to repository");
        System.out.println(comment);
    }
}