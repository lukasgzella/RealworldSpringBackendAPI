package com.hibernateRealworldRelations.realworldRelations.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode
@Table(name = "followers")
public class Follower {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="from_user_fk")
    private User from;

    @ManyToOne
    @JoinColumn(name="to_user_fk")
    private User to;

    public Follower(User from, User to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "Follower{" +
                "id=" + id +
                ", from=" + from.getUsername() +
                ", to=" + to.getUsername() +
                '}';
    }
}