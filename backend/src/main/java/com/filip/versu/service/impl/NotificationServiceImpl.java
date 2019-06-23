package com.filip.versu.service.impl;


import com.fasterxml.jackson.databind.util.JSONPObject;
import com.filip.versu.entity.dto.FirebaseNotifKeyExchanger;
import com.filip.versu.entity.dto.FirebaseNotificationDTO;
import com.filip.versu.entity.dto.NotificationDTO;
import com.filip.versu.entity.dto.UserDTO;
import com.filip.versu.entity.model.*;
import com.filip.versu.entity.model.Notification;
import com.filip.versu.entity.model.abs.AbsBaseEntity;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.exception.UnauthorizedException;
import com.filip.versu.repository.NotificationRepository;
import com.filip.versu.service.*;
import com.filip.versu.service.NotificationService;
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
    private PostService postService;

    @Lazy
    @Autowired
    private PostFeedbackVoteService postFeedbackVoteService;

    @Autowired
    private UserService userService;

    private String senderId, apiKey;

    @Autowired
    public NotificationServiceImpl(Environment env) {
        senderId = env.getProperty("firebase.sender_id");
        apiKey = env.getProperty("firebase.api_key");
    }

    @Async
    public Future<Notification> createAsync(Notification entity) {

        entity.setLastUpdateTimestamp(System.currentTimeMillis());

        //notification is unique per target, type, content entity id
        if (entity.getNotificationType() == Notification.NotificationType.comment ||
                entity.getNotificationType() == Notification.NotificationType.post_feedback) {

            if (entity.getTarget().equals(entity.getCreator())) {//receiver of the notification is the same as the source.
                return null;
            }
            entity.setSeen(false);

            List<User> targets = new ArrayList<>();
            targets.add(entity.getTarget());
            createPushNotificationForTarget(targets, entity);

            Notification notification = this.findByNotifTypeTargetAbsBase(entity.getNotificationType(), entity.getTarget(), entity.getPayloadId());
            if (notification == null) {
                return new AsyncResult<>(super.create(entity));
            } else {
                entity.setId(notification.getId());
                return new AsyncResult<>(this.update(entity));
            }

        } else if (entity.getNotificationType() == Notification.NotificationType.post) {

            Post post = postService.get(entity.getPayloadId());

            //creating notifications for viewers
            for (User viewer : post.getViewers()) {
                Notification notification = new Notification();
                notification.setTarget(viewer);
                notification.setPayloadId(post.getId());
                notification.setCreator(entity.getCreator());
                notification.setLastUpdateTimestamp(entity.getLastUpdateTimestamp());
                notification.setNotificationType(entity.getNotificationType());

                super.create(notification);
            }

            createPushNotificationForTarget(post.getViewers(), entity);

            return null;

        } else if (entity.getNotificationType() == Notification.NotificationType.following) {

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

        String url = "https://fcm.googleapis.com/fcm/notification";

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
            logger.info("Pushing " +  notificationDTO.type.name() +  " notification about entity with " + notificationDTO.contentEntityID + " to devices");
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
        restTemplate.exchange(url, HttpMethod.POST, httpEntity, Object.class);
    }

    @Override
    public Notification create(Notification entity) {
        throw new UnsupportedOperationException("use createAsync() instead of this method");
    }

    private Notification findByNotifTypeTargetAbsBase(Notification.NotificationType notificationType, User target, Long payloadId) {
        return repository.findOneByTargetAndNotificationTypeAndPayloadId(target, notificationType, payloadId);
    }

    @Override
    public List<NotificationDTO> listNotificationsOfUser(Long targetID, Long lastLoadedId, Pageable pageable, User requester, boolean returnOnlyUnseen) {

        User target = userService.get(targetID, requester);

        if (!target.equals(requester)) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        Page<Notification> notifications;
        if (returnOnlyUnseen) {
            if (lastLoadedId == null) {
                notifications = repository.
                        findByTargetLastUpdateTimestampSeenOrderByLastUpdateTimestamp(target, target.getLastNotificationRefreshTimestamp(), false, pageable);
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

        List<NotificationDTO> notificationDTOList = new ArrayList<>();

        if (notifications != null) {
            for (Notification notification: notifications.getContent()) {
                this.markAsSeen(notification.getId(), target);
                NotificationDTO notificationDTO = createDTOfromNotification(notification);
                notificationDTOList.add(notificationDTO);
            }
        }
        return notificationDTOList;
    }

    private NotificationDTO createDTOfromNotification(Notification notification) {

        Notification.NotificationType type = notification.getNotificationType();

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.type = notification.getNotificationType();
        notificationDTO.id = notification.getId();
        notificationDTO.seen = notification.isSeen();
        notificationDTO.contentEntityID = notification.getPayloadId();
        notificationDTO.creationTime = notification.getLastUpdateTimestamp();
        notificationDTO.userDTO = new UserDTO(notification.getCreator());

        if (type == Notification.NotificationType.comment) {

            Post post = postService.get(notification.getPayloadId());
            notificationDTO.count = commentService.countAtPost(post);
            notificationDTO.photoUrls.add(post.getPhotos().get(0).getPath());
            if(post.getPhotos().size() > 1) {
                notificationDTO.photoUrls.add(post.getPhotos().get(1).getPath());
            }
        }

        //TODO solve
        else if (type == Notification.NotificationType.post_feedback) {

            Post post = postService.get(notification.getPayloadId());

            for (PostFeedbackPossibility feedbackPossibility : post.getPostFeedbackPossibilities()) {
                notificationDTO.count = notificationDTO.count + postFeedbackVoteService.countByFeedbackPossibility(feedbackPossibility, post.getOwner());
            }
            notificationDTO.photoUrls.add(post.getPhotos().get(0).getPath());
            if(post.getPhotos().size() > 1) {
                notificationDTO.photoUrls.add(post.getPhotos().get(1).getPath());
            }

        } else if (type == Notification.NotificationType.post) {

            Post post = postService.get(notification.getPayloadId());

            notificationDTO.photoUrls.add(post.getPhotos().get(0).getPath());
            if(post.getPhotos().size() > 1) {
                notificationDTO.photoUrls.add(post.getPhotos().get(1).getPath());
            }
        }


        return notificationDTO;
    }


    @Override
    public boolean removeByUser(User user) {
        repository.deleteByCreator(user);
        repository.deleteByTarget(user);
        return true;
    }

    @Override
    public boolean removeByTypeAndId(Notification.NotificationType notificationType, Long payloadId) {
        repository.deleteByNotificationTypeAndPayloadId(notificationType, payloadId);
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
