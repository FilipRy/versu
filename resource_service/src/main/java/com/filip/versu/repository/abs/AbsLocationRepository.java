package com.filip.versu.repository.abs;

import com.filip.versu.entity.model.abs.AbsLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface AbsLocationRepository<K extends AbsLocation> extends JpaRepository<K, Long> {

    public K findOneByLongitudeAndLatitudeAndGoogleID(double longitude, double latitude, String googleID);

}
