package com.hibernateRealworldRelations.realworldRelations.repository;

import com.hibernateRealworldRelations.realworldRelations.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User,Long> {

//    Optional<User> findByEmail(String email);
    @Query("""
            SELECT u FROM User u 
            LEFT JOIN FETCH u.following 
            LEFT JOIN FETCH u.followers 
            LEFT JOIN FETCH u.articles 
            WHERE (u.id = :id) 
            """)
    User findById(@Param("id") long id);

    Optional<User> findByUsername(String username);

//    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
