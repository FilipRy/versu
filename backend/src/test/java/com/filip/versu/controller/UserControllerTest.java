package com.filip.versu.controller;

import com.filip.versu.VersuApplication;
import com.filip.versu.entity.dto.UserDTO;
import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.User;
import com.filip.versu.exception.AuthenticationException;
import com.filip.versu.service.PostService;
import com.filip.versu.service.PostServiceTest;
import com.filip.versu.service.UserService;
import com.filip.versu.service.UserServiceTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertTrue;

//@RunWith(SpringJUnit4ClassRunner.class)
//@WebAppConfiguration
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {
//
//    public static final String[] testUsersAccessTokens = {
//            "EAADLekHbTggBAK4bFfk0vXFXe071EozRiNP37XTRoYWDaBbz5zGijpudL8ADZBr7ZAJAaWxCbpgmnlZA2s3WLaffklr3wipzFzxzUngztAk8Y8EDdIFyHGbxIN9ZBSZCSGtTTDgt1mgCUHBCyATqyxzA7V0yVHCXtgXZColJJKqjbFPOLU59yW"};
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
//
//    @Test
//    public void test_create_createUserFromFacebook_shouldCreateUser() {
//        UserDTO userDTO = new UserDTO();
//        userDTO.email = "email@email.com";
//        userDTO.username = "unique username";
//
//        userDTO = userController.create(userDTO, testUsersAccessTokens[0]);
//
//        User user = userService.get(userDTO.getId());
//
//        assertUsersEquals(user, userDTO);
//
//        assertTrue(user.getExternalAccounts().size() == 1);
//
//        ExternalAccount externalAccount = user.getExternalAccounts().get(0);
//
//        assertTrue(externalAccount.getAppUser().equals(user));
//        assertTrue(externalAccount.getProvider().equals(ExternalAccount.ExternalAccountProvider.FACEBOOK));
//
//    }
//
//    @Test
//    public void test_create_createUserFromFacebook_shouldReturnExistingUser() {
//
//        UserDTO userDTO = new UserDTO();
//        userDTO.email = "email@email.com";
//        userDTO.username = "unique username";
//
//        userDTO = userController.create(userDTO, testUsersAccessTokens[0]);
//
//        UserDTO userDTO1 = new UserDTO();
//        userDTO1.email = "email@email2.com";
//        userDTO1.username = "unique username2";
//
//        userDTO1 = userController.create(userDTO1, testUsersAccessTokens[0]);//using the access token of already registered user here -> registered user should be returned
//
//        assertTrue(userDTO1.equals(userDTO));
//    }
//
//    @Test
//    public void test_create_createAnonymUser_shouldCreateUSer() {
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
//        User user = userService.get(userDTO.getId());
//
//        assertTrue(user != null);
//        assertTrue(user.getId().equals(userDTO.getId()));
//
//        assertTrue(user.getExternalAccounts().size() == 1);
//
//        ExternalAccount externalAccount = user.getExternalAccounts().get(0);
//
//        assertTrue(externalAccount.getAppUser().equals(user));
//        assertTrue(externalAccount.getProvider().equals(ExternalAccount.ExternalAccountProvider.ANONYM_NAME));
//        assertTrue(externalAccount.getExternalUserId().equals(post.getSecretUrl() + "_" + user.getUsername()));
//
//    }
//
//    @Test
//    public void test_exchangeToken_FacebookToken_shouldReturnNonRegisteredUser() {
//
//        UserDTO userDTO = new UserDTO();
//        userDTO.email = "email@email.com";
//        userDTO.username = "unique username";
//
//        userDTO = userController.exchangeToken(testUsersAccessTokens[0]);
//
//        assertTrue(userDTO != null);
//        assertTrue(userDTO.getId() == null);//unknown user is returned
//
//    }
//
//    @Test
//    public void test_exchangeToken_FacebookToken_shouldReturnRegisteredUser() {
//
//        UserDTO userDTO = new UserDTO();
//        userDTO.email = "email@email.com";
//        userDTO.username = "unique username";
//
//        userDTO = userController.create(userDTO, testUsersAccessTokens[0]);
//
//        UserDTO registeredUser = userController.exchangeToken(testUsersAccessTokens[0]);
//
//        assertTrue(registeredUser.equals(userDTO));
//
//    }
//
//    @Test
//    public void test_exchangeToken_anonymToken_shouldReturnRegisteredUser() {
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
//        UserDTO registeredUser = userController.exchangeToken(userDTO.accessToken);
//
//        assertTrue(registeredUser.equals(userDTO));
//
//    }
//
//    @Test(expected = AuthenticationException.class)
//    public void test_exchangeToke_shouldThrowAuthenticationException() {
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
//        User user = userService.get(userDTO.getId());
//
//        userService.delete(user.getId(), user);//removing user
//
//        userController.exchangeToken(userDTO.accessToken);//AuthException should be thrown here
//
//
//    }
//
//
//    private void assertUsersEquals(User user, UserDTO userDTO) {
//        assertTrue(user != null);
//        assertTrue(user.getId().equals(userDTO.getId()));
//        assertTrue(user.getUsername().equals(userDTO.username));
//        assertTrue(user.getEmail().equals(userDTO.email));
//    }

}

