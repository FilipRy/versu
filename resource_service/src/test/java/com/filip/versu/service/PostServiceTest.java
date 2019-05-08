package com.filip.versu.service;


import com.filip.versu.entity.model.*;
import com.filip.versu.exception.ForbiddenException;
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

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PostServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private FollowingService followingService;

    @Autowired
    private PostFeedbackVoteService postFeedbackVoteService;

    @Autowired
    private PostFeedbackPossibilityService postFeedbackPossibilityService;

    @Autowired
    private CommentService commentService;

    @Test
    public void test_create_shouldOK() {

        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        Post post = createPost(owner, null);

        post = postService.create(post, owner);

        Post postExisting = postService.get(post.getId());

        assertTrue(postExisting.equals(post));

    }

    @Test
    public void test_createVotingPost_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        Post post = createVotingShoppingItem(owner, null);

        List<PostFeedbackPossibility> feedbackPossbilitiesBeforeCreate = post.getPostFeedbackPossibilities();

        post = postService.create(post, owner);

        assertTrue(post.getPostFeedbackPossibilities().size() == 2);
        assertTrue(post.getPostFeedbackPossibilities().get(0).equals(feedbackPossbilitiesBeforeCreate.get(0)));
        assertTrue(post.getPostFeedbackPossibilities().get(1).equals(feedbackPossbilitiesBeforeCreate.get(1)));
    }


//    @Test
//    public void test_getDetailsBySecretUrl_shouldOK() {
//        User owner = UserServiceTest.createUser("owner");
//        owner = userService.create(owner, owner);
//
//        Post post = createPost(owner, null);
//        post.setSecretUrl("generate me");
//
//        post = postService.create(post, owner);
//
//        User userWithLink = new User();
//
//        userWithLink.setId(-1l);//initialize with some invalid id here
//        userWithLink.setUserRole(User.UserRole.USER_WITH_LINK);
//        userWithLink.setSecretUrl(post.getSecretUrl());
//
//        Post getPost = postService.getDetailsBySecretUrl(post.getSecretUrl(), userWithLink);
//
//        assertTrue(getPost.equals(post));
//    }

