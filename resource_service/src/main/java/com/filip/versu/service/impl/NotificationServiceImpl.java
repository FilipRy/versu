package com.filip.versu.service.impl;

import com.amazonaws.util.json.JSONObject;
import com.filip.versu.entity.dto.FirebaseNotifKeyExchanger;
import com.filip.versu.entity.dto.FirebaseNotificationDTO;
import com.filip.versu.entity.dto.NotificationDTO;
import com.filip.versu.entity.dto.UserDTO;
import com.filip.versu.entity.model.*;
import com.filip.versu.entity.model.notification.FollowingNotification;
import com.filip.versu.entity.model.notification.Notification;
import com.filip.versu.entity.model.notification.PostNotification;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.exception.UnauthorizedException;
import com.filip.versu.repository.NotificationRepository;
import com.filip.versu.service.CommentService;
import com.filip.versu.service.NotificationService;
import com.filip.versu.service.PostFeedbackVoteService;
import com.filip.versu.service.UserService;
import com.filip.versu.service.impl.abs.AbsCrudServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;


@Service
public class NotificationServiceImpl extends AbsCrudServiceImpl<Notification, Long, NotificationRepository> implements NotificationService {

    private final static Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Lazy
    @Autowired
    private CommentService commentService;

    @Lazy
    @Autowired
    private PostFeedbackVoteService postFeedbackVoteService;

    @Autowired
    private UserService userService;

    private String senderId, apiKey;

    @Autowired
    public NotificationServiceImpl(Environment env) {
        senderId = env.getProperty("firebase.sender_id");
        apiKey = env.getProperty("google.apikey");
    }

    @Async
    @Override
    public Future<Notification> createAsync(Notification entity) {

        entity.setLastUpdateTimestamp(System.currentTimeMillis());

        //notification is unique per target, type, content entity id
        if (entity.getType() == NotificationDTO.NotificationType.comment ||
                entity.getType() == NotificationDTO.NotificationType.post_feedback) {

            PostNotification postNotification = (PostNotification) entity;
            if (postNotification.getTarget().equals(postNotification.getCreator())) {//receiver of the notification is the same as the source.
                return null;
            }
            PostNotification notification = getByTypeTargetShopping(entity.getType(), entity.getTarget(), postNotification.getEntityContent());
            if (notification == null) {
                List<User> targets = new ArrayList<>();
                targets.add(entity.getTarget());
                createPushNotificationForTarget(targets, entity);
                return new AsyncResult<>(super.create(entity));
            } else {
                entity.setId(notification.getId());
                List<User> targets = new ArrayList<>();
                targets.add(entity.getTarget());
                createPushNotificationForTarget(targets, entity);
                return new AsyncResult<>(update(entity));
            }

        } else if (entity.getType() == NotificationDTO.NotificationType.post) {

            PostNotification postNotification = (PostNotification) entity;

            //creating notifications for viewers
            for (User viewer : postNotification.getEntityContent().getViewers()) {

                PostNotification notification = new PostNotification();
                notification.setTarget(viewer);
                notification.setEntityContent(postNotification.getEntityContent());
                notification.setCreator(postNotification.getCreator());
                notification.setLastUpdateTimestamp(entity.getLastUpdateTimestamp());
                notification.setType(postNotification.getType());

                super.create(notification);
            }

            createPushNotificationForTarget(postNotification.getEntityContent().getViewers(), postNotification);

            return null;

        } else if (entity.getType() == NotificationDTO.NotificationType.following) {

            List<User> targets = new ArrayList<>();
            targets.add(entity.getTarget());
            createPushNotificationForTarget(targets, entity);

            return new AsyncResult<>(super.create(entity));
        }
        return new AsyncResult<>(super.create(entity));
    }

    /**
     * this is a blocking operation and should be invoked in a separate thread
     *
     * @param targets
     * @param notification
     */
    private void createPushNotificationForTarget(List<User> targets, Notification notification) {
        List<DeviceInfo> deviceInfos = new ArrayList<>();

        for (User target : targets) {
            target = userService.get(target.getId());
            if(logger.isInfoEnabled()) {
                logger.info("Creating push notification for user " + target.getUsername() + " (has " + target.getDevices().size() + " devices).");
            }
            deviceInfos.addAll(target.getDevices());
        }

        if (deviceInfos == null || deviceInfos.size() == 0) {
            return;
        }
        String notificationKey = retrieveNotificationDeviceGroupKey(deviceInfos);

        NotificationDTO notificationDTO = createDTOfromNotification(notification);

        pushNotificationToDevices(notificationKey, notificationDTO);
    }

