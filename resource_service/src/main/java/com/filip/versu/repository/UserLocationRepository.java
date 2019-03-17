package com.filip.versu.repository;

import com.filip.versu.entity.model.UserLocation;
import com.filip.versu.repository.abs.AbsLocationRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserLocationRepository extends AbsLocationRepository<UserLocation> {
}
