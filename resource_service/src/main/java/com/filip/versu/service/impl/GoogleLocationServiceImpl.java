package com.filip.versu.service.impl;

import com.filip.versu.entity.model.GoogleLocation;
import com.filip.versu.repository.GoogleLocationRepository;
import com.filip.versu.service.GoogleMapsService;
import com.filip.versu.service.abs.GoogleLocationService;
import com.filip.versu.service.impl.abs.AbsCrudServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class GoogleLocationServiceImpl extends AbsCrudServiceImpl<GoogleLocation, Long, GoogleLocationRepository> implements GoogleLocationService {

    @Autowired
    private GoogleMapsService googleMapsService;

    @Override
    public GoogleLocation create(GoogleLocation entity) {
        GoogleLocation existing = mapToExistingLocation(entity);
        if(existing != null) {
            return existing;
        }

        if(entity.getGoogleID() != null) {
            entity = googleMapsService.findByGoogleID(entity);
        } else {//this is the case when user havn't/dont't want specified his/her location exactly (the case when location is displayed in his/her profile), so only city and state will be persisted
            entity = googleMapsService.findLocalityByLatLng(entity);
        }

        //params might have changed -> try to map again
        existing = mapToExistingLocation(entity);
        if(existing != null) {
            return existing;
        }

        return super.create(entity);
    }



    @Override
    public GoogleLocation transferUpdateFields(GoogleLocation getEntity, GoogleLocation updatedEntity) {
        getEntity.setLongitude(updatedEntity.getLongitude());
        getEntity.setLatitude(updatedEntity.getLatitude());
        getEntity.setName(updatedEntity.getName());
        getEntity.setGoogleID(updatedEntity.getGoogleID());
        return getEntity;
    }

    @Override
    public GoogleLocation mapToExistingLocation(GoogleLocation location) {
        return repository.findOneByLongitudeAndLatitudeAndGoogleID(location.getLongitude(), location.getLatitude(), location.getGoogleID());
    }

    /**
     * Compute distance in meter.
     * @param pointA
     * @param pointB
     * @return
     */
    public static double computeDistance(GoogleLocation pointA, GoogleLocation pointB) {
        float lat1 = (float) pointA.getLatitude();
        float lng1 = (float) pointA.getLongitude();

        float lat2 = (float) pointB.getLatitude();
        float lng2 = (float) pointB.getLongitude();

        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

}
