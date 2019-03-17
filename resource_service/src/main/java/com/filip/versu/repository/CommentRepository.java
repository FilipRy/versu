package com.filip.versu.repository;

import com.filip.versu.entity.model.Comment;
import com.filip.versu.entity.model.Post;
import com.filip.versu.repository.abs.AbsFeedbackPostRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface CommentRepository extends AbsFeedbackPostRepository<Comment> {

    public List<Comment> findTop2ByPostOrderByIdDesc(Post post);

    @Transactional
    @Modifying
    @Query(value = "SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci", nativeQuery = true)
    public void setNamesToUtf8Mb4();

}
