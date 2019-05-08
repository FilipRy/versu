package com.filip.versu.service;

import com.filip.versu.entity.model.Comment;
import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.User;
import com.filip.versu.service.abs.CrudAuthService;
import com.filip.versu.service.abs.UserCompositionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface CommentService extends CrudAuthService<Comment, Long>, UserCompositionService<Comment> {

    /**
     * This method is called only from shoppingItemServiceImpl.initWithFeedbackAction, which is already authorized -> no need for auth here.
     * This method returns the @count most recent comments given on a shoppingitem.
     * @param post
     * @return
     */
    public List<Comment> listByShoppingItemMostRecent(Post post);

    /**
     * List a page of entities created by user = @userID.
     * @param userID
     * @param lastLoadedId
     *@param pageable  @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<Comment> listOfUser(Long userID, Long lastLoadedId, Pageable pageable, User requester);


    /**
     * This method lists the feedback actions (comments, favourites) on shopping item.
     * @param postID
     * @param lastLoadedId
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<Comment> listByPostReversePaging(Long postID, Long lastLoadedId, Pageable pageable, User requester);


    /**
     * This method lists the feedback actions (comments, favourites) on shopping item.
     * @param secretUrl - url as an access
     * @param lastLoadedId
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<Comment> listByPostReversePaging(String secretUrl, Long lastLoadedId, Pageable pageable, User requester);


    /**
     * * Precondition: the creator is got from repository.
     * @param creator
     * @param post
     * @param requester
     * @return
     */
    public Comment findByCreatorAndShoppingItem(User creator, Post post, User requester);


    /**
     * This method returns the count of feedback actions (comment and favourites) on shopping item.
     * This method is not doing authorization, because it's invoked only by PostService
     * @param post
     * @return
     */
    public int countAtPost(Post post);

    /**
     * This method soft deletes feedback action (comments, favourites) of a shopping item.
     * This is used if shopping item is deleting. TODO is this method used in shopping item delete ?
     * @param post
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeOfShoppingItem(Post post, User requester);

}
