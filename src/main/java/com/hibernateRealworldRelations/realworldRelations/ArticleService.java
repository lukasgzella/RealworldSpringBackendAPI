package com.hibernateRealworldRelations.realworldRelations;

import com.hibernateRealworldRelations.realworldRelations.entity.Article;
import com.hibernateRealworldRelations.realworldRelations.entity.Follower;
import com.hibernateRealworldRelations.realworldRelations.entity.Tag;
import com.hibernateRealworldRelations.realworldRelations.entity.User;
import com.hibernateRealworldRelations.realworldRelations.repository.ArticleRepository;
import com.hibernateRealworldRelations.realworldRelations.repository.TagRepository;
import com.hibernateRealworldRelations.realworldRelations.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

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

        System.out.println("taglist? Enter tags separated by spaces");
        List<String> stringList = Arrays.stream(scanner.nextLine().split("\s")).toList();
        Article article = Article.builder()
                .author(user)
                .title(title)
                .build();
        Article savedArticle = articleRepository.save(article);
        Set<Tag> tags = stringList.stream().map(s -> Tag.builder().article(savedArticle).name(s).build()).collect(Collectors.toSet());
        tags.forEach(tagRepository::save);
        System.out.println("tags: ");
        tags.forEach(tag -> System.out.println(tag.getId() + " " + tag.getName()));
        savedArticle.setTagList(tags);
        System.out.println(savedArticle.toString());
        // todo issue: tags are added despite of same existing tags which are related with another article
    }
}