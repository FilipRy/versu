package com.filip.versu.repository;

import com.filip.versu.entity.model.Comment;
import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    public List<Comment> findTop2ByPostOrderByIdDesc(Post post);

    public Comment findOneByOwnerAndPost(User owner, Post post);

    public Page<Comment> findByPostOrderByTimestampDesc(Post post, Pageable pageable);

    @Query("select fa from Comment fa where fa.post = :post and fa.id < :lastId order by fa.timestamp desc")
    public Page<Comment> findByPostPaging(@Param("post") Post post, @Param("lastId") Long lastId, Pageable pageable);

    public Page<Comment> findByOwnerOrderByTimestampDesc(User owner, Pageable pageable);

    @Query("select fa from Comment fa where fa.owner = :owner and fa.id < :lastId order by fa.timestamp desc")
    public Page<Comment> findByOwnerPaging(@Param("owner") User owner, @Param("lastId") Long lastId, Pageable pageable);

    public Long countByPost(Post post);

    @Modifying
    @Query("update Comment fa set fa.isDeleted=true where fa.post = :post")
    public void markAsDeletedByPost(@Param("post") Post post);

    @Modifying
    @Query("update Comment fa set fa.isDeleted=true where fa.owner = :owner")
    public void markAsDeletedByCreator(@Param("owner") User owner);

    @Transactional
    @Modifying
    @Query(value = "SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci", nativeQuery = true)
    public void setNamesToUtf8Mb4();

}
