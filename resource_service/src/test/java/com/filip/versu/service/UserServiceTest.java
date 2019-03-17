package com.filip.versu.service;


import com.filip.versu.VersuApplication;
import com.filip.versu.entity.model.ExternalAccount;
import com.filip.versu.entity.model.User;
import com.filip.versu.entity.model.UserLocation;
import com.filip.versu.exception.EntityExistsException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {VersuApplication.class})
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void createUser_shouldOK() {

        User userFilip = createUser("popel-princko");
        userFilip = userService.create(userFilip, userFilip);

        assertTrue(userFilip.getId() != null);
    }


    @Test
    public void createUser_shouldReturnAlreadyExistingUser() {

        User userFilip = createUser("popel-princko");
        userFilip = userService.create(userFilip, userFilip);

        User userFilipCopy = new User();

        ExternalAccount externalAccount = new ExternalAccount();
        externalAccount.setAppUser(userFilipCopy);
        externalAccount.setExternalUserId(userFilip.getExternalAccounts().get(0).getExternalUserId());
        externalAccount.setProvider(userFilip.getExternalAccounts().get(0).getProvider());

        userFilipCopy.getExternalAccounts().add(externalAccount);

        userFilipCopy = userService.create(userFilipCopy, userFilipCopy);

        //using the same external account provider second time -> 1st user should be returned
        assertTrue(userFilipCopy.equals(userFilip));


    }

    @Test(expected = EntityExistsException.class)
    public void createUser_shouldThrowEntityExistsException() {

        User userFilip = createUser("popel-princko");
        userFilip = userService.create(userFilip, userFilip);

        assertTrue(userFilip.getId() != null);

        User existingUser = createUser("popel-princko");
        existingUser.setId(userFilip.getId());

        /**
         * The EntityExistsException should be thrown here
         */
        existingUser = userService.create(existingUser, existingUser);

    }


    @Test
    public void test_findByNameLike() {
        User userFilip = createUser("this is my name");
        userFilip = userService.create(userFilip, userFilip);

        String query = "my name";

        Page<User> results = userService.findByNameLike(query, new PageRequest(0, 20));

        assertTrue(results.getContent().contains(userFilip));

        query = "this";
        results = userService.findByNameLike(query, new PageRequest(0, 20));

        assertTrue(results.getContent().contains(userFilip));
    }

    @Test
    public void test_updateUser_shouldNotUpdateUserLocation() {

        User userFilip = createUser("smthing5");
        userFilip = userService.create(userFilip, userFilip);

        assertTrue(userFilip.getLocation() == null);

        UserLocation userLocation = new UserLocation();
        userLocation.setLatitude(48.160514);
        userLocation.setLongitude(17.103830);
        userFilip.setLocation(userLocation);

        userFilip = userService.update(userFilip, userFilip);

        //this is the first location of userFilip
        userLocation = userFilip.getLocation();

        UserLocation userLocation1 = new UserLocation();
        userLocation1.setLatitude(48.159219);
        userLocation1.setLongitude(17.100965);
        userFilip.setLocation(userLocation1);

        //the location should stay the same, because user doesn't move too much.
        userFilip = userService.update(userFilip, userFilip);

        assertTrue(userFilip.getLocation().equals(userLocation));

    }


    @Test
    public void test_updateUser_shouldUpdateUserLocation() {
        User userFilip = createUser("smthing5");
        userFilip = userService.create(userFilip, userFilip);

        assertTrue(userFilip.getLocation() == null);

        UserLocation userLocation = new UserLocation();
        userLocation.setLatitude(48.160514);
        userLocation.setLongitude(17.103830);

        userFilip.setLocation(userLocation);

        userFilip = userService.update(userFilip, userFilip);

        //this is the first location of userFilip
        userLocation = userFilip.getLocation();

        UserLocation userLocation1 = new UserLocation();
        userLocation1.setLatitude(48.142839);
        userLocation1.setLongitude(16.958847);
        userFilip.setLocation(userLocation1);

        //the location should be updated, because user moved.
        userFilip = userService.update(userFilip, userFilip);

        assertFalse(userFilip.getLocation().equals(userLocation));
    }

    @Test
    public void test_deleteUser() {

        User userFilip = createUser("popel-princko");
        userFilip = userService.create(userFilip, userFilip);

        userService.delete(userFilip.getId(), userFilip);


        User getUser = userService.get(userFilip.getId());
        assertTrue(getUser == null);

    }

    public static User createUser(String username) {
        User userFilip = new User();
        userFilip.setEmail(username+"@mail.com");

        ExternalAccount externalAccount = new ExternalAccount();
        externalAccount.setProvider(ExternalAccount.ExternalAccountProvider.FACEBOOK);
        externalAccount.setExternalUserId(Long.toString(System.currentTimeMillis() + System.nanoTime()));
        externalAccount.setAppUser(userFilip);

        userFilip.getExternalAccounts().add(externalAccount);

        userFilip.setUsername(username);
        userFilip.setPassword("princko");

        return userFilip;
    }

}
