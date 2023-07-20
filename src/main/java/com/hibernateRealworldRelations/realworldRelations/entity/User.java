package com.hibernateRealworldRelations.realworldRelations.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private Long id;
    private String username;
    @OneToMany(mappedBy = "to")
    private Set<Follower> followers;
    @OneToMany(mappedBy = "from")
    private Set<Follower> following;
    @OneToMany(mappedBy = "author")
    private List<Article> articles;
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "following-users_favorite-articles",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "article_id")}
    )
    private Set<Article> favoriteArticles;
    @OneToMany(mappedBy = "author")
    private List<Comment> comments;


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", followers=" + followers +
                ", following=" + following +
                ", articles=" + articles +
                ", favorites articles=" + favoriteArticles +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(followers, user.followers) && Objects.equals(following, user.following) && Objects.equals(articles, user.articles) && Objects.equals(favoriteArticles, user.favoriteArticles) && Objects.equals(comments, user.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}