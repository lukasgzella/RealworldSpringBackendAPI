package com.hibernateRealworldRelations.realworldRelations.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User author;
    @ManyToOne
    @JoinColumn(name="article_id")
    private Article article;

    @Override
    public String toString() {
        return "Comment{" +
                "comment_id=" + id +
                ", article_id=" + article.getId() +
                ", user_id=" + author.getId() +
                '}';
    }
}
