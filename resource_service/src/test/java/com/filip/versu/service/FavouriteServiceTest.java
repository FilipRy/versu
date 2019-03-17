package com.filip.versu.service;

import com.filip.versu.VersuApplication;
import com.filip.versu.entity.model.Favourite;
import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.User;
import com.filip.versu.exception.EntityNotExistsException;
import com.filip.versu.exception.FeedbackActionExistsException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {VersuApplication.class})
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FavouriteServiceTest {


    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private FavouriteService favouriteService;

    @Test
    public void test_createFavourite_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);
        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);

        Post post = PostServiceTest.createShoppingItem(owner, viewers);
        post = postService.create(post, owner);

        Favourite favourite = new Favourite();
        favourite.setPost(post);
        favourite.setOwner(viewer);

        favourite = favouriteService.create(favourite, viewer);

        Favourite getFavourite = favouriteService.get(favourite.getId());
        assertTrue(favourite.equals(getFavourite));

    }

    @Test(expected = FeedbackActionExistsException.class)
    public void test_createFavourite_shouldThrowFeedbackActionExistsException() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);
        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);

        Post post = PostServiceTest.createShoppingItem(owner, viewers);
        post = postService.create(post, owner);

        Favourite favourite = new Favourite();
        favourite.setPost(post);
        favourite.setOwner(viewer);

        favourite = favouriteService.create(favourite, viewer);

        Favourite favouriteDuplicate = new Favourite();
        favouriteDuplicate.setPost(post);
        favouriteDuplicate.setOwner(viewer);

        /**
         * The exception should be thrown here.
         */
        favouriteDuplicate = favouriteService.create(favouriteDuplicate, viewer);

    }


    @Test(expected = EntityNotExistsException.class)
    public void test_listFavouriteOnDeletedShoppingItem_shouldThrowEntityNotExistsException() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);
        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);

        Post post = PostServiceTest.createShoppingItem(owner, viewers);
        post = postService.create(post, owner);

        Favourite favourite = new Favourite();
        favourite.setPost(post);
        favourite.setOwner(viewer);

        favourite = favouriteService.create(favourite, viewer);

        /**
         * deleting shopping item
         */
        postService.delete(post.getId(), owner);

        //the exception should be thrown here because the post was deleted
        Page<Favourite> favouritePage = favouriteService.listByPostReversePaging(post.getId(), null, new PageRequest(0, 10), viewer);
    }

    @Test
    public void test_listFavouriteOfUser_checkDeletedItems_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);
        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);

        Post post = PostServiceTest.createShoppingItem(owner, viewers);
        post = postService.create(post, owner);

        Favourite favourite = new Favourite();
        favourite.setPost(post);
        favourite.setOwner(viewer);

        favourite = favouriteService.create(favourite, viewer);

        /**
         * deleting shopping item
         */
        postService.delete(post.getId(), owner);

        Page<Favourite> favouritePage = favouriteService.listOfUser(viewer.getId(), null, new PageRequest(0, 20), viewer);

        /**
         * the viewer should see his favourite on soft-deleted shopping item as: content unavailable
         */
        Favourite favouriteUnavailable = favouritePage.getContent().get(0);
        assertTrue(favouriteUnavailable.getPost() == null);
        assertTrue(favouriteUnavailable.getOwner().equals(viewer));
        assertTrue(favouriteUnavailable.getId().equals(favourite.getId()));


    }

}
