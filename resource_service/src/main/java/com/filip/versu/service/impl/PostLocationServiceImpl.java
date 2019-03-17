package com.filip.versu.service.impl;

import com.filip.versu.entity.model.PostLocation;
import com.filip.versu.repository.PostLocationRepository;
import com.filip.versu.service.PostLocationService;
import com.filip.versu.service.impl.abs.AbsLocationServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class PostLocationServiceImpl extends AbsLocationServiceImpl<PostLocation, PostLocationRepository> implements PostLocationService {

    @Override
    public PostLocation mapToExistingLocation(PostLocation location) {
        return repository.findOneByLongitudeAndLatitudeAndGoogleID(location.getLongitude(), location.getLatitude(), location.getGoogleID());
    }
}
