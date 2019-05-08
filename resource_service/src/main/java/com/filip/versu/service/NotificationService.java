package com.filip.versu.service;

import com.filip.versu.entity.dto.NotificationDTO;
import com.filip.versu.entity.model.Notification;
import com.filip.versu.entity.model.User;
import com.filip.versu.entity.model.abs.AbsBaseEntity;
import com.filip.versu.service.abs.CrudService;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Future;


public interface NotificationService extends CrudService<Notification, Long> {

    /**
     *
     * @param absNotification
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Future<Notification> createAsync(Notification absNotification);


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

    /**
     * Marks notification with @id as seen.
     * @param id
     * @param requester
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean markAsSeen(Long id, User requester);


    @Transactional(propagation = Propagation.REQUIRED)
    public boolean removeByUser(User user);

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean removeByTypeAndId(Notification.NotificationType notificationType, Long payloadId);

}
