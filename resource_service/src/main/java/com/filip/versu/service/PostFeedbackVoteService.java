package com.filip.versu.service;

import com.filip.versu.entity.model.PostFeedbackVote;
import com.filip.versu.service.abs.CrudAuthService;
import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.PostFeedbackPossibility;
import com.filip.versu.entity.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


public interface PostFeedbackVoteService extends CrudAuthService<PostFeedbackVote, Long> {


    /**
     * List a page of entities created by user = @userID.
     * @param userID
     * @param pageable
     * @param lastLoadedId
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<PostFeedbackVote> findByUserPaging(Long userID, Pageable pageable, User requester, Long lastLoadedId);

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<PostFeedbackVote> findByFeedbackPossibilityReversePaging(Long postFeedbackPossibilityId, Long lastLoadedId, Pageable pageable, User requester);

    public int countByFeedbackPossibility(PostFeedbackPossibility postFeedbackPossibility, User requester);


    /**
     * Precondition: the creator is got from repository.
     *
     * @param creator
     * @param post
     * @param requester
     * @return
     */
    public PostFeedbackVote findByCreatorAndPost(User creator, Post post, User requester);


    @Transactional(propagation = Propagation.REQUIRED)
    public boolean removeByFeedbackPossibility(PostFeedbackPossibility postFeedbackPossibility, User requester);

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean removeByCreator(User owner, User requester);

}
