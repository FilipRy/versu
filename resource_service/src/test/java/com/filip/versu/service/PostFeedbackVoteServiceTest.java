package com.filip.versu.service;

import com.filip.versu.entity.model.Following;
import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.PostFeedbackVote;
import com.filip.versu.entity.model.User;
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

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PostFeedbackVoteServiceTest {

    @Autowired
    private FollowingService followingService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private PostFeedbackVoteService postFeedbackVoteService;

    @Test
    public void test_createPostFeedback_shouldOK() {

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

        PostFeedbackVote postFeedbackVote = PostServiceTest.createVote(post.getPostFeedbackPossibilities().get(0), viewer);


        postFeedbackVote = postFeedbackVoteService.create(postFeedbackVote, viewer);

        assertTrue(postFeedbackVote.getId() != null);

        PostFeedbackVote existingPostFeedbackVote = postFeedbackVoteService.get(postFeedbackVote.getId());

        assertTrue(postFeedbackVote.equals(existingPostFeedbackVote));

    }


    @Test
    public void test_createPostFeedback_onVotingPost() {
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

        PostFeedbackVote postFeedbackVote = PostServiceTest.createVote(post.getPostFeedbackPossibilities().get(0), viewer);

        /**
         * creating feedback vote for the first possibility
         */
        postFeedbackVote = postFeedbackVoteService.create(postFeedbackVote, viewer);

        PostFeedbackVote postFeedbackVote1 = PostServiceTest.createVote(post.getPostFeedbackPossibilities().get(0), viewer);

        /**
         * creating feedback vote for the second possibility -> first possibility vote should be deleted
         */
        postFeedbackVote1 = postFeedbackVoteService.create(postFeedbackVote1, viewer);

        PostFeedbackVote shouldBeDeleted = postFeedbackVoteService.get(postFeedbackVote.getId());
        PostFeedbackVote existingPostFeedbackVote = postFeedbackVoteService.get(postFeedbackVote1.getId());

        /**
         * showing that first feedback vote is deleted
         */
        assertTrue(shouldBeDeleted == null);

        /**
         * second feedback vote is created
         */
        assertTrue(existingPostFeedbackVote != null);
        assertTrue(existingPostFeedbackVote.equals(postFeedbackVote1));

    }


    @Test(expected = ForbiddenException.class)
    public void test_createPostFeedbackOnChosenPost_shouldThrownException() {

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

        post.setChosenFeedbackPossibility(post.getPostFeedbackPossibilities().get(0));

        /**
         * marking post as chosen
         */
        post = postService.update(post, owner);

        PostFeedbackVote postFeedbackVote = PostServiceTest.createVote(post.getPostFeedbackPossibilities().get(0), viewer);

        postFeedbackVote = postFeedbackVoteService.create(postFeedbackVote, viewer);
    }

    @Test
    public void test_createPostFeedback_onAlreadyVotedPost() {
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

        PostFeedbackVote postFeedbackVote = PostServiceTest.createVote(post.getPostFeedbackPossibilities().get(0), viewer);

        //creating first feedback - OK
        postFeedbackVote = postFeedbackVoteService.create(postFeedbackVote, viewer);

        PostFeedbackVote postFeedbackVote1 = PostServiceTest.createVote(post.getPostFeedbackPossibilities().get(1), viewer);

        //creating postFeedback on post, which was already used for voting, this should delete postFeedback and create postFeedback1
        postFeedbackVote1 = postFeedbackVoteService.create(postFeedbackVote1, viewer);


        assertTrue(postFeedbackVoteService.get(postFeedbackVote.getId()) == null);
        assertTrue(postFeedbackVoteService.get(postFeedbackVote1.getId()).equals(postFeedbackVote1));
    }


    @Test
    public void test_listOfUser_afterPostDeleted() {

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

        PostFeedbackVote postFeedbackVote = PostServiceTest.createVote(post.getPostFeedbackPossibilities().get(0), viewer);

        //creating first feedback - OK
        postFeedbackVote = postFeedbackVoteService.create(postFeedbackVote, viewer);

        //deleting post, where postFeedback was created
        postService.delete(post.getId(), owner);


        Page<PostFeedbackVote> postFeedbackPage = postFeedbackVoteService.findByUserPaging(viewer.getId(), PageRequest.of(0, 20), viewer, -1l);

        //postFeedback should be deleted
        assertTrue(postFeedbackPage != null);
        assertTrue(postFeedbackPage.getContent().size() == 0);

    }


    @Test
    public void test_listOfUser_shouldBeOrderedByTimestamp() {

        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        Post post = PostServiceTest.createPost(owner, null);
        post.setAccessType(Post.AccessType.PUBLICC);
        post = postService.create(post, owner);

        Post post2 = PostServiceTest.createPost(owner, null);
        post2.setAccessType(Post.AccessType.PUBLICC);
        post2 = postService.create(post2, owner);


        PostFeedbackVote postFeedbackVote = PostServiceTest.createVote(post.getPostFeedbackPossibilities().get(0), viewer);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        PostFeedbackVote postFeedbackVote1 = PostServiceTest.createVote(post2.getPostFeedbackPossibilities().get(0), viewer);

        //creating first feedback - OK
        postFeedbackVote = postFeedbackVoteService.create(postFeedbackVote, viewer);

        ////creating second feedback - OK
        postFeedbackVote1 = postFeedbackVoteService.create(postFeedbackVote1, viewer);

        Page<PostFeedbackVote> postFeedbackPage = postFeedbackVoteService.findByUserPaging(viewer.getId(), PageRequest.of(0, 20), viewer, null);

        assertTrue(postFeedbackPage != null);
        assertTrue(postFeedbackPage.getContent() != null);
        assertTrue(postFeedbackPage.getContent().size() == 2);

        //first should be the newest postFeedback
        assertTrue(postFeedbackPage.getContent().get(0).equals(postFeedbackVote1));
        assertTrue(postFeedbackPage.getContent().get(1).equals(postFeedbackVote));
    }



}
