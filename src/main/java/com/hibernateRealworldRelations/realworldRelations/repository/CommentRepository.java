package com.hibernateRealworldRelations.realworldRelations.repository;

import com.hibernateRealworldRelations.realworldRelations.entity.Comment;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment,Long> {

}