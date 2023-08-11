package com.hibernateRealworldRelations.realworldRelations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    public void chooseAction() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("You are in article controller mock, choose action: ");
        LOOP:
        while (true) {
            System.out.println("1 - getArticles");
            System.out.println("2 - feedArticles");
            System.out.println("3 - getArticle");
            System.out.println("4 - createArticle");
            System.out.println("5 - updateArticle");
            System.out.println("6 - deleteArticle");

            System.out.println("e - exit");

            switch (scanner.nextLine()) {
                case "1": {
                    articleService.getArticles();
                    break;
                }
                case "e":
                    break LOOP;
            }
        }

    }
}
