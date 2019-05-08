package com.filip.versu.service;

import com.filip.versu.entity.model.GoogleLocation;


public interface GoogleMapsService {


    public GoogleLocation findByGoogleID(GoogleLocation location);

    public GoogleLocation findLocalityByLatLng(GoogleLocation location);

    public GoogleLocation findAdminAreaByLatLng(GoogleLocation location);

}
