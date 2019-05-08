package com.filip.versu.service;

import com.filip.versu.VersuApplication;
import com.filip.versu.entity.model.Following;
import com.filip.versu.entity.model.User;
import com.filip.versu.exception.EntityExistsException;
import com.filip.versu.exception.EntityNotExistsException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FollowingServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private FollowingService followingService;


    @Test
    public void createFollowing_shouldOK() {

        User userCreator = UserServiceTest.createUser("creator");
        User userTarget = UserServiceTest.createUser("target");

        userCreator = userService.create(userCreator, userCreator);
        userTarget = userService.create(userTarget, userTarget);

        Following following = new Following();
        following.setCreator(userCreator);
        following.setTarget(userTarget);

        following = followingService.create(following, userCreator);

        assertTrue(following.getId() != null);
        assertTrue(following.getCreator().equals(userCreator));
        assertTrue(following.getTarget().equals(userTarget));

    }

    /**
     * The following's target does not exists.
     */
    @Test(expected = EntityNotExistsException.class)
    public void createFollowing_shouldThrowEntityNotExistsException() {

        User userCreator = UserServiceTest.createUser("creator");
        User userTarget = UserServiceTest.createUser("target");

        userCreator = userService.create(userCreator, userCreator);

        /**
         * The user target does not exists
         */
        Following following = new Following();
        following.setCreator(userCreator);
        following.setTarget(userTarget);

        /**
         * The exception should be thrown here.
         */
        following = followingService.create(following, userCreator);

    }

    /**
     * Creating duplicated following
     */
    @Test(expected = EntityExistsException.class)
    public void createFollowing_shouldThrowEntityExistsException() {

        User userCreator = UserServiceTest.createUser("creator");
        User userTarget = UserServiceTest.createUser("target");

        userCreator = userService.create(userCreator, userCreator);
        userTarget = userService.create(userTarget, userTarget);

        Following following = new Following();
        following.setCreator(userCreator);
        following.setTarget(userTarget);

        following = followingService.create(following, userCreator);

        Following followingDuplicate = new Following();
        followingDuplicate.setTarget(userTarget);
        followingDuplicate.setCreator(userCreator);

        /**
         * The exception should be thrown here
         */
        followingDuplicate = followingService.create(followingDuplicate, userCreator);

    }


    @Test
    public void testListFollowingsOfUser_shouldOK() {

        User userCreator = UserServiceTest.createUser("creator");
        User userTarget = UserServiceTest.createUser("target");
        User userTarget2 = UserServiceTest.createUser("target2");
        User userTarget3 = UserServiceTest.createUser("target3");

        userCreator = userService.create(userCreator, userCreator);
        userTarget = userService.create(userTarget, userTarget);
        userTarget2 = userService.create(userTarget2, userTarget2);
        userTarget3 = userService.create(userTarget3, userTarget3);

        Following following = new Following();
        following.setCreator(userCreator);
        following.setTarget(userTarget);

        Following following2 = new Following();
        following2.setCreator(userCreator);
        following2.setTarget(userTarget2);

        Following following3 = new Following();
        following3.setCreator(userCreator);
        following3.setTarget(userTarget3);

        followingService.create(following, userCreator);
        followingService.create(following2, userCreator);
        followingService.create(following3, userCreator);

        Page<Following> followings = followingService.listFollowedByOfUser(userCreator.getId(), null, new PageRequest(0, 20), userCreator);

        assertTrue(followings.getContent().contains(following));
        assertTrue(followings.getContent().contains(following2));
        assertTrue(followings.getContent().contains(following3));

    }


    @Test
    public void testListFollowingsOfUserPaging_shouldOK() {

        User userCreator = UserServiceTest.createUser("creator");

        userCreator = userService.create(userCreator, userCreator);

        List<Following> followings = new ArrayList<>();

        int size = 50;

        for (int i = 0; i < size; i++) {
            User userTarget = UserServiceTest.createUser("target_"+i);
            userTarget = userService.create(userTarget, userTarget);

            Following following = new Following();
            following.setCreator(userCreator);
            following.setTarget(userTarget);

            following = followingService.create(following, userCreator);

            followings.add(following);
        }

        int index = 0;

        Page<Following> followingsPage = followingService.listFollowedByOfUser(userCreator.getId(), null, new PageRequest(0, 20), userCreator);
        do {
            for(Following fol: followingsPage.getContent()) {

                Following retrievedFollowing = followings.get(size - (++index));

                assertTrue(fol.equals(retrievedFollowing));
            }

            Long lastId = null;
            if(followingsPage != null) {
                lastId = followingsPage.getContent().get(followingsPage.getContent().size() - 1).getId();
            }

            followingsPage = followingService.listFollowedByOfUser(userCreator.getId(), lastId, new PageRequest(0, 20), userCreator);

        } while ((followingsPage.getContent().size() != 0));


        assertTrue(index == size);



    }

    @Test
    public void testRemoveOfUser_shouldOK() {

        User userCreator = UserServiceTest.createUser("creator");
        User userTarget = UserServiceTest.createUser("target");

        userCreator = userService.create(userCreator, userCreator);
        userTarget = userService.create(userTarget, userTarget);

        Following following = new Following();
        following.setCreator(userCreator);
        following.setTarget(userTarget);

        following = followingService.create(following, userCreator);

        /**
         * The following was removed
         */
        followingService.removeOfUser(following.getCreator(), userCreator);

        /**
         * The @following should not be found
         */
        assertTrue(followingService.get(following.getId()) == null);

    }


    @Test
    public void test_deleteFollowing_shouldOK() {

        User userCreator = UserServiceTest.createUser("creator");
        User userTarget = UserServiceTest.createUser("target");

        userCreator = userService.create(userCreator, userCreator);
        userTarget = userService.create(userTarget, userTarget);

        Following following = new Following();
        following.setCreator(userCreator);
        following.setTarget(userTarget);

        following = followingService.create(following, userCreator);

        followingService.delete(following.getId(), userTarget);

        Following existingFollowing = followingService.get(following.getId());

        /**
         * Showing that the Following was deleted
         */
        assertTrue(existingFollowing == null);

        User getCreator = userService.get(userCreator.getId());
        User getTarget = userService.get(userTarget.getId());

        /**
         * Showing that both users are still alive.
         */
        assertTrue(getCreator.equals(userCreator));
        assertTrue(getTarget.equals(userTarget));

    }

}
