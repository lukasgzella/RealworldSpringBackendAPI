package com.hibernateRealworldRelations.realworldRelations;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
@RequiredArgsConstructor
public class RealworldRelationsApplication implements CommandLineRunner {

	private final CommonService service;
	private final ArticleController articleController;

	public static void main(String[] args) {
		SpringApplication.run(RealworldRelationsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Welcome");
		LOOP: while (true) {
			System.out.println("1 - create user");
			System.out.println("2 - create article");
			System.out.println("3 - add folower");
			System.out.println("4 - print user");
			System.out.println("5 - add comment");
			System.out.println("6 - print article");
			System.out.println("7 - make article favorite");
			System.out.println("8 - add tag to article");
			System.out.println("9 - go to Article Controller");
			System.out.println("10 - find followers by usernames");
			System.out.println("art - add test users with articles");
			System.out.println("tgs - add test tags");
			System.out.println("fav - add test favorites");
			System.out.println("e - exit");
			System.out.println("s - select email");
			System.out.println("auth - getAuthentication - facade");

			System.out.println("Choose from menu");
			switch (scanner.nextLine()) {
				case "1":	{
					System.out.println("Enter username: ");
					String username = scanner.nextLine();
					service.addUser(username);
					break;
				}
				case "2":	{
					System.out.println("Enter userId: ");
					long userId = Long.parseLong(scanner.nextLine());
					service.addArticleByUserId(userId);
					break;
				}
				case "3":	{
					System.out.println("Enter userIdFrom: ");
					long userIdFrom = Long.parseLong(scanner.nextLine());
					System.out.println("Enter userIdTo: ");
					long userIdTo = Long.parseLong(scanner.nextLine());
					service.followUserById(userIdFrom, userIdTo);
					break;
				}
				case "4":	{
					System.out.println("Enter userId: ");
					long userId = Long.parseLong(scanner.nextLine());
					service.printUserById(userId);
					break;
				}
				case "5":	{
					System.out.println("Enter articleId: ");
					long articleId = Long.parseLong(scanner.nextLine());
					System.out.println("Enter userId: ");
					long userId = Long.parseLong(scanner.nextLine());
					service.addCommentToArticle(articleId, userId);
					break;
				}
				case "6":	{
					System.out.println("Enter articleId: ");
					long articleId = Long.parseLong(scanner.nextLine());
					service.printArticleById(articleId);
					break;
				}
				case "7":	{
					System.out.println("Enter articleId: ");
					long articleId = Long.parseLong(scanner.nextLine());
					System.out.println("Enter userId: ");
					long userId = Long.parseLong(scanner.nextLine());
					service.makeArticleFavorite(articleId, userId);
					break;
				}
				case "8":	{
					System.out.println("Enter articleId: ");
					long articleId = Long.parseLong(scanner.nextLine());
					System.out.println("Enter tagName: ");
					String tagName = scanner.nextLine();
					service.addTagToArticle(articleId, tagName);
					break;
				}
				case "9":	{
					System.out.println("---> go to articleController...");
					articleController.chooseAction();
					break;
				}
				case "10":	{
					System.out.println("find followers");
					System.out.println("Enter username FROM: ");
					String from = scanner.nextLine();
					System.out.println("Enter username TO: ");
					String to = scanner.nextLine();
					service.findFollowers(from, to);
					break;
				}
				case "art":	{
					System.out.println("---> Add test users with articles");
					service.addTestUsersWithArticles();
					service.addTestArticlesByRequest();
					break;
				}
				case "tgs":	{
					System.out.println("---> Add tags to test article");
					service.addTagToArticle(1,"t1a1");
					service.addTagToArticle(1,"t2a1");
					service.addTagToArticle(2,"t1a2");
					service.addTagToArticle(2,"t2a2");
					break;
				}
				case "fav": {
					System.out.println("---> Add favorites to test article");
					service.makeArticleFavorite(1,1);
					service.makeArticleFavorite(2,1);
					service.makeArticleFavorite(3,1);
					service.makeArticleFavorite(4,1);
					service.makeArticleFavorite(5,1);
					service.makeArticleFavorite(6,2);
					service.makeArticleFavorite(7,2);
					service.makeArticleFavorite(8,2);
					service.makeArticleFavorite(9,2);
					service.makeArticleFavorite(10,2);
					break;
				}
				case "s": {
					System.out.println("---> select email from");
					service.select();
					break;
				}
				case "auth": {
					System.out.println("---> getting auth");
					service.getAuth();
					break;
				}
				case "e": System.exit(0);
			}
		}
	}
}
