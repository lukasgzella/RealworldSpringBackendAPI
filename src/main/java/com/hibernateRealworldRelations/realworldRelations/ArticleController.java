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
            System.out.println("3 - createArticle");
            System.out.println("4 - getArticle");
            System.out.println("5 - updateArticle");
            System.out.println("6 - deleteArticle");
            System.out.println("7 - addCommentToAnArticle");
            System.out.println("8 - getCommentsFromAnArticle");
            System.out.println("9 - deleteComment");
            System.out.println("10 - favoriteArticle");
            System.out.println("11 - unfavoriteArticle");

            System.out.println("e - exit");

            switch (scanner.nextLine()) {
                case "1": {
                    articleService.getArticles();
                    break;
                }
                case "2": {
                    articleService.feedArticles();
                    break;
                }
                case "3": {
                    articleService.createArticle();
                    break;
                }
                case "4": {
                    articleService.getArticle();
                    break;
                }
                case "5": {
                    articleService.updateArticle();
                    break;
                }
                case "6": {
                    articleService.deleteArticle();
                    break;
                }
                case "7": {
                    articleService.addCommentsToAnArticle();
                    break;
                }
                case "8": {
                    articleService.getCommentsFromAnArticle();
                    break;
                }
                case "9": {
                    articleService.deleteComment();
                    break;
                }
                case "10": {
                    articleService.favoriteArticle();
                    break;
                }
                case "11": {
                    articleService.unfavoriteArticle();
                    break;
                }
                case "e":
                    break LOOP;
            }
        }
    }
}
