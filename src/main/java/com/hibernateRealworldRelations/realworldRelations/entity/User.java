package com.hibernateRealworldRelations.realworldRelations.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
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
    //todo
//    @OneToMany(mappedBy = "author")
//    private List<Comment> comments;


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", followers=" + followers +
                ", following=" + following +
                ", articles=" + articles +
                '}';
    }
}