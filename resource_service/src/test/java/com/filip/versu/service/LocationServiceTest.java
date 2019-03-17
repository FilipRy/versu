package com.filip.versu.service;


import com.filip.versu.VersuApplication;
import com.filip.versu.entity.model.PostLocation;
import com.filip.versu.entity.model.UserLocation;
import com.filip.versu.entity.model.abs.AbsLocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {VersuApplication.class})
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LocationServiceTest {


    @Autowired
    private PostLocationService postLocationService;

    @Autowired
    private UserLocationService userLocationService;

    @Test
    public void test_createLocationByGoogleId_shouldOK() {
        PostLocation location = new PostLocation();
        location.setGoogleID("ChIJhT-9kj-JbEcRFjPcvf5V47s");
        location.setName("Mountain View");
        location.setLatitude(11);
        location.setLongitude(11);

        location = postLocationService.create(location);

        assertTrue(location.getId() != null);

        PostLocation getLocation = postLocationService.get(location.getId());

        assertTrue(getLocation.equals(location));
    }

    @Test
    public void test_createLocationByLocality_shouldOK() {
        UserLocation userLocation = new UserLocation();

        userLocation.setLatitude(48.174088);
        userLocation.setLongitude(17.094314);

        userLocation = userLocationService.create(userLocation);

        assertTrue(userLocation.getGoogleID() != null);
        assertTrue(userLocation.getGoogleID().equals("ChIJl2HKCjaJbEcRaEOI_YKbH2M"));

        assertTrue(userLocation.getName() != null);
        assertTrue(userLocation.getName().equals("Bratislava, Slovakia"));

        assertTrue(userLocation.getLatitude() == 48.1485965);
        assertTrue(userLocation.getLongitude() == 17.1077477);
    }

    @Test
    public void test_createLocationByAdminArea_shouldOK() {
        UserLocation userLocation = new UserLocation();

        userLocation.setLatitude(48.472304);
        userLocation.setLongitude(15.219847);

        userLocation = userLocationService.create(userLocation);

        assertTrue(userLocation.getGoogleID() != null);
        assertTrue(userLocation.getGoogleID().equals("ChIJ8S5gEt2BbUcR3C5ibAmuoVU"));

        assertTrue(userLocation.getName() != null);
        assertTrue(userLocation.getName().equals("Lower Austria, Austria"));

        assertTrue(userLocation.getLatitude() == 48.10807699999999);
        assertTrue(userLocation.getLongitude() == 15.8049558);
    }

    @Test
    public void test_createLocationUnknown_shouldOK() {
        UserLocation userLocation = new UserLocation();

        userLocation.setLatitude(47.395736);
        userLocation.setLongitude(-36.029315);

        userLocation = userLocationService.create(userLocation);

        assertTrue(userLocation.getGoogleID() == null);

        assertTrue(userLocation.getName() != null);
        assertTrue(userLocation.getName().equals(AbsLocation.NAME_UKNOWN));

        assertTrue(userLocation.getLatitude() == AbsLocation.LAT_UNKNOWN);
        assertTrue(userLocation.getLatitude() == AbsLocation.LON_UNKNOWN);
    }

    @Test
    public void test_createLocation_shouldMapToExisting() {
        PostLocation location = new PostLocation();
        location.setGoogleID("ChIJhT-9kj-JbEcRFjPcvf5V47s");
        location.setName("Mountain View");
        location.setLatitude(11);
        location.setLongitude(11);

        location = postLocationService.create(location);


        PostLocation location1 = new PostLocation();
        location1.setGoogleID("ChIJhT-9kj-JbEcRFjPcvf5V47s");
        location1.setName("Mountain View");
        location1.setLatitude(11);
        location1.setLongitude(11);

        location1 = postLocationService.create(location1);

        /**
         * location1 should be mapped to location
         */
        assertTrue(location1.equals(location));

    }

}
