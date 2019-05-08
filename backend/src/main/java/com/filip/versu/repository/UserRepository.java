package com.filip.versu.repository;


import com.filip.versu.entity.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    public User findOneByUsername(String username);

    public User findOneByEmail(String email);

    @Modifying
    @Query("update User u set u.lastNotificationRefreshTimestamp = :timestamp where u.id = :userId")
    public void updateLastNotificationRefreshTimestamp(@Param("userId") Long userId, @Param("timestamp") long timestamp);

    @Query("select u from User u where u.username LIKE %:username%")
    public Page<User> findByUsernameLike(@Param("username") String username, Pageable pageable);

}
