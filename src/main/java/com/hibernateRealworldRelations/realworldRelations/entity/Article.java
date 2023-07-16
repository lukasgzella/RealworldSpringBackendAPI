package com.hibernateRealworldRelations.realworldRelations.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    @JoinColumn(name="user_id", nullable = false)
    private User author;

}
