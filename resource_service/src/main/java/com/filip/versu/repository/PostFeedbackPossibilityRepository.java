package com.filip.versu.repository;

import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.PostFeedbackPossibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PostFeedbackPossibilityRepository extends JpaRepository<PostFeedbackPossibility, Long> {

    @Modifying
    @Query("update PostFeedbackPossibility fa set fa.isDeleted=true where fa.post = :post")
    public void markAsDeletedByPost(@Param("post") Post post);


}
