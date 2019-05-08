package com.filip.versu.repository;

import com.filip.versu.entity.model.GoogleLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GoogleLocationRepository extends JpaRepository<GoogleLocation, Long> {

    public GoogleLocation findOneByLongitudeAndLatitudeAndGoogleID(double longitude, double latitude, String googleID);

}
