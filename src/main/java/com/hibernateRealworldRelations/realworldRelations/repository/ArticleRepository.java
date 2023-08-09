package com.hibernateRealworldRelations.realworldRelations.repository;

import com.hibernateRealworldRelations.realworldRelations.entity.Article;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


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
            LEFT JOIN FETCH a.author 
            LEFT JOIN FETCH a.tagList 
            WHERE a.author.username = :author 
            """)
    List<Article> findArticlesByAuthor(@Param("author") String author);

    @Query("""
            SELECT a FROM Article a
            WHERE (:tag IN (SELECT t.name FROM a.tagList t))
            """)
    List<Article> findArticlesByTag(@Param("tag") String tag);

    @Query("""
            SELECT a FROM Article a
            WHERE (:favorited IN (SELECT fu.username FROM a.followingUsers fu))
            """)
    List<Article> findArticlesByFavorited(@Param("favorited") String favorited);
}