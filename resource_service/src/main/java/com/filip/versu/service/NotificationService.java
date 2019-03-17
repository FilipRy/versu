package com.filip.versu.service;

import com.filip.versu.entity.dto.NotificationDTO;
import com.filip.versu.entity.model.*;
import com.filip.versu.entity.model.notification.Notification;
import com.filip.versu.entity.model.notification.PostNotification;
import com.filip.versu.service.abs.CrudService;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Future;


public interface NotificationService extends CrudService<Notification, Long> {

    /**
     *
     * @param notification
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Future<Notification> createAsync(Notification notification);
    public Future<Notification> createForPostFeedback(PostFeedbackVote postFeedbackVote);
    public Future<Notification> createForComment(Comment comment);


    /**
     * Informs the viewers of post, that there is a new post.
     * @param post
     * @return
     */
    public Future<Notification> createForPost(Post post);
    public Future<Notification> createForFollowing(Following following);


    /**
     *
     * @param targetID the id of user whose notification are going to be retrieved.
     * @param lastLoadedId
     *@param pageable
     * @param requester
     * @param returnOnlyUnseen : if true -> returns only notifications, which haven't been seen by this user.
*                        (e.g. If user has already been notified in browser, then he don't want to be notified again about the same in mobile device)    @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<NotificationDTO> listNotificationsOfUser(Long targetID, Long lastLoadedId, Pageable pageable, User requester, boolean returnOnlyUnseen);

    @Transactional(propagation = Propagation.REQUIRED)
    public PostNotification getByTypeTargetShopping(NotificationDTO.NotificationType type, User target, Post post);

    /**
     * Marks notification with @id as seen.
     * @param id
     * @param requester
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean markAsSeen(Long id, User requester);

    /**
     * This method is invoked only by authorized endpoint
     * @param post
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean removeByShoppingItem(Post post);

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean removeByUser(User user);


    /**
     * This method is invoked only by authorized enpoitn
     * @param following
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean removeByFollowing(Following following);

}