//    @Test
//    public void test_getDetailsBySecretUrl_SecretLinkGivenToNonViewer_shouldOK() {
//
//        User owner = UserServiceTest.createUser("owner");
//        owner = userService.create(owner, owner);
//
//        //this is a non-viewer user, but this user received the link.
//        User viewer = UserServiceTest.createUser("viewer1");
//        viewer = userService.create(viewer, viewer);
//        viewer.setUserRole(User.UserRole.USER_WITH_LINK);
//
//        Post post = createPost(owner, null);
//        post.setAccessType(Post.AccessType.FOLLOWERS);
//        post.setSecretUrl("generate me");
//
//        post = postService.create(post, owner);
//
//        viewer.setSecretUrl(post.getSecretUrl());
//
//        Post getPost = postService.getDetailsBySecretUrl(post.getSecretUrl(), viewer);
//
//        assertTrue(getPost.equals(post));
//    }

    @Test
    public void test_findPostsVisibleForViewer_owner_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        Post post = createPost(owner, null);

        post = postService.create(post, owner);

        Page<Post> shoppingItems = postService.findPostsVisibleForViewer(owner.getId(), PageRequest.of(0, 20), owner, null);

        assertTrue(shoppingItems.getContent().contains(post));

    }

    /**
     * Owner creates a public shopping item.
     */
    @Test
    public void test_findPostsVisibleForViewer_public_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        Post post = PostServiceTest.createPost(owner, null);
        post.setAccessType(Post.AccessType.PUBLICC);
        post = postService.create(post, owner);

        Page<Post> shoppingItemPage = postService.findPostsVisibleForViewer(owner.getId(),PageRequest.of(0, 20), viewer, null);

        /**
         * shoppingitem should be visible for anyone
         */
        assertTrue(shoppingItemPage.getContent().contains(post));
    }

    /**
     * Owner creates a shopping item visible only for his/her FOLLOWERS.
     */
    @Test
    public void test_findPostsVisibleForViewer_followers_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        Following following = new Following();
        following.setCreator(viewer);
        following.setTarget(owner);

        following = followingService.create(following, viewer);

        Post post = PostServiceTest.createPost(owner, null);
        post.setAccessType(Post.AccessType.FOLLOWERS);
        post = postService.create(post, owner);

        Page<Post> shoppingItemPage = postService.findPostsVisibleForViewer(owner.getId(),PageRequest.of(0, 20), viewer, null);

        assertTrue(shoppingItemPage.getContent().contains(post));

    }


    @Test
    public void test_findPostsVisibleForViewer_shouldReturnListWithMyFavouriteMyVoteYes() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        Following following = new Following();
        following.setCreator(viewer);
        following.setTarget(owner);

        following = followingService.create(following, viewer);

        Post post = PostServiceTest.createPost(owner, null);
        post.setAccessType(Post.AccessType.FOLLOWERS);
        post = postService.create(post, owner);

        PostFeedbackVote postFeedbackVote = createVote(post.getPostFeedbackPossibilities().get(0), viewer);

        postFeedbackVote = postFeedbackVoteService.create(postFeedbackVote, viewer);

        Page<Post> shoppingItems = postService.findPostsVisibleForViewer(owner.getId(),PageRequest.of(0, 20), viewer, null);

        assertTrue(shoppingItems.getContent().contains(post));

        boolean found = false;
        for (Post si : shoppingItems) {
            if (si.equals(post)) {
                assertTrue(si.getMyPostFeedbackVote().equals(postFeedbackVote));
                found = true;
            }
        }

        assertTrue(found);

    }

    @Test
    public void test_findPostsVisibleForViewer_postDeleted_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        Post post = createPost(owner, null);

        post = postService.create(post, owner);

        /**
         * deleting shopping item
         */
        postService.delete(post.getId(), owner);

        /**
         * the deleted shopping item should not be found.
         */
        Page<Post> shoppingItems = postService.findPostsVisibleForViewer(owner.getId(), PageRequest.of(0, 10), owner, null);

        assertFalse(shoppingItems.getContent().contains(post));

    }


    @Test
    public void test_findForUserByTime_public_shouldNotReturnPublicPosts() {

        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        Post post = PostServiceTest.createPost(owner, null);
        post.setAccessType(Post.AccessType.PUBLICC);
        post = postService.create(post, owner);

        Page<Post> postForViewer = postService.findForUserByTime(viewer.getId(), null,PageRequest.of(0, 20), viewer);

        assertTrue(postForViewer != null);
        assertTrue(postForViewer.getContent().size() == 0);
        //post should not be visible in viewer's timeline, because it's PUBLICC and there is no following between viewer and owner


    }

    @Test
    public void test_findForUserByTime_public_shouldReturnPublicPosts() {

        User owner1 = UserServiceTest.createUser("owner1");
        owner1 = userService.create(owner1, owner1);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);


        //viewer follows owner1
        Following following = new Following();
        following.setCreator(viewer);
        following.setTarget(owner1);

        following = followingService.create(following, viewer);

        Post post = PostServiceTest.createPost(owner1, null);
        post.setAccessType(Post.AccessType.PUBLICC);
        post = postService.create(post, owner1);

        Page<Post> postForViewer = postService.findForUserByTime(viewer.getId(), null,PageRequest.of(0, 20), viewer);

        assertTrue(postForViewer != null);
        assertTrue(postForViewer.getContent().size() == 1);
        //post should be visible in viewer's timeline, because it's PUBLICC

        Post post1 = postForViewer.getContent().get(0);

        assertTrue(post1.equals(post));

    }

    @Test
    public void test_findForUserByTime_followers_createFollowing_shouldOK() {

        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);


        //viewer follows owner
        Following following = new Following();
        following.setCreator(viewer);
        following.setTarget(owner);

        following = followingService.create(following, viewer);

        Post post = PostServiceTest.createPost(owner, null);
        post.setAccessType(Post.AccessType.FOLLOWERS);
        post = postService.create(post, owner);

        Page<Post> postForViewer = postService.findForUserByTime(viewer.getId(), null,PageRequest.of(0, 20), viewer);

        assertTrue(postForViewer != null);
        assertTrue(postForViewer.getContent().contains(post));

        User viewer2 = UserServiceTest.createUser("viewer2");
        viewer2 = userService.create(viewer2, viewer2);

        //viewer2 follows owner
        Following following1 = new Following();
        following1.setCreator(viewer2);
        following1.setTarget(owner);

        following1 = followingService.create(following1, viewer2);


        postForViewer = postService.findForUserByTime(viewer.getId(), null,PageRequest.of(0, 20), viewer);
        Page<Post> postForViewer2 = postService.findForUserByTime(viewer2.getId(), null,PageRequest.of(0, 20), viewer2);

        assertTrue(postForViewer != null);
        assertTrue(postForViewer2 != null);

        //@post should be visible for viewer & viewer2
        assertTrue(postForViewer.getContent().contains(post));
        assertTrue(postForViewer2.getContent().contains(post));
    }


    @Test
    public void test_findForUserByTime_followers_removeFollowing_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);


        //viewer follows owner
        Following following = new Following();
        following.setCreator(viewer);
        following.setTarget(owner);

        following = followingService.create(following, viewer);

        Post post = PostServiceTest.createPost(owner, null);
        post.setAccessType(Post.AccessType.FOLLOWERS);
        post = postService.create(post, owner);

        Page<Post> postForViewer = postService.findForUserByTime(viewer.getId(), null,PageRequest.of(0, 20), viewer);

        assertTrue(postForViewer != null);
        assertTrue(postForViewer.getContent().contains(post));

        //following between owner and viewer deleted
        followingService.delete(following.getId(), following.getCreator());

        postForViewer = postService.findForUserByTime(viewer.getId(), null,PageRequest.of(0, 20), viewer);

        //post shouldn't be visible for viewer anymore
        assertTrue(postForViewer != null);
        assertFalse(postForViewer.getContent().contains(post));

    }


    @Test
    public void test_findForUserByTime_followers_updateToSpecific_shouldOK() {

        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);
        User viewer2 = UserServiceTest.createUser("viewer2");
        viewer2 = userService.create(viewer2, viewer2);

        //viewer follows owner
        Following following = new Following();
        following.setCreator(viewer);
        following.setTarget(owner);

        //viewer2 follows owner
        Following following1 = new Following();
        following1.setCreator(viewer2);
        following1.setTarget(owner);

        following = followingService.create(following, viewer);
        following1 = followingService.create(following1, viewer2);

        //creating post visible for FOLLOWERS
        Post post = PostServiceTest.createPost(owner, null);
        post.setAccessType(Post.AccessType.FOLLOWERS);
        post = postService.create(post, owner);


        Page<Post> postForViewer = postService.findForUserByTime(viewer.getId(), null,PageRequest.of(0, 20), viewer);
        Page<Post> postForViewer2 = postService.findForUserByTime(viewer2.getId(), null,PageRequest.of(0, 20), viewer2);

        assertTrue(postForViewer != null);
        assertTrue(postForViewer2 != null);

        //@post should be visible for viewer & viewer2
        assertTrue(postForViewer.getContent().contains(post));
        assertTrue(postForViewer2.getContent().contains(post));


        //post is now visible only for @viewer
        post.setAccessType(Post.AccessType.SPECIFIC);
        post.getViewers().add(viewer);

        post = postService.update(post, owner);

        List<User> viewers = postService.listViewers(post.getId(), owner);

        assertTrue(viewers.contains(viewer));
        assertTrue(viewers.size() == 1);


        postForViewer = postService.findForUserByTime(viewer.getId(), null,PageRequest.of(0, 20), viewer);
        postForViewer2 = postService.findForUserByTime(viewer2.getId(), null,PageRequest.of(0, 20), viewer2);

        //After update: @post should be visible only for viewer
        assertTrue(postForViewer.getContent().contains(post));
        assertFalse(postForViewer2.getContent().contains(post));
    }

    @Test
    public void test_findForUserByTime_specific_updateViewers_shouldOK() {
        fail("not yet implemented");
    }

    @Test
    public void test_findForUserByTime_includeTwoMostRecentComments_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        Following following = new Following();
        following.setCreator(viewer);
        following.setTarget(owner);

        following = followingService.create(following, viewer);

        Post post = PostServiceTest.createPost(owner, null);
        post.setAccessType(Post.AccessType.FOLLOWERS);
        post = postService.create(post, owner);

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setOwner(viewer);
        comment.setContent("com1");
        comment = commentService.create(comment, viewer);

        Comment comment1 = new Comment();
        comment1.setPost(post);
        comment1.setOwner(viewer);
        comment1.setContent("com2");
        comment1 = commentService.create(comment1, viewer);

        Comment comment2 = new Comment();
        comment2.setPost(post);
        comment2.setOwner(viewer);
        comment2.setContent("com3");
        comment2 = commentService.create(comment2, viewer);

        Page<Post> shoppingItemPage = postService.findForUserByTime(viewer.getId(), null,PageRequest.of(0, 20), viewer);

        assertTrue(shoppingItemPage.getContent().contains(post));

        boolean found = false;
        for (Post post1 : shoppingItemPage.getContent()) {
            if (post1.getId().equals(post.getId())) {
                found = true;
                //the shopping item contains the 2 most recent comments
                assertTrue(post1.getMostRecentComment().contains(comment1));
                assertTrue(post1.getMostRecentComment().contains(comment2));

                //the 1st comment is not loaded
                assertFalse(post1.getMostRecentComment().contains(comment));
            }
        }
        assertTrue(found);
    }


    @Test
    public void test_findForUserByLocation_shouldOK() {

        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        Post post = PostServiceTest.createPost(owner, null);
        post.setAccessType(Post.AccessType.PUBLICC);

        GoogleLocation location = new GoogleLocation();
        location.setLatitude(48.140401);
        location.setLongitude(17.121373);

        post.setLocation(location);//post is created at Bratislava, Eurovea

        post = postService.create(post, owner);

        /**
         * Making request from Bratislava Caste, @post should be visible from here
         */
        Page<Post> postPage = postService.findForUserByLocation(viewer.getId(),PageRequest.of(0, 20), viewer, 48.142948, 17.099373);

        assertTrue(postPage.getContent().contains(post));

    }


    @Test
    public void test_findForUserByLocation_testPaging_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        Post post = PostServiceTest.createPost(owner, null);
        post.setAccessType(Post.AccessType.PUBLICC);

        //distance to me 8.1146
        GoogleLocation location = new GoogleLocation();
        location.setLatitude(48.13499468);
        location.setLongitude(17.23101718);

        post.setLocation(location);

        post = postService.create(post, owner);

        Post post1 = PostServiceTest.createPost(owner, null);
        post1.setAccessType(Post.AccessType.PUBLICC);

        //distance to me 19.844
        GoogleLocation location1 = new GoogleLocation();
        location1.setLatitude(48.070942);
        location1.setLongitude(16.87559347);
        post1.setLocation(location1);

        post1 = postService.create(post1, owner);

        Post post2 = PostServiceTest.createPost(owner, null);
        post2.setAccessType(Post.AccessType.PUBLICC);

        //distance to me = 11.3203
        GoogleLocation location2 = new GoogleLocation();
        location2.setLatitude(48.17828152);
        location2.setLongitude(17.26331606);
        post2.setLocation(location2);

        post2 = postService.create(post2, owner);

        Post post3 = PostServiceTest.createPost(owner, null);
        post3.setAccessType(Post.AccessType.PUBLICC);

        //distance to me = 17.8431
        GoogleLocation location3 = new GoogleLocation();
        location3.setLatitude(47.98964446);
        location3.setLongitude(17.03822233);
        post3.setLocation(location3);

        post3 = postService.create(post3, owner);

        Post post4 = PostServiceTest.createPost(owner, null);
        post4.setAccessType(Post.AccessType.PUBLICC);

        //distance to me = 14.1401
        GoogleLocation location4 = new GoogleLocation();
        location4.setLatitude(48.24567076);
        location4.setLongitude(17.01594667);
        post4.setLocation(location4);

        post4 = postService.create(post4, owner);

        //my location = 48.139991, 17.121951

        Page<Post> postPage = postService.findForUserByLocation(viewer.getId(),  PageRequest.of(0, 2), viewer, 48.139991, 17.121951);
        //this postPage should contain the two nearest post
        assertTrue(postPage.getContent().contains(post));
        assertTrue(postPage.getContent().contains(post2));
        assertFalse(postPage.getContent().contains(post1));
        assertFalse(postPage.getContent().contains(post3));
        assertFalse(postPage.getContent().contains(post4));

        postPage = postService.findForUserByLocation(viewer.getId(),  PageRequest.of(1, 2), viewer, 48.139991, 17.121951);
        assertFalse(postPage.getContent().contains(post));
        assertFalse(postPage.getContent().contains(post1));
        assertFalse(postPage.getContent().contains(post2));
        assertTrue(postPage.getContent().contains(post3));
        assertTrue(postPage.getContent().contains(post4));

        postPage = postService.findForUserByLocation(viewer.getId(),  PageRequest.of(2, 2), viewer, 48.139991, 17.121951);
        assertFalse(postPage.getContent().contains(post));
        assertTrue(postPage.getContent().contains(post1));
        assertFalse(postPage.getContent().contains(post2));
        assertFalse(postPage.getContent().contains(post3));
        assertFalse(postPage.getContent().contains(post4));
    }

    @Test
    public void test_findVSPossibilities_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        Following following = new Following();
        following.setCreator(viewer);
        following.setTarget(owner);

        following = followingService.create(following, viewer);

        Post post = PostServiceTest.createPost(owner, null);

        post.setAccessType(Post.AccessType.FOLLOWERS);
        post.setPostFeedbackPossibilities(new ArrayList<>());
        post.getPostFeedbackPossibilities().add(createPossibility("possibilityInVS1", post));
        post.getPostFeedbackPossibilities().add(createPossibility("possibilityInVS2", post));

        post = postService.create(post, owner);

        List<String[]> possibilities = postService.findVSPossibilities("possibility", viewer);

        assertTrue(possibilities != null);
        assertTrue(possibilities.size() == 1);
        String[] resultPossibilities = possibilities.get(0);
        //the returned possibilities are the same as at @post
        assertTrue(resultPossibilities[0].equals("possibilityInVS1".toLowerCase()) || resultPossibilities[0].equals("possibilityInVS2".toLowerCase()));
        assertTrue(resultPossibilities[1].equals("possibilityInVS1".toLowerCase()) || resultPossibilities[1].equals("possibilityInVS2".toLowerCase()));

        possibilities = postService.findVSPossibilities("possibilityInVS1VSposs", viewer);

        assertTrue(possibilities != null);
        assertTrue(possibilities.size() == 1);
        resultPossibilities = possibilities.get(0);
        //the returned possibilities are the same as at @post
        assertTrue(resultPossibilities[0].equals("possibilityInVS1".toLowerCase()) || resultPossibilities[0].equals("possibilityInVS2".toLowerCase()));
        assertTrue(resultPossibilities[1].equals("possibilityInVS1".toLowerCase()) || resultPossibilities[1].equals("possibilityInVS2".toLowerCase()));

    }


    @Test
    public void test_findBySecretUrl_WithNullAsSecretUrl() {

        Post post = postService.getPostBySecretUrl(null);

        assertTrue(post == null);

    }

    @Test
    public void test_findByFeedbackPossibilitiesName_twoPossiblitiesName_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        Following following = new Following();
        following.setCreator(viewer);
        following.setTarget(owner);

        following = followingService.create(following, viewer);

        Post post = PostServiceTest.createPost(owner, null);

        post.setAccessType(Post.AccessType.FOLLOWERS);
        post.setPostFeedbackPossibilities(new ArrayList<>());
        post.getPostFeedbackPossibilities().add(createPossibility("possA", post));
        post.getPostFeedbackPossibilities().add(createPossibility("possB", post));

        post = postService.create(post, owner);

        Post post1 = PostServiceTest.createPost(owner, null);
        post1.setAccessType(Post.AccessType.FOLLOWERS);
        post1.getPostFeedbackPossibilities().clear();
        post1.getPostFeedbackPossibilities().add(createPossibility("possA", post1));
        post1.getPostFeedbackPossibilities().add(createPossibility("smthngOther", post1));

        post1 = postService.create(post1, owner);

        Post post2 = PostServiceTest.createPost(owner, null);
        post2.setAccessType(Post.AccessType.FOLLOWERS);
        post2.getPostFeedbackPossibilities().clear();
        post2.getPostFeedbackPossibilities().add(createPossibility("possB", post2));
        post2.getPostFeedbackPossibilities().add(createPossibility("blabla", post2));

        post2 = postService.create(post2, owner);

        Post post3 = PostServiceTest.createPost(owner, null);
        post3.setAccessType(Post.AccessType.FOLLOWERS);

        post3.getPostFeedbackPossibilities().clear();
        post3.getPostFeedbackPossibilities().add(createPossibility("blabla2", post3));
        post3.getPostFeedbackPossibilities().add(createPossibility("blabla", post3));

        post3 = postService.create(post3, owner);

        Post post4 = PostServiceTest.createPost(owner, null);
        post4.setAccessType(Post.AccessType.FOLLOWERS);

        post4.getPostFeedbackPossibilities().clear();
        post4.getPostFeedbackPossibilities().add(createPossibility("possA", post4));
        post4.getPostFeedbackPossibilities().add(createPossibility("possB", post4));

        post4.setPublishTime(post.getPublishTime() + 100000);

        post4 = postService.create(post4, owner);

        Page<Post> postPage = postService.findByFeedbackPossibilitiesName("possa", "possb", viewer,PageRequest.of(0, 20));

        assertTrue(postPage != null);
        assertTrue(postPage.getContent().size() == 4);

        assertTrue(postPage.getContent().get(0).equals(post4));//most relevant & most recent post needs to be first
        assertTrue(postPage.getContent().get(1).equals(post));

        assertTrue(postPage.getContent().contains(post1));
        assertTrue(postPage.getContent().contains(post2));

    }

    @Test
    public void test_findByFeedbackPossibilitiesName_onePossibilityName_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        Following following = new Following();
        following.setCreator(viewer);
        following.setTarget(owner);

        following = followingService.create(following, viewer);

        Post post = PostServiceTest.createPost(owner, null);

        post.setAccessType(Post.AccessType.FOLLOWERS);

        post.setPostFeedbackPossibilities(new ArrayList<>());
        post.getPostFeedbackPossibilities().add(createPossibility("possA", post));
        post.getPostFeedbackPossibilities().add(createPossibility("possB", post));


        post = postService.create(post, owner);

        Post post2 = PostServiceTest.createPost(owner, null);
        post2.setAccessType(Post.AccessType.FOLLOWERS);

        post2.getPostFeedbackPossibilities().clear();
        post2.getPostFeedbackPossibilities().add(createPossibility("blabla2", post));
        post2.getPostFeedbackPossibilities().add(createPossibility("blabla", post));

        post2 = postService.create(post2, owner);

        Page<Post> postPage = postService.findByFeedbackPossibilitiesName("possa", null, viewer,PageRequest.of(0, 20));

        assertTrue(postPage != null);
        assertTrue(postPage.getContent().size() == 1);

        assertTrue(postPage.getContent().get(0).equals(post));

    }


    @Test
    public void test_findByFeedbackPossibilitiesName_onePossibilityName2_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        User nonViewer = UserServiceTest.createUser("non-viewer");
        nonViewer = userService.create(nonViewer, nonViewer);

        Following following = new Following();
        following.setCreator(viewer);
        following.setTarget(owner);

        following = followingService.create(following, viewer);

        Post post = PostServiceTest.createPost(owner, null);
        post.setAccessType(Post.AccessType.FOLLOWERS);


        post.setPostFeedbackPossibilities(new ArrayList<>());
        post.getPostFeedbackPossibilities().add(createPossibility("poss1", post));
        post.getPostFeedbackPossibilities().add(createPossibility("poss2", post));

        post = postService.create(post, owner);

        Post publicPost = PostServiceTest.createPost(owner, null);
        publicPost.setAccessType(Post.AccessType.PUBLICC);

        publicPost.getPostFeedbackPossibilities().clear();
        publicPost.getPostFeedbackPossibilities().add(createPossibility("poss1", publicPost));
        publicPost.getPostFeedbackPossibilities().add(createPossibility("poss2", publicPost));


        publicPost = postService.create(publicPost, owner);

        Page<Post> postPage = postService.findByFeedbackPossibilitiesName("poss1", null, viewer,PageRequest.of(0, 20));
        Page<Post> postPage1 = postService.findByFeedbackPossibilitiesName("poss1", null, nonViewer,PageRequest.of(0, 20));

        Page<Post> postPage2 = postService.findByFeedbackPossibilitiesName("poss1", null, owner,PageRequest.of(0, 20));

        /**
         * viewer can see both publicPost and post
         */
        assertTrue(postPage.getContent().contains(post));
        assertTrue(postPage.getContent().contains(publicPost));

        /**
         * nonViewer can see only publicPost
         */
        assertTrue(postPage1.getContent().contains(publicPost));
        assertFalse(postPage1.getContent().contains(post));

        /**
         * owner can see his posts
         */
        assertTrue(postPage2.getContent().contains(post));
        assertTrue(postPage2.getContent().contains(publicPost));

    }

    @Test
    public void test_findByLocationGoogleId_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        Following following = new Following();
        following.setCreator(viewer);
        following.setTarget(owner);

        following = followingService.create(following, viewer);

        String googleID = "ChIJhT-9kj-JbEcRFjPcvf5V47s";

        GoogleLocation postLocation = new GoogleLocation();
        postLocation.setGoogleID(googleID);

        Post post = PostServiceTest.createPost(owner, null);
        post.setAccessType(Post.AccessType.FOLLOWERS);
        post.setLocation(postLocation);

        post = postService.create(post, owner);

        GoogleLocation postLocation1 = new GoogleLocation();
        postLocation.setGoogleID("some id here");

        Post post2 = PostServiceTest.createPost(owner, null);
        post2.setAccessType(Post.AccessType.FOLLOWERS);
        post2.setLocation(postLocation1);

        post2 = postService.create(post2, owner);

        Page<Post> postPage = postService.findByLocationGoogleId(googleID, viewer,PageRequest.of(0, 20), null);

        assertTrue(postPage.getContent().contains(post));
        assertFalse(postPage.getContent().contains(post2));

    }

    @Test
    public void test_findByLocationGoogleId_findPostsByCity_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        Following following = new Following();
        following.setCreator(viewer);
        following.setTarget(owner);

        following = followingService.create(following, viewer);

        GoogleLocation location = new GoogleLocation();
        location.setGoogleID("ChIJhT-9kj-JbEcRFjPcvf5V47s");
        location.setLatitude(48.14050109999999);
        location.setLongitude(17.1213257);

        Post post = PostServiceTest.createPost(owner, null);
        post.setAccessType(Post.AccessType.FOLLOWERS);
        post.setLocation(location);

        //post is created in Bratislava, Eurovea
        post = postService.create(post, owner);


        GoogleLocation postLocation = new GoogleLocation();
        postLocation.setGoogleID("ChIJueLdxlRUbEcRck4wd2eRmCo");
        postLocation.setLatitude(48.120252);
        postLocation.setLongitude(16.5619684);

        Post post1 = PostServiceTest.createPost(owner, null);
        post1.setAccessType(Post.AccessType.FOLLOWERS);
        post1.setLocation(postLocation);

        //post is created in Schwechat, Austria
        post1 = postService.create(post1, owner);

        String googleIDBratislava = "ChIJl2HKCjaJbEcRaEOI_YKbH2M";

        //find posts in Bratislava
        Page<Post> postPage = postService.findByLocationGoogleId(googleIDBratislava, viewer,PageRequest.of(0, 20), null);

        //postPage should contain post created in Bratislava, Eurovea
        assertTrue(postPage.getContent().contains(post));
        //postPage should not contain post created in Schwechat, Austria
        assertFalse(postPage.getContent().contains(post1));

    }


    @Test
    public void test_listViewers_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);
        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);

        Post post = createPost(owner, viewers);

        post = postService.create(post, owner);

        List<User> getViewers = postService.listViewers(post.getId(), owner);

        assertTrue(getViewers.contains(viewer));

    }


    @Test
    public void test_listViewersEmpty_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        Post post = createPost(owner, null);

        post = postService.create(post, owner);

        List<User> getViewers = postService.listViewers(post.getId(), owner);

        assertTrue(getViewers.size() == 0);

    }

    @Test
    public void test_update_description_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        Post post = createPost(owner, null);

        post = postService.create(post, owner);

        post.setDescription("updated desc");

        post = postService.update(post, owner);

        Post getPost = postService.get(post.getId());

        assertTrue(post.equals(getPost));
    }

    @Test
    public void test_update_viewers_addNewViewer_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);
        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);

        Post post = createPost(owner, viewers);
        post.setAccessType(Post.AccessType.SPECIFIC);

        post = postService.create(post, owner);

        /**
         * adding new viewer
         */
        User viewer2 = UserServiceTest.createUser("viewer2");
        viewer2 = userService.create(viewer2, viewer2);
        post.getViewers().add(viewer2);

        post = postService.update(post, owner);

        List<User> getViewers = postService.listViewers(post.getId(), owner);

        assertTrue(getViewers.contains(viewer));
        assertTrue(getViewers.contains(viewer2));

    }

    /**
     * removing viewer + adding new viewer
     */
    @Test
    public void test_update_viewers_AddRemoveViewer_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        User viewer2 = UserServiceTest.createUser("viewer2");
        viewer = userService.create(viewer, viewer);
        viewer2 = userService.create(viewer2, viewer2);
        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);
        viewers.add(viewer2);

        Post post = createPost(owner, viewers);
        post.setAccessType(Post.AccessType.SPECIFIC);

        post = postService.create(post, owner);

        /**
         * creating new viewer3 and removing viewer2
         */
        User viewer3 = UserServiceTest.createUser("viewer3");
        viewer3 = userService.create(viewer3, viewer3);
        post.getViewers().add(viewer3);
        post.getViewers().remove(viewer2);

        post = postService.update(post, owner);

        List<User> getViewers = postService.listViewers(post.getId(), owner);

        assertTrue(getViewers.contains(viewer));
        assertFalse(getViewers.contains(viewer2));
        assertTrue(getViewers.contains(viewer3));

    }

    @Test
    public void test_update_changeChosenState_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        Post post = createPost(owner, null);
        post = postService.create(post, owner);
        post.setChosenFeedbackPossibility(post.getPostFeedbackPossibilities().get(0));

        post = postService.update(post, owner);
        assertTrue(post.getChosenFeedbackPossibility().equals(post.getPostFeedbackPossibilities().get(0)));

        Post getPost = postService.get(post.getId());

        assertTrue(getPost.equals(post));
    }

    @Test(expected = ForbiddenException.class)
    public void test_update_changeChosenState_shouldThrowException() {

        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        Post post = createPost(owner, null);
        post = postService.create(post, owner);

        Post post1 = createPost(owner, null);
        post1.getPostFeedbackPossibilities().get(0).setName("bla bla bla");
        post1 = postService.create(post1, owner);

        post.setChosenFeedbackPossibility(post1.getPostFeedbackPossibilities().get(0));

        /**
         * the exception should be thrown here, because the vote for non-existing possibility is to be created
         */
        postService.update(post, owner);
    }


    @Test
    public void test_delete_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        Post post = createPost(owner, null);

        post = postService.create(post, owner);

        postService.delete(post.getId(), owner);

        Post getPost = postService.get(post.getId());

        assertTrue(getPost == null);

    }

    @Test
    public void test_removeOfUser_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        Post post = createPost(owner, null);

        post = postService.create(post, owner);

        postService.removeOfUser(owner, owner);

        Post getPost = postService.get(post.getId());
        assertTrue(getPost == null);
    }


    @Test
    public void test_remove_shouldRemovePossibilities() {


        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        Following following = new Following();
        following.setCreator(viewer);
        following.setTarget(owner);

        following = followingService.create(following, viewer);

        Post post = PostServiceTest.createPost(owner, null);

        post.setAccessType(Post.AccessType.FOLLOWERS);
        post.getPostFeedbackPossibilities().clear();
        post.getPostFeedbackPossibilities().add(createPossibility("possA", post));
        post.getPostFeedbackPossibilities().add(createPossibility("possB", post));

        post = postService.create(post, owner);

        Page<Post> postPage = postService.findByFeedbackPossibilitiesName("possa", "possb", viewer,PageRequest.of(0, 20));

        //find by possibilities successfully returned post
        assertTrue(postPage != null);
        assertTrue(postPage.getContent().size() == 1);
        assertTrue(postPage.getContent().get(0).equals(post));


        postService.delete(post.getId(), owner);

        PostFeedbackPossibility possibility = postFeedbackPossibilityService.get(post.getPostFeedbackPossibilities().get(0).getId());

        assertTrue(possibility == null);//possibility should be deleted

        possibility = postFeedbackPossibilityService.get(post.getPostFeedbackPossibilities().get(1).getId());

        assertTrue(possibility == null);//another possibility should be also deleted


        List<String[]> foundPatterns = postService.findVSPossibilities("possa", viewer);

        assertTrue(foundPatterns != null);
        assertTrue(foundPatterns.size() == 0);//possA should be already deleted

    }


    public static Post createPost(User owner, List<User> viewers) {
        Post.Timer timer = new Post.Timer(System.currentTimeMillis(), 3600 * 1000);
        PostPhoto photo = new PostPhoto("http://myserver.com/somephoto.jpg", System.currentTimeMillis());

        Post post = new Post();

        List<PostPhoto> photos = new ArrayList<>();
        photo.setPost(post);
        photos.add(photo);

        PostFeedbackPossibility possibility1 = new PostFeedbackPossibility();
        possibility1.setName("good");
        possibility1.setPost(post);

        PostFeedbackPossibility possibility2 = new PostFeedbackPossibility();
        possibility2.setName("bad");
        possibility2.setPost(post);

        post.setPostFeedbackPossibilities(new ArrayList<>());
        post.getPostFeedbackPossibilities().add(possibility1);
        post.getPostFeedbackPossibilities().add(possibility2);


        post.setOwner(owner);
        post.setAccessType(Post.AccessType.SPECIFIC);
        post.setPhotos(photos);
        post.setTimer(timer);
        post.setDescription("some desc");

        GoogleLocation location = new GoogleLocation();
        location.setGoogleID("ChIJhT-9kj-JbEcRFjPcvf5V47s");
        location.setLatitude(48.14050109999999);
        location.setLongitude(17.1213257);

        post.setLocation(location);

        if (viewers != null) {
            post.setViewers(viewers);
        }

        return post;
    }

    public static Post createVotingShoppingItem(User owner, List<User> viewers) {
        Post post = createPost(owner, viewers);

        List<PostPhoto> photos = new ArrayList<>();
        PostPhoto photo = new PostPhoto("http://myserver.com/somephoto.jpg", System.currentTimeMillis());
        photo.setPost(post);
        PostPhoto photo2 = new PostPhoto("http://myserver.com/somephoto2.jpg", System.currentTimeMillis());
        photo2.setPost(post);

        photos.add(photo);
        photos.add(photo2);

        post.setPhotos(photos);

        return post;
    }


    public static PostFeedbackVote createVote(PostFeedbackPossibility possibility, User user) {
        PostFeedbackVote postFeedbackVote = new PostFeedbackVote();
        postFeedbackVote.setOwner(user);
        postFeedbackVote.setPostFeedbackPossibility(possibility);
        postFeedbackVote.setTimestamp(System.currentTimeMillis());

        return postFeedbackVote;
    }

    public static PostFeedbackPossibility createPossibility(String name, Post post) {
        PostFeedbackPossibility possibility1 = new PostFeedbackPossibility();
        possibility1.setName(name);
        possibility1.setPost(post);
        return possibility1;
    }

}
