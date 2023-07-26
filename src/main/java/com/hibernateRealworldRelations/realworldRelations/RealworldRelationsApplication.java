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
			System.out.println("e - exit");

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
				case "e": break LOOP;
			}

		}
	}
}
