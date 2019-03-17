package com.filip.versu.repository;

import com.filip.versu.entity.model.Following;
import com.filip.versu.entity.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface FollowingRepository extends JpaRepository<Following, Long> {

    public Following findOneByCreatorAndTarget(User creator, User target);

    public Long countByCreator(User creator);

    public Long countByTarget(User target);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Following f WHERE f.creator = :creator AND f.target = :target")
    boolean existsByCreatorAndTarget(@Param("creator") User creator, @Param("target") User target);

    public Page<Following> findByCreatorOrTarget(User creator, User target, Pageable pageable);

    @Query(value = "select f from Following f join fetch f.target where f.creator = :creator order by f.id desc ", countQuery = "select count(f) from Following f where f.creator = :creator order by f.id desc")
    public Page<Following> findByCreator(@Param("creator") User creator, Pageable pageable);


    @Query(value = "select f from Following f join fetch f.target where f.creator = :creator and f.id < :lastId order by f.id desc ", countQuery = "select count(f) from Following f where f.creator = :creator and f.id < :lastId order by f.id desc")
    public Page<Following> findByCreatorPaging(@Param("creator") User creator, @Param("lastId") Long lastId, Pageable pageable);


    @Query(value = "select f from Following f join fetch f.creator where f.target = :target order by f.id desc ", countQuery = "select count(f) from Following f where f.target = :target order by f.id desc")
    public Page<Following> findByTarget(@Param("target") User target, Pageable pageable);

    @Query(value = "select f from Following f join fetch f.creator where f.target = :target and f.id < :lastId order by f.id desc ", countQuery = "select count(f) from Following f where f.target = :target and f.id < :lastId order by f.id desc")
    public Page<Following> findByTargetPaging(@Param("target") User target, @Param("lastId") Long lastId, Pageable pageable);

    public void removeByCreatorOrTarget(User creator, User target);

}
