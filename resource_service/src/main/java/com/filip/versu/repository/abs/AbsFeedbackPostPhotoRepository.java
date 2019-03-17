package com.filip.versu.repository.abs;

import com.filip.versu.entity.model.PostPhoto;
import com.filip.versu.entity.model.abs.AbsFeedbackPostPhoto;
import com.filip.versu.entity.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface AbsFeedbackPostPhotoRepository<K extends AbsFeedbackPostPhoto> extends JpaRepository<K, Long> {

    public K findOneByOwnerAndPhoto(User owner, PostPhoto photo);

    public Page<K> findByPhoto(PostPhoto photo, Pageable pageable);

    public Page<K> findByOwner(User owner, Pageable pageable);

    public Long countByPhoto(PostPhoto photo);

    @Modifying
    @Query("update #{#entityName} fa set fa.isDeleted=true where fa.photo = :photo")
    public void markAsDeletedByPhoto(@Param("photo") PostPhoto photo);

    @Modifying
    @Query("update #{#entityName} fa set fa.isDeleted=true where fa.owner = :owner")
    public void markAsDeletedByCreator(@Param("owner") User owner);

}
