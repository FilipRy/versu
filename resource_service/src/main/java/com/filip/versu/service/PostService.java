package com.filip.versu.service;


import com.filip.versu.entity.dto.URLWrapperDTO;
import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.User;
import com.filip.versu.service.abs.CrudAuthService;
import com.filip.versu.service.abs.UserCompositionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostService extends CrudAuthService<Post, Long>, UserCompositionService<Post> {

    /**
     * This is the time to live of the pre-signed urls [ms].
     */
    public static final int PRE_SIGNED_URL_TTL = 5 * 60 * 1000;

    public static final int NEAR_BY_MAX_DISTANCE_KM = 20;


    /**
     * This method is called only by authorized endpoint
     *
     * @param user
     * @param post - this param must be retrieved from DB, before sending it here, not just passing a post sent by user to backend.
     * @return
     */
    public boolean canUserReadPost(User user, Post post);

    /**
     * Returns the shopping item with ID @shoppingItemID.
     *
     * @param shoppingItemID
     * @param requester
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Post getDetails(Long shoppingItemID, User requester);

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Post getDetailsBySecretUrl(String secretUrl, User requester);


    /**
     * This method is used only at authenticating requests from anonym users.
     *
     * @param secretUrl
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Post getPostBySecretUrl(String secretUrl);

    /**
     * List a page of shopping items created by user = @ownerID, which are visible for user = @requester.
     * (e.g. if @requester is viewing profile of @ownerID)
     *  @param ownerID
     * @param pageable @return
     * @param lastLoadedId
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<Post> findPostsVisibleForViewer(Long ownerID, Pageable pageable, User requester, Long lastLoadedId);


    /**
     * This method is used to fill timeline feed.
     *
     * @param userID
     * @param lastLoadedId
     *@param pageable
     * @param requester   @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<Post> findForUserByTime(Long userID, Long lastLoadedId, Pageable pageable, User requester);

    /**
     * This method is used to fill nearby feed.
     *  @param userID
     * @param pageable
     * @param requester
     * @param latitude
     * @param longitude     @return
     * */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<Post> findForUserByLocation(Long userID, Pageable pageable, User requester, double latitude, double longitude);


    /**
     * This method return possibilities, which have already been used at posts.
     *
     * @param pattern   - pattern for possibilities, smtng in form of: likeVS, or poss1 or likeVSlove...
     * @param requester
     * @return - array of possibilities, return[0] ... possibilityA, return[1] ... possibility B
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<String[]> findVSPossibilities(String pattern, User requester);

    /**
     * @param
     * @param requester
     * @param pageable  @return all posts having possibilities equals as in @feedbackPossibilities, if one of these possibilities == null, then it's ignored.
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<Post> findByFeedbackPossibilitiesName(String feedbackPossA, String feedbackPossB, User requester, Pageable pageable);


    /**
     * @param googleId
     * @param pageable
     * @param lastLoadedId
     * @return all posts marked in a location with a specified place id by google.
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<Post> findByLocationGoogleId(String googleId, User requester, Pageable pageable, Long lastLoadedId);

    /**
     * This lists the user, who are authorized to view a shopping item.
     * Precondition: ShoppingItem.accessType == SPECIFIC
     *
     * @param shoppingItemID
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<User> listViewers(Long shoppingItemID, User requester);

    /**
     * This generated the presigned URLs for storing objects in Amazon S3.
     *
     * @param objectKeys
     * @return
     */
    public URLWrapperDTO generatePreSignedURL(URLWrapperDTO objectKeys);


    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<Post> initializeWithMyFeedbackAction(Page<Post> shoppingItems, User requester);

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Post initializeWithMyFeedbackAction(Post post, User requester);

}