    private String retrieveNotificationDeviceGroupKey(List<DeviceInfo> deviceInfos) {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        String url = "https://android.googleapis.com/gcm/notification";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        httpHeaders.set("Authorization", "key=" + apiKey);
        httpHeaders.set("project_id", senderId);

        FirebaseNotifKeyExchanger keyExchanger = new FirebaseNotifKeyExchanger();
        keyExchanger.setOperation("create");
        keyExchanger.setNotification_key_name(Long.toString(System.currentTimeMillis()) + deviceInfos.hashCode());

        List<String> registrationIds = new ArrayList<>();
        for (DeviceInfo deviceInfo : deviceInfos) {
            if (deviceInfo.getDeviceRegistrationID() != null) {
                registrationIds.add(deviceInfo.getDeviceRegistrationID());
            }
        }
        keyExchanger.setRegistration_ids(registrationIds);

        HttpEntity<FirebaseNotifKeyExchanger> httpEntity = new HttpEntity<>(keyExchanger, httpHeaders);
        ResponseEntity<FirebaseNotifKeyExchanger.FirebaseNotificationKey> notificationKeyResponse = restTemplate.exchange(url, HttpMethod.POST, httpEntity, FirebaseNotifKeyExchanger.FirebaseNotificationKey.class);

        FirebaseNotifKeyExchanger.FirebaseNotificationKey notificationKey = notificationKeyResponse.getBody();
        return notificationKey.getNotification_key();
    }

    private void pushNotificationToDevices(String notificationKey, NotificationDTO notificationDTO) {

        if(logger.isInfoEnabled()) {
            logger.info("Pushing notification " + notificationDTO.getId() + " to devices");
        }

        FirebaseNotificationDTO firebaseNotificationDTO = new FirebaseNotificationDTO();
        firebaseNotificationDTO.setTo(notificationKey);

        FirebaseNotificationDTO.FirebaseNotificationBody notificationBody = new FirebaseNotificationDTO.FirebaseNotificationBody();

        firebaseNotificationDTO.setData(notificationDTO);
        firebaseNotificationDTO.setNotification(notificationBody);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        String url = "https://fcm.googleapis.com/fcm/send";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        httpHeaders.set("Authorization", "key=" + apiKey);

        HttpEntity<FirebaseNotificationDTO> httpEntity = new HttpEntity<>(firebaseNotificationDTO, httpHeaders);
        restTemplate.exchange(url, HttpMethod.POST, httpEntity, JSONObject.class);

    }

    @Override
    public Notification create(Notification entity) {
        throw new UnsupportedOperationException("use createAsync() instead of this method");
    }

    @Override
    public List<NotificationDTO> listNotificationsOfUser(Long targetID, Long lastLoadedId, Pageable pageable, User requester, boolean returnOnlyNotNotified) {

        User target = userService.get(targetID, requester);

        if (!target.equals(requester)) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        List<NotificationDTO> notificationDTOs = new ArrayList<>();

        Page<Notification> notifications;
        if (returnOnlyNotNotified) {

            if (lastLoadedId == null) {
                notifications = repository.
                        findByTargetAndLastUpdateTimestampGreaterThanOrderByLastUpdateTimestampDesc(target,
                                target.getLastNotificationRefreshTimestamp(), pageable);
            } else {
                notifications = repository.findByTargetUnseenPaging(target, lastLoadedId, target.getLastNotificationRefreshTimestamp(), pageable);
            }

        } else {

            if (lastLoadedId == null) {
                notifications = repository.findByTargetOrderByLastUpdateTimestampDesc(target, pageable);
            } else {
                notifications = repository.findByTargetPaging(target, lastLoadedId, pageable);
            }
        }

        //updating the last seen notification timestamp
        userService.updateLastNotificationRefreshTimestamp(targetID, System.currentTimeMillis(), target);

        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = createDTOfromNotification(notification);
            notificationDTOs.add(notificationDTO);
        }

