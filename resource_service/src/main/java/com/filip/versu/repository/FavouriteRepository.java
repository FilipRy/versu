package com.filip.versu.repository;

import com.filip.versu.entity.model.Favourite;
import com.filip.versu.repository.abs.AbsFeedbackPostRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FavouriteRepository extends AbsFeedbackPostRepository<Favourite> {
}
