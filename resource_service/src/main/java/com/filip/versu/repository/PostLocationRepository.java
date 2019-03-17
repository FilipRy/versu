package com.filip.versu.repository;

import com.filip.versu.entity.model.PostLocation;
import com.filip.versu.repository.abs.AbsLocationRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostLocationRepository extends AbsLocationRepository<PostLocation> {
}
