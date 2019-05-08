package com.filip.versu.service;

import com.filip.versu.entity.model.Following;
import com.filip.versu.entity.model.User;
import com.filip.versu.service.abs.CrudAuthService;
import com.filip.versu.service.abs.UserCompositionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


public interface FollowingService extends CrudAuthService<Following, Long>, UserCompositionService<Following> {


    public boolean existsFollowingByCreatorAndTarget(User creator, User target);

    public Following getFollowingByCreatorAndTarget(User creator, User target, User requestor);

    /**
     * Returns the FOLLOWERS of user
     * @param userID
     * @param lastLoadedId
     *@param pageable  @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<Following> listFollowersOfUser(Long userID, Long lastLoadedId, Pageable pageable, User requester);


    /**
     * Returns the users, who are followed by @user
     * @param userID
     * @param lastLoadedId
     *@param pagelable  @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<Following> listFollowedByOfUser(Long userID, Long lastLoadedId, Pageable pagelable, User requester);


    /**
     * This method can be invoked by each user of this application.
     * @param userID
     * @return Count of users, who are following the user @userID.
     */
    public int countFollowersOfUser(Long userID);

    /**
     * This method can be invoked by each user of this application.
     * @param userID
     * @return Count of users, who are followed by the user @userID.
     */
    public int countFollowedByOfUser(Long userID);



}
