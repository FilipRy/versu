package com.filip.versu.repository;

import com.filip.versu.entity.model.PostPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostPhotoRepository extends JpaRepository<PostPhoto, Long> {
}
