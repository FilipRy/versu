package com.filip.versu.service.abs;


import com.filip.versu.entity.model.GoogleLocation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface GoogleLocationService extends CrudService<GoogleLocation, Long> {


    /**
     *
     * @param location
     * @return location, which is already persisted in DB and has the same lat and lng as @param location. If no such location is found -> return null
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public GoogleLocation mapToExistingLocation(GoogleLocation location);

}
