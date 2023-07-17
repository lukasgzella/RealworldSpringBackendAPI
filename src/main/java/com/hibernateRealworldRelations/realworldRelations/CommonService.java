package com.hibernateRealworldRelations.realworldRelations;

import com.hibernateRealworldRelations.realworldRelations.entity.Article;
import com.hibernateRealworldRelations.realworldRelations.entity.Follower;
import com.hibernateRealworldRelations.realworldRelations.entity.User;
import com.hibernateRealworldRelations.realworldRelations.exceptions.NoSuchUserException;
import com.hibernateRealworldRelations.realworldRelations.repository.ArticleRepository;
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
        System.out.println(userTo);
        Thread.sleep(10000);
        Follower follower = new Follower(userFrom,userTo);
        Thread.sleep(10000);
        System.out.println(follower);
        follower = followerRepository.save(follower);
        Thread.sleep(10000);
        System.out.println(follower);
        Set<Follower> following = userFrom.getFollowing();
        Thread.sleep(4000);
        System.out.println(following);
        Thread.sleep(4000);
        following.add(follower);
        Thread.sleep(4000);
        Set<Follower> followers = userTo.getFollowers();
        Thread.sleep(4000);
        System.out.println(followers);
        followers.add(follower);
        Thread.sleep(4000);
        System.out.println(followers);
        Thread.sleep(4000);

        userRepository.save(userFrom);
        userRepository.save(userTo);
    }


    public void addArticleByUserId(long id) {
        User author = userRepository.findById(id);
        Article article = Article.builder().author(author).build();
        List<Article> articles = author.getArticles();
        articles.add(article);
        author.setArticles(articles);

        userRepository.save(author);
        articleRepository.save(article);
    }

    public void printUserById(long userId) {
        User author = userRepository.findById(userId);
        System.out.println(author);
    }
}