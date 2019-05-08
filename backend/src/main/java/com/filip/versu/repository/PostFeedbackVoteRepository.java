package com.filip.versu.repository;

import com.filip.versu.entity.model.PostFeedbackVote;
import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.PostFeedbackPossibility;
import com.filip.versu.entity.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostFeedbackVoteRepository extends JpaRepository<PostFeedbackVote, Long> {

    @Query("select fv from PostFeedbackVote fv where fv.postFeedbackPossibility.post = :post and fv.owner = :owner")
    public PostFeedbackVote findOneByOwnerAndPost(@Param("owner") User owner, @Param("post") Post post);

    public Page<PostFeedbackVote> findByPostFeedbackPossibilityOrderByTimestampDesc(PostFeedbackPossibility postFeedbackPossibility, Pageable pageable);

    @Query("select fa from PostFeedbackVote fa where fa.postFeedbackPossibility = :postFeedbackPossibility and fa.id < :lastId order by fa.timestamp desc")
    public Page<PostFeedbackVote> findByPostFeedbackPossibilityPaging(@Param("postFeedbackPossibility") PostFeedbackPossibility postFeedbackPossibility, @Param("lastId") Long lastId, Pageable pageable);

    public Page<PostFeedbackVote> findByOwnerOrderByTimestampDesc(User owner, Pageable pageable);

    @Query("select fa from PostFeedbackVote fa where fa.owner = :owner and fa.id < :lastId order by fa.timestamp desc")
    public Page<PostFeedbackVote> findByOwnerPaging(@Param("owner") User owner, @Param("lastId") Long lastId, Pageable pageable);

    public Long countByPostFeedbackPossibility(PostFeedbackPossibility postFeedbackPossibility);

    @Modifying
    @Query("update PostFeedbackVote fa set fa.isDeleted=true where fa.postFeedbackPossibility = :feedPoss")
    public void markAsDeletedByFeedbackPossibility(@Param("feedPoss") PostFeedbackPossibility feedPoss);

    @Modifying
    @Query("update PostFeedbackVote fa set fa.isDeleted=true where fa.owner = :owner")
    public void markAsDeletedByCreator(@Param("owner") User owner);


}
