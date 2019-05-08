package com.filip.versu.service;


import com.filip.versu.VersuApplication;
import com.filip.versu.entity.model.GoogleLocation;
import com.filip.versu.service.abs.GoogleLocationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LocationServiceTest {


    @Autowired
    private GoogleLocationService googleLocationService;


    @Test
    public void test_createLocationByGoogleId_shouldOK() {

        GoogleLocation location = new GoogleLocation();
        location.setGoogleID("ChIJhT-9kj-JbEcRFjPcvf5V47s");
        location.setName("Mountain View");
        location.setLatitude(11);
        location.setLongitude(11);

        location = googleLocationService.create(location);

        assertTrue(location.getId() != null);

        GoogleLocation getLocation = googleLocationService.get(location.getId());

        assertTrue(getLocation.equals(location));
    }

    @Test
    public void test_createLocationByLocality_shouldOK() {
        GoogleLocation userLocation = new GoogleLocation();

        userLocation.setLatitude(48.174088);
        userLocation.setLongitude(17.094314);

        userLocation = googleLocationService.create(userLocation);

        assertTrue(userLocation.getGoogleID() != null);
        assertTrue(userLocation.getGoogleID().equals("ChIJl2HKCjaJbEcRaEOI_YKbH2M"));

        assertTrue(userLocation.getName() != null);
        assertTrue(userLocation.getName().equals("Bratislava, Slovakia"));

        assertTrue(userLocation.getLatitude() == 48.1485965);
        assertTrue(userLocation.getLongitude() == 17.1077477);
    }

    @Test
    public void test_createLocationByAdminArea_shouldOK() {
        GoogleLocation userLocation = new GoogleLocation();

        userLocation.setLatitude(48.472304);
        userLocation.setLongitude(15.219847);

        userLocation = googleLocationService.create(userLocation);

        assertTrue(userLocation.getGoogleID() != null);
        assertTrue(userLocation.getGoogleID().equals("ChIJ8S5gEt2BbUcR3C5ibAmuoVU"));

        assertTrue(userLocation.getName() != null);
        assertTrue(userLocation.getName().equals("Lower Austria, Austria"));

        assertTrue(userLocation.getLatitude() == 48.10807699999999);
        assertTrue(userLocation.getLongitude() == 15.8049558);
    }

    @Test
    public void test_createLocationUnknown_shouldOK() {
        GoogleLocation userLocation = new GoogleLocation();

        userLocation.setLatitude(47.395736);
        userLocation.setLongitude(-36.029315);

        userLocation = googleLocationService.create(userLocation);

        assertTrue(userLocation.getGoogleID() == null);

        assertTrue(userLocation.getName() != null);
        assertTrue(userLocation.getName().equals(GoogleLocation.NAME_UKNOWN));

        assertTrue(userLocation.getLatitude() == GoogleLocation.LAT_UNKNOWN);
        assertTrue(userLocation.getLatitude() == GoogleLocation.LON_UNKNOWN);
    }

    @Test
    public void test_createLocation_shouldMapToExisting() {
        GoogleLocation location = new GoogleLocation();
        location.setGoogleID("ChIJhT-9kj-JbEcRFjPcvf5V47s");
        location.setName("Mountain View");
        location.setLatitude(11);
        location.setLongitude(11);

        location = googleLocationService.create(location);


        GoogleLocation location1 = new GoogleLocation();
        location1.setGoogleID("ChIJhT-9kj-JbEcRFjPcvf5V47s");
        location1.setName("Mountain View");
        location1.setLatitude(11);
        location1.setLongitude(11);

        location1 = googleLocationService.create(location1);

        /**
         * location1 should be mapped to location
         */
        assertTrue(location1.equals(location));

    }

}
