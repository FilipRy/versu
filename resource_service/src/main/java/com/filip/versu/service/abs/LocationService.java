package com.filip.versu.service.abs;


import com.filip.versu.entity.model.abs.AbsLocation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface LocationService<K extends AbsLocation> extends CrudService<K, Long> {


    /**
     *
     * @param location
     * @return location, which is already persisted in DB and has the same lat and lng as @param location. If no such location is found -> return null
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public K mapToExistingLocation(K location);

}
