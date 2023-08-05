package com.hibernateRealworldRelations.realworldRelations;

import com.hibernateRealworldRelations.realworldRelations.entity.Article;
import com.hibernateRealworldRelations.realworldRelations.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Scanner;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;


    public void getArticles(int limit, int offset) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("You are in article service mock, find article by: ");
        LOOP:
        while (true) {
            System.out.println("1 - author");
            System.out.println("2 - tag");
            System.out.println("3 - favorited");

            System.out.println("e - exit");

            switch (scanner.nextLine()) {
                case "1": {
                    System.out.println("enter author name:");
                    String author = scanner.nextLine();
                    getArticlesByAuthor(author);
                    break;
                }
                case "2": {
                    System.out.println("enter tag name:");
                    String tag = scanner.nextLine();
                    break;
                }
                case "3": {
                    System.out.println("is favorited? y/n");
                    break;
                }
                case "e":
                    break LOOP;
            }
        }
    }

    @Transactional
    public void getArticlesByAuthor(String author) {
        Article article = articleRepository.findByAuthor(author);
        System.out.println(article);
    }
    @Transactional
    public void getArticlesByTag(String author) {
        Article article = articleRepository.findByAuthor(author);
        System.out.println(article);
    }

}