        return notificationDTOs;
    }

    private NotificationDTO createDTOfromNotification(Notification notification) {

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.type = notification.getType();
        notificationDTO.id = notification.getId();
        notificationDTO.seen = notification.isSeen();

        NotificationDTO.NotificationType type = notification.getType();

        int count = 0;

        if (type == NotificationDTO.NotificationType.comment) {

            PostNotification postNotification = (PostNotification) notification;
            count = commentService.countAtShoppingItem(postNotification.getEntityContent());
            notificationDTO.contentEntityID = postNotification.getEntityContent().getId();
            notificationDTO.photoUrls.add(postNotification.getEntityContent().getPhotos().get(0).getPath());

            if(postNotification.getEntityContent().getPhotos().size() > 1) {
                notificationDTO.photoUrls.add(postNotification.getEntityContent().getPhotos().get(1).getPath());
            }


        }
        //TODO solve
        else if (type == NotificationDTO.NotificationType.post_feedback) {

            PostNotification postNotification = (PostNotification) notification;
            for (PostFeedbackPossibility feedbackPossibility : postNotification.getEntityContent().getPostFeedbackPossibilities()) {
                count = count + postFeedbackVoteService.countByFeedbackPossibility(feedbackPossibility, postNotification.getEntityContent().getOwner());
            }
            notificationDTO.contentEntityID = postNotification.getEntityContent().getId();

            notificationDTO.photoUrls.add(postNotification.getEntityContent().getPhotos().get(0).getPath());

            if(postNotification.getEntityContent().getPhotos().size() > 1) {
                notificationDTO.photoUrls.add(postNotification.getEntityContent().getPhotos().get(1).getPath());
            }

        } else if (type == NotificationDTO.NotificationType.post) {

            PostNotification postNotification = (PostNotification) notification;
            User creator = postNotification.getEntityContent().getOwner();
            notificationDTO.userDTO = new UserDTO(creator);
            notificationDTO.contentEntityID = postNotification.getEntityContent().getId();

            notificationDTO.photoUrls.add(postNotification.getEntityContent().getPhotos().get(0).getPath());

            if(postNotification.getEntityContent().getPhotos().size() > 1) {
                notificationDTO.photoUrls.add(postNotification.getEntityContent().getPhotos().get(1).getPath());
            }

        } else if (type == NotificationDTO.NotificationType.following) {

            FollowingNotification followingNotification = (FollowingNotification) notification;
            User creator = followingNotification.getEntityContent().getCreator();
            notificationDTO.userDTO = new UserDTO(creator);
            notificationDTO.contentEntityID = followingNotification.getEntityContent().getId();
        }

        notificationDTO.userDTO = new UserDTO(notification.getCreator());
        notificationDTO.count = count;
        notificationDTO.creationTime = notification.getLastUpdateTimestamp();


        return notificationDTO;
    }


    @Override
    public Future<Notification> createForPostFeedback(PostFeedbackVote postFeedbackVote) {
        PostNotification notification = new PostNotification();
        notification.setType(NotificationDTO.NotificationType.post_feedback);
        notification.setCreator(postFeedbackVote.getOwner());
        notification.setTarget(postFeedbackVote.getPostFeedbackPossibility().getPost().getOwner());
        notification.setEntityContent(postFeedbackVote.getPostFeedbackPossibility().getPost());
        return createAsync(notification);
    }

    @Override
    public Future<Notification> createForComment(Comment comment) {
        PostNotification notification = new PostNotification();
        notification.setType(NotificationDTO.NotificationType.comment);
        notification.setCreator(comment.getOwner());
        notification.setTarget(comment.getPost().getOwner());
        notification.setEntityContent(comment.getPost());
        return createAsync(notification);
    }

    @Override
    public Future<Notification> createForPost(Post post) {

        PostNotification notification = new PostNotification();
        notification.setType(NotificationDTO.NotificationType.post);
        notification.setCreator(post.getOwner());

        notification.setEntityContent(post);
        return createAsync(notification);
    }

    @Override
    public Future<Notification> createForFollowing(Following following) {
        FollowingNotification notification = new FollowingNotification();
        notification.setType(NotificationDTO.NotificationType.following);
        notification.setCreator(following.getCreator());
        notification.setTarget(following.getTarget());
        notification.setEntityContent(following);
        return createAsync(notification);
    }

    @Override
    public PostNotification getByTypeTargetShopping(NotificationDTO.NotificationType type, User target, Post post) {
        return repository.findOneByTypeAndTargetAndEntityContent(type, target, post);
    }

    @Override
    public boolean removeByShoppingItem(Post post) {
        repository.removeNotificationsByPost(post);
        return true;
    }

    @Override
    public boolean removeByUser(User user) {
        //TODO
        return false;
    }

    @Override
    public boolean removeByFollowing(Following following) {
        repository.removeNotificationsByFollowing(following);
        return true;
    }

    @Override
    public boolean markAsSeen(Long id, User requester) {
        Notification notification = get(id);

        if(notification == null) {
            return false;
        }

        if(!requester.equals(notification.getTarget())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        notification.setSeen(true);
        update(notification);
        return true;
    }

    @Override
    public Notification transferUpdateFields(Notification getEntity, Notification updatedEntity) {
        getEntity.setLastUpdateTimestamp(updatedEntity.getLastUpdateTimestamp());
        getEntity.setCreator(updatedEntity.getCreator());
        getEntity.setSeen(updatedEntity.isSeen());
        return getEntity;
    }
}
