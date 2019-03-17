package com.filip.versu.service;

import com.filip.versu.entity.model.abs.AbsLocation;


public interface GoogleMapsService {


    public AbsLocation findByGoogleID(AbsLocation location);

    public AbsLocation findLocalityByLatLng(AbsLocation location);

    public AbsLocation findAdminAreaByLatLng(AbsLocation location);

}
