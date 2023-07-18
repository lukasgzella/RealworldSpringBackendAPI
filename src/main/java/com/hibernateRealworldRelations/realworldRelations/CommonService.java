package com.hibernateRealworldRelations.realworldRelations;

import com.hibernateRealworldRelations.realworldRelations.entity.Article;
import com.hibernateRealworldRelations.realworldRelations.entity.Comment;
import com.hibernateRealworldRelations.realworldRelations.entity.Follower;
import com.hibernateRealworldRelations.realworldRelations.entity.User;
import com.hibernateRealworldRelations.realworldRelations.repository.ArticleRepository;
import com.hibernateRealworldRelations.realworldRelations.repository.CommentRepository;
import com.hibernateRealworldRelations.realworldRelations.repository.FollowerRepository;
import com.hibernateRealworldRelations.realworldRelations.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

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
        Article article = Article.builder().author(author).build();
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
    public void addCommentToArticle(long articleId) {
        Article article = articleRepository.findById(articleId);
        Comment comment = Comment.builder().article(article).build();
        comment = commentRepository.save(comment);
        List<Comment> comments = article.getComments();
        comments.add(comment);
        article.setComments(comments);

        articleRepository.save(article);
        System.out.println("comment with article_id: " + article.getId() + " added to repository");
        System.out.println(comment);
    }

    public void printArticleById(long articleId) {
        Article article = articleRepository.findById(articleId);
        System.out.println(article);
    }
}