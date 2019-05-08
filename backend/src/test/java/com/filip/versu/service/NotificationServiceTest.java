package com.filip.versu.service;


import com.filip.versu.entity.dto.NotificationDTO;
import com.filip.versu.entity.dto.UserDTO;
import com.filip.versu.entity.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class NotificationServiceTest {


    @Autowired
    private UserService userService;

    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private PostService postService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PostFeedbackVoteService postFeedbackVoteService;

    @Autowired
    private FollowingService followingService;

    @Test
    public void test_create_shouldCreateNewNotification() throws Exception {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        Post post = PostServiceTest.createPost(owner, null);
        post = postService.create(post, owner);

        Notification notification = new Notification();
        notification.setPayloadId(post.getId());
        notification.setNotificationType(Notification.NotificationType.comment);
        notification.setTarget(owner);

        Future<Notification> notificationFuture = notificationService.createAsync(notification);

        while (!notificationFuture.isDone()) {
            Thread.sleep(100);
        }

        assertTrue(notificationFuture.get().getId() != null);

        Notification getNotif = notificationService.get(notificationFuture.get().getId());

        assertTrue(getNotif.equals(notificationFuture.get()));
    }

    @Test
    public void test_create_shouldUpdateExistingNotification() throws Exception {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        User viewer1 = UserServiceTest.createUser("viewer2");
        viewer1 = userService.create(viewer1, viewer1);

        Post post = PostServiceTest.createPost(owner, null);
        post = postService.create(post, owner);

        Notification notification = new Notification();
        notification.setPayloadId(post.getId());
        notification.setNotificationType(Notification.NotificationType.comment);
        notification.setTarget(owner);
        notification.setCreator(viewer);

        Future<Notification> notificationFuture = notificationService.createAsync(notification);

        while (!notificationFuture.isDone()) {
            Thread.sleep(100);
        }

        //creating the notification of the same type and same target again
        Notification nextNotification = new Notification();
        nextNotification.setPayloadId(post.getId());
        nextNotification.setNotificationType(Notification.NotificationType.comment);
        nextNotification.setTarget(owner);
        nextNotification.setCreator(viewer1);

        //showing that no new notification was created
        Future<Notification> nextNotificationFuture = notificationService.createAsync(nextNotification);

        while (!nextNotificationFuture.isDone()) {
            Thread.sleep(100);
        }

        Notification getNotif = notificationFuture.get();
        Notification getNextNotif = nextNotificationFuture.get();

        assertTrue(getNextNotif.getId().equals(getNotif.getId()));
        assertTrue(getNextNotif.getTarget().equals(getNotif.getTarget()));
        assertFalse(getNextNotif.getCreator().equals(getNotif.getCreator()));
        assertFalse(getNextNotif.getLastUpdateTimestamp() == getNotif.getLastUpdateTimestamp());
    }

    @Test
    public void test_create_post_shouldOK() throws InterruptedException {

        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);

        //creating post, viewer should be notified
        Post post = PostServiceTest.createPost(owner, viewers);
        post = postService.create(post, owner);

        Thread.sleep(500);

        List<NotificationDTO> notifications = notificationService.listNotificationsOfUser(viewer.getId(), null,  PageRequest.of(0, 20), viewer, false);

        assertTrue(notifications != null);
        assertTrue(notifications.size() == 1);

        //this should be a post notification
        NotificationDTO postNotification = notifications.get(0);
        //showing that post notification was created correctly
        assertTrue(postNotification.type == Notification.NotificationType.post);
        assertTrue(postNotification.userDTO.getId().equals(owner.getId()));
        assertTrue(postNotification.contentEntityID.equals(post.getId()));
    }


    @Test
    public void test_create_following_shouldOK() throws InterruptedException {
        User creator = UserServiceTest.createUser("creator");
        creator = userService.create(creator, creator);

        User target = UserServiceTest.createUser("viewer1");
        target = userService.create(target, target);

        Following following = new Following();
        following.setTarget(target);
        following.setCreator(creator);

        //creating following -> following notification for @target should be created
        following = followingService.create(following, creator);

        Thread.sleep(500);

        List<NotificationDTO> notificationDTOs = notificationService.listNotificationsOfUser(target.getId(), null,  PageRequest.of(0, 20), target, false);

        assertTrue(notificationDTOs != null);
        assertTrue(notificationDTOs.size() == 1);

        NotificationDTO notificationDTO = notificationDTOs.get(0);

        //showing that notification was correctly created
        assertTrue(notificationDTO.type == Notification.NotificationType.following);
        assertTrue(notificationDTO.contentEntityID.equals(following.getId()));
        assertTrue(notificationDTO.userDTO.getId().equals(creator.getId()));

        notificationDTOs = notificationService.listNotificationsOfUser(creator.getId(), null,  PageRequest.of(0, 20), creator, false);

        //not notification for @onwer should be created.
        assertTrue(notificationDTOs != null);
        assertTrue(notificationDTOs.size() == 0);
    }

    @Test
    public void test_listNotificationOfUser_shouldOK() throws InterruptedException {

        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        User viewer1 = UserServiceTest.createUser("viewer2");
        viewer1 = userService.create(viewer1, viewer1);

        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);
        viewers.add(viewer1);

        Post post = PostServiceTest.createPost(owner, viewers);
        post = postService.create(post, owner);

        PostFeedbackVote postFeedbackVote = PostServiceTest.createVote(post.getPostFeedbackPossibilities().get(0), viewer);

        postFeedbackVote = postFeedbackVoteService.create(postFeedbackVote, viewer);

        Thread.sleep(500); // waiting till notification is created

        PostFeedbackVote postFeedbackVote1 = PostServiceTest.createVote(post.getPostFeedbackPossibilities().get(1), viewer1);

        postFeedbackVote1 = postFeedbackVoteService.create(postFeedbackVote1, viewer1);

        Thread.sleep(500); // waiting till notification is created

        List<NotificationDTO> notificationDTOs = notificationService.listNotificationsOfUser(owner.getId(), null, PageRequest.of(0, 10), owner, false);

        assertTrue(notificationDTOs.size() == 1);

        NotificationDTO notificationDTO = notificationDTOs.get(0);

        assertTrue(notificationDTO.userDTO != null);
        assertTrue(notificationDTO.contentEntityID.equals(post.getId()));
        assertTrue(notificationDTO.count == 2);
        assertTrue(notificationDTO.type == Notification.NotificationType.post_feedback);

        UserDTO viewer1DTO = new UserDTO(viewer1);

        assertTrue(notificationDTO.userDTO.equals(viewer1DTO));

    }

    @Test
    public void test_listNotificationOfUser_fetchUnseen_shouldOK() throws InterruptedException {

        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        User viewer1 = UserServiceTest.createUser("viewer2");
        viewer1 = userService.create(viewer1, viewer1);

        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);
        viewers.add(viewer1);

        Post post = PostServiceTest.createPost(owner, viewers);
        post = postService.create(post, owner);

        PostFeedbackVote postFeedbackVote = PostServiceTest.createVote(post.getPostFeedbackPossibilities().get(0), viewer);

        postFeedbackVote = postFeedbackVoteService.create(postFeedbackVote, viewer);

        Thread.sleep(500);

        long ownerLastNotificationRefreshTimestamp = owner.getLastNotificationRefreshTimestamp();

        List<NotificationDTO> unseenNotifications = notificationService.listNotificationsOfUser(owner.getId(), null,  PageRequest.of(0, 20), owner, true);

        assertTrue(unseenNotifications != null);
        assertTrue(unseenNotifications.size() == 1);

        //only one unseen notification should be returned
        NotificationDTO notificationDTO = unseenNotifications.get(0);

        assertTrue(notificationDTO.contentEntityID.equals( post.getId()));
        assertTrue(notificationDTO.type == Notification.NotificationType.post_feedback);
        assertTrue(notificationDTO.userDTO != null);
        assertTrue(notificationDTO.userDTO.id.equals(viewer.getId()));
        assertTrue(notificationDTO.count == 1);

        User getUser = userService.get(owner.getId());
        assertTrue(getUser.getLastNotificationRefreshTimestamp() != ownerLastNotificationRefreshTimestamp);//the last notification refresh timestamp was updated
        ownerLastNotificationRefreshTimestamp = getUser.getLastNotificationRefreshTimestamp();

        //creating new post feedback action
        PostFeedbackVote postFeedbackVote1 = PostServiceTest.createVote(post.getPostFeedbackPossibilities().get(1), viewer1);

        postFeedbackVote1 = postFeedbackVoteService.create(postFeedbackVote1, viewer1);

        Thread.sleep(500);

        unseenNotifications = notificationService.listNotificationsOfUser(owner.getId(), null,  PageRequest.of(0, 20), owner, true);

        assertTrue(unseenNotifications != null);
        assertTrue(unseenNotifications.size() == 1);

        NotificationDTO nextNotification = unseenNotifications.get(0);

        assertTrue(nextNotification.getId().equals(notificationDTO.getId()));//id should stay the same
        assertTrue(nextNotification.id.equals( notificationDTO.getId()));
        assertTrue(nextNotification.type == Notification.NotificationType.post_feedback);
        assertTrue(nextNotification.count == 2);
        assertTrue(nextNotification.contentEntityID.equals(post.getId()));
        assertTrue(nextNotification.userDTO != null);
        assertTrue(nextNotification.userDTO.getId().equals(viewer1.getId()));

        getUser = userService.get(owner.getId());
        assertTrue(getUser.getLastNotificationRefreshTimestamp() != ownerLastNotificationRefreshTimestamp);//the last notification refresh timestamp was updated
    }


    @Test
    public void test_listNotificationOfUser_fetchUnseen2_shouldOK() throws InterruptedException {

        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        User viewer1 = UserServiceTest.createUser("viewer2");
        viewer1 = userService.create(viewer1, viewer1);

        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);
        viewers.add(viewer1);

        Post post = PostServiceTest.createPost(owner, viewers);
        post = postService.create(post, owner);

        Post post1 = PostServiceTest.createPost(owner, viewers);
        post1 = postService.create(post1, owner);

        //creating post feedback on post
        PostFeedbackVote postFeedbackVote = PostServiceTest.createVote(post.getPostFeedbackPossibilities().get(0), viewer);

        postFeedbackVote = postFeedbackVoteService.create(postFeedbackVote, viewer);

        long ownerLastNotificationRefreshTimestamp = owner.getLastNotificationRefreshTimestamp();

        Thread.sleep(500);

        List<NotificationDTO> unseenNotifications = notificationService.listNotificationsOfUser(owner.getId(), null,  PageRequest.of(0, 20), owner, true);

        assertTrue(unseenNotifications != null);
        assertTrue(unseenNotifications.size() == 1);

        //only one unseen notification should be returned
        NotificationDTO notificationDTO = unseenNotifications.get(0);

        assertTrue(notificationDTO.contentEntityID.equals( post.getId()));
        assertTrue(notificationDTO.type == Notification.NotificationType.post_feedback);
        assertTrue(notificationDTO.userDTO != null);
        assertTrue(notificationDTO.userDTO.id.equals(viewer.getId()));
        assertTrue(notificationDTO.count == 1);

        User getUser = userService.get(owner.getId());
        assertTrue(getUser.getLastNotificationRefreshTimestamp() != ownerLastNotificationRefreshTimestamp);//the last notification refresh timestamp was updated
        ownerLastNotificationRefreshTimestamp = getUser.getLastNotificationRefreshTimestamp();

        //creating new post feedback action on post1
        PostFeedbackVote postFeedbackVote1 = PostServiceTest.createVote(post1.getPostFeedbackPossibilities().get(1), viewer1);

        postFeedbackVote1 = postFeedbackVoteService.create(postFeedbackVote1, viewer1);

        Thread.sleep(500);

        unseenNotifications = notificationService.listNotificationsOfUser(owner.getId(), null,  PageRequest.of(0, 20), owner, true);

        assertTrue(unseenNotifications != null);
        assertTrue(unseenNotifications.size() == 1);

        NotificationDTO nextNotification = unseenNotifications.get(0);

        assertFalse(nextNotification.getId().equals(notificationDTO.getId()));//nextNotification is new notification, because it' created by voting on new post (@post1).
        assertTrue(nextNotification.type == Notification.NotificationType.post_feedback);
        assertTrue(nextNotification.count == 1);
        assertTrue(nextNotification.contentEntityID.equals( post1.getId()));
        assertTrue(nextNotification.userDTO != null);
        assertTrue(nextNotification.userDTO.getId().equals(viewer1.getId()));


    }

    @Test
    public void test_listNotificationOfUser_afterPostDeleted() {

        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);

        Post post = PostServiceTest.createPost(owner, viewers);
        post = postService.create(post, owner);

        //creating post feedback on post
        PostFeedbackVote postFeedbackVote = PostServiceTest.createVote(post.getPostFeedbackPossibilities().get(0), viewer);

        postFeedbackVote = postFeedbackVoteService.create(postFeedbackVote, viewer);

        //deleting post -> PostFeedback should also be deleted
        postService.delete(post.getId(), owner);

        Page<PostFeedbackVote> postFeedbackPage = postFeedbackVoteService.findByUserPaging(owner.getId(),  PageRequest.of(0, 20), owner, -1l);

        assertTrue(postFeedbackPage != null);
        assertTrue(postFeedbackPage.getContent().size() == 0);
        //@postFeedback should be also deleted
    }




}
