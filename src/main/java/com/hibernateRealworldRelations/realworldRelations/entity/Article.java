package com.hibernateRealworldRelations.realworldRelations.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue
    @Column(name = "article_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User author;
    @Builder.Default
    @ManyToMany(mappedBy = "favoriteArticles")
    private Set<User> followingUsers = new HashSet<>();
    @OneToMany(mappedBy = "article")
    private List<Comment> comments;
    @Builder.Default
    @OneToMany(mappedBy = "article")
    private Set<Tag> tagList = new HashSet<>();

    private String title;

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", author=" + author.getUsername() +
                ", comments=" + comments +
                ", followingUsers=" + followingUsers.stream().map(User::getUsername).toList() +
                ", favoritesCount=" + followingUsers.size() +
                ", tagList" + tagList.stream().map(Tag::getName).toList() +
                '}';
    }
}
