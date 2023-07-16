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

    public void followUserById(long userIdFrom, long userIdTo) {
        User userFrom = userRepository.findById(userIdFrom);
        User userTo = userRepository.findById(userIdTo);
        Follower follower = new Follower(userFrom,userTo);
        List<Follower> following = userFrom.getFollowing();
        following.add(follower);
        List<Follower> followers = userTo.getFollowers();
        followers.add(follower);

        userRepository.save(userFrom);
        userRepository.save(userTo);
        followerRepository.save(follower);
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