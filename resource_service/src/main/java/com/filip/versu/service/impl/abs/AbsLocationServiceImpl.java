package com.filip.versu.service.impl.abs;

import com.filip.versu.entity.model.abs.AbsLocation;
import com.filip.versu.repository.abs.AbsLocationRepository;
import com.filip.versu.service.GoogleMapsService;
import com.filip.versu.service.abs.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public abstract class AbsLocationServiceImpl<K extends AbsLocation, R extends AbsLocationRepository<K>> extends AbsCrudServiceImpl<K, Long, R> implements LocationService<K> {

    @Autowired
    private GoogleMapsService googleMapsService;

    @Override
    public K create(K entity) {
        K existing = mapToExistingLocation(entity);
        if(existing != null) {
            return existing;
        }

        if(entity.getGoogleID() != null) {
            entity = (K) googleMapsService.findByGoogleID(entity);
        } else {//this is the case when user havn't/dont't want specified his/her location exactly (the case when location is displayed in his/her profile), so only city and state will be persisted
            entity = (K) googleMapsService.findLocalityByLatLng(entity);
        }

        //params might have changed -> try to map again
        existing = mapToExistingLocation(entity);
        if(existing != null) {
            return existing;
        }

        return super.create(entity);
    }



    @Override
    public K transferUpdateFields(K getEntity, K updatedEntity) {
        getEntity.setLongitude(updatedEntity.getLongitude());
        getEntity.setLatitude(updatedEntity.getLatitude());
        getEntity.setName(updatedEntity.getName());
        getEntity.setGoogleID(updatedEntity.getGoogleID());
        return getEntity;
    }

    /**
     * Compute distance in meter.
     * @param pointA
     * @param pointB
     * @return
     */
    public static double computeDistance(AbsLocation pointA, AbsLocation pointB) {
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
