package com.hibernateRealworldRelations.realworldRelations.repository;

import com.hibernateRealworldRelations.realworldRelations.entity.Article;
import com.hibernateRealworldRelations.realworldRelations.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


public interface ArticleRepository extends CrudRepository<Article, Long> {

    //    @Query(
//            """
//                    SELECT a FROM Article a
//                    WHERE (:tag IS NULL OR :tag IN (SELECT t.tag.name FROM a.includeTags t))
//                    AND (:author IS NULL OR a.author.username = :author)
//                    AND (:favorited IS NULL OR :favorited IN (SELECT fu.user.username FROM a.favoriteUsers fu))
//                    ORDER BY a.createdAt DESC
//                    """)
//    Page<Article> findByParams(
//            @Param("tag") String tag,
//            @Param("author") String author,
//            @Param("favorited") String favorited,
//            Pageable pageable
//    );
//    Page<Article> findByAuthorOrderByCreatedAtDesc(Collection<User> authors, Pageable pageable);
//    Article findBySlug(String slug);
    @Query("""
            SELECT a FROM Article a 
            LEFT JOIN FETCH a.comments 
                        
            WHERE (a.id = :id) 
            """)
    Article findById(@Param("id") long id);

    @Query("""
            SELECT a FROM Article a 
            WHERE a.author = :author 
            """)
    Article findByAuthor(@Param("author") String author);
}