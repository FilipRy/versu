package com.filip.versu.service.impl;

import com.filip.versu.entity.model.UserLocation;
import com.filip.versu.repository.UserLocationRepository;
import com.filip.versu.service.UserLocationService;
import com.filip.versu.service.impl.abs.AbsLocationServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserLocationServiceImpl extends AbsLocationServiceImpl<UserLocation, UserLocationRepository> implements UserLocationService {

    @Override
    public UserLocation mapToExistingLocation(UserLocation location) {
        return repository.findOneByLongitudeAndLatitudeAndGoogleID(location.getLongitude(), location.getLatitude(), location.getGoogleID());
    }
}
