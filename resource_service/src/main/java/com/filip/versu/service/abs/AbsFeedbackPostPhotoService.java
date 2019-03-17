package com.filip.versu.service.abs;

import com.filip.versu.entity.model.PostPhoto;
import com.filip.versu.entity.model.abs.AbsFeedbackPostPhoto;
import com.filip.versu.entity.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is the super-interface for feedback actions (comments, votes, favourites) on shopping items.
 */
public interface AbsFeedbackPostPhotoService<K extends AbsFeedbackPostPhoto> extends CrudAuthService<K, Long>, UserCompositionService<K> {

    /**
     * List a page of entities created by user = @userID.
     * @param userID
     * @param pageable
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<K> listOfUser(Long userID, Pageable pageable, User requester);

    /**
     * Precondition: the creator is got from repository.
     * @param creator
     * @param photo
     * @param requester
     * @return
     */
    public K findByCreatorAndPhoto(User creator, PostPhoto photo, User requester);

    /**
     * This method lists the feedback actions (votes yes, votes no) on photo.
     * @param photoID
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<K> listByPhoto(Long photoID, Pageable pageable, User requester);


    /**
     * This method returns the count of feedback actions (vote_yes yes, vote_yes no) on photo.
     * This method is not doing authorization, because it's invoked only by PostService
     * @param photo
     * @return
     */
    public int countAtPhoto(PostPhoto photo);


    /**
     * This method soft deletes feedback action (votes yes, vote_yes no) of a photo.
     * This is used if shopping item is deleting.
     * @param photo
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeOfPhoto(PostPhoto photo, User requester);


}
