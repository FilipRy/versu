package com.filip.versu.controller;


import com.filip.versu.VersuApplication;
import com.filip.versu.entity.dto.PostDTO;
import com.filip.versu.entity.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.entity.dto.PostFeedbackVoteDTO;
import com.filip.versu.entity.dto.UserDTO;
import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.PostFeedbackVote;
import com.filip.versu.entity.model.User;
import com.filip.versu.exception.AuthenticationException;
import com.filip.versu.exception.UnauthorizedException;
import com.filip.versu.service.*;
import com.filip.versu.service.PostFeedbackVoteService;
import com.filip.versu.service.PostService;
import com.filip.versu.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertTrue;

//@RunWith(SpringJUnit4ClassRunner.class)
//@WebAppConfiguration
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PostFeedbackVoteControllerTest {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private PostService postService;
//
//    @Autowired
//    private UserController userController;
//
//    @Autowired
//    private PostController postController;
//
//    @Autowired
//    private PostFeedbackVoteController postFeedbackVoteController;
//
//    @Autowired
//    private PostFeedbackVoteService postFeedbackVoteService;
//
//
//    @Test
//    @Transactional
//    public void test_createAnonym_shouldCreatePostFeedback() {
//
//        User owner = UserServiceTest.createUser("owner");
//        owner = userService.create(owner, owner);
//
//        Post post = PostServiceTest.createPost(owner, null);
//        post.setSecretUrl("generate me");
//
//        post = postService.create(post, owner);
//
//        UserDTO userDTO = new UserDTO();
//        userDTO.email = "email@email.com";
//        userDTO.username = "unique username";
//
//        userDTO = userController.createAnonym(userDTO, post.getSecretUrl());
//
//        PostDTO postDTO = postController.getDetailsBySecretUrlAuth(post.getSecretUrl(), userDTO.accessToken);
//
//        PostFeedbackVoteDTO postFeedbackVoteDTO = createPostFeedbackVoteDTO(userDTO, postDTO.postFeedbackPossibilities.get(0));
//
//        postFeedbackVoteDTO = postFeedbackVoteController.createAnonym(postFeedbackVoteDTO, userDTO.accessToken);
//
//        assertTrue(postFeedbackVoteDTO != null);
//        assertTrue(postFeedbackVoteDTO.getId() != null);
//
//        PostFeedbackVote postFeedbackVote = postFeedbackVoteService.get(postFeedbackVoteDTO.getId());
//
//        assertTrue(postFeedbackVote.getId().equals(postFeedbackVoteDTO.getId()));
//        assertTrue(postFeedbackVote.getPostFeedbackPossibility().getPost().getId().equals(postFeedbackVoteDTO.feedbackPossibilityDTO.postDTO.getId()));
//        assertTrue(postFeedbackVote.getOwner().getId().equals(postFeedbackVoteDTO.owner.getId()));
//        assertTrue(postFeedbackVote.getPostFeedbackPossibility().getName().equals(postFeedbackVoteDTO.feedbackPossibilityDTO.name));
//
//    }
//
//    @Test(expected = UnauthorizedException.class)
//    @Transactional
//    public void test_createAnonym_hasIncorrectUrl_shouldThrowUnauthenticatedException() {
//
//        User owner = UserServiceTest.createUser("owner");
//        owner = userService.create(owner, owner);
//
//        Post post = PostServiceTest.createPost(owner, null);
//        post.setSecretUrl("generate me");
//
//        Post post1 = PostServiceTest.createPost(owner, null);
//        post1.setSecretUrl("generate me");
//
//        post = postService.create(post, owner);
//        post1 = postService.create(post1, owner);
//
//        UserDTO userDTO = new UserDTO();
//        userDTO.email = "email@email.com";
//        userDTO.username = "unique username";
//
//        userDTO = userController.createAnonym(userDTO, post.getSecretUrl());
//
//        PostDTO postDTO = postController.getDetailsBySecretUrlAuth(post1.getSecretUrl(), userDTO.accessToken);//retrieving post1
//
//        PostFeedbackVoteDTO postFeedbackVoteDTO = createPostFeedbackVoteDTO(userDTO, postDTO.postFeedbackPossibilities.get(0));
//
//        postDTO.setId(post.getId());//attacker could rewrite postDTO id and create postFeedback with valid secret url
//
//
//        postFeedbackVoteController.createAnonym(postFeedbackVoteDTO, userDTO.accessToken);//AuthException should be thrown here.
//
//    }
//
//    @Test
//    @Transactional
//    public void test_delete_shouldOK() {
//
//        User owner = UserServiceTest.createUser("owner");
//        owner = userService.create(owner, owner);
//
//        Post post = PostServiceTest.createPost(owner, null);
//        post.setSecretUrl("generate me");
//
//        post = postService.create(post, owner);
//
//        UserDTO userDTO = new UserDTO();
//        userDTO.email = "email@email.com";
//        userDTO.username = "unique username";
//        userDTO = userController.createAnonym(userDTO, post.getSecretUrl());
//
//        PostDTO postDTO = postController.getDetailsBySecretUrlAuth(post.getSecretUrl(), userDTO.accessToken);//retrieving post
//
//        PostFeedbackVoteDTO postFeedbackVoteDTO = createPostFeedbackVoteDTO(userDTO, postDTO.postFeedbackPossibilities.get(0));
//
//        postFeedbackVoteDTO = postFeedbackVoteController.createAnonym(postFeedbackVoteDTO, userDTO.accessToken);//creating postFeedbackVoteDTO
//
//        postFeedbackVoteController.delete(postFeedbackVoteDTO.getId(), userDTO.accessToken);
//
//        PostFeedbackVote postFeedbackVote = postFeedbackVoteService.get(postFeedbackVoteDTO.getId());
//
//        assertTrue(postFeedbackVote == null);//checking if postFeedback was deleted
//    }
//
//    @Test(expected = AuthenticationException.class)
//    @Transactional
//    public void test_delete_anonymTriesToDeleteActionOfAnotherUser() {
//
//        User owner = UserServiceTest.createUser("owner");
//        owner = userService.create(owner, owner);
//
//        Post post = PostServiceTest.createPost(owner, null);
//        post.setSecretUrl("generate me");
//
//        post = postService.create(post, owner);
//
//        UserDTO userDTO = new UserDTO();
//        userDTO.email = "email@email.com";
//        userDTO.username = "unique username";
//        userDTO = userController.createAnonym(userDTO, post.getSecretUrl());
//
//        UserDTO userWithFacebook = new UserDTO();
//        userWithFacebook.email = "email2@email.com";
//        userWithFacebook.username = "usr222";
//        userWithFacebook = userController.create(userWithFacebook, UserControllerTest.testUsersAccessTokens[0]);
//
//        PostDTO postDTO = postController.getDetailsBySecretUrlAuth(post.getSecretUrl(), UserControllerTest.testUsersAccessTokens[0]);//retrieving post
//
//        PostFeedbackVoteDTO postFeedbackVoteDTO = createPostFeedbackVoteDTO(userWithFacebook, postDTO.postFeedbackPossibilities.get(0));
//
//        // user registered with facebook is creating postFeedbackVoteDTO
//        postFeedbackVoteDTO = postFeedbackVoteController.createAnonym(postFeedbackVoteDTO, UserControllerTest.testUsersAccessTokens[0]);
//
//        //attacker stole id of userWithFacebook, and wants to delete his feedback action -> AuthException should be thrown
//        postFeedbackVoteController.delete(postFeedbackVoteDTO.getId(), userWithFacebook.getId().toString());
//
//    }
//
//    public static PostFeedbackVoteDTO createPostFeedbackVoteDTO(UserDTO userDTO, PostFeedbackPossibilityDTO postFeedbackPossibilityDTO) {
//        PostFeedbackVoteDTO postFeedbackVoteDTO = new PostFeedbackVoteDTO();
//        postFeedbackVoteDTO.feedbackPossibilityDTO = postFeedbackPossibilityDTO;
//        postFeedbackVoteDTO.owner = userDTO;
//        return postFeedbackVoteDTO;
//    }

}
