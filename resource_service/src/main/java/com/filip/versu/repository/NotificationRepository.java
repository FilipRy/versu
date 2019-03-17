package com.filip.versu.repository;

import com.filip.versu.entity.model.Following;
import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.User;
import com.filip.versu.entity.model.notification.Notification;
import com.filip.versu.entity.model.notification.FollowingNotification;
import com.filip.versu.entity.model.notification.PostNotification;
import com.filip.versu.entity.dto.NotificationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {


    public Page<Notification> findByTargetOrderByLastUpdateTimestampDesc(User target, Pageable pageable);

    @Query("select n from Notification n where n.target = :target and n.id < :lastId order by n.lastUpdateTimestamp desc ")
    public Page<Notification> findByTargetPaging(@Param("target") User target, @Param("lastId") Long lastLoadedId, Pageable pageable);

    public Page<Notification> findByTargetAndLastUpdateTimestampGreaterThanOrderByLastUpdateTimestampDesc(User target, long lastUpdateTimestamp, Pageable pageable);

    @Query("select n from Notification n where n.target = :target and n.id < :lastId and n.lastUpdateTimestamp > :lastUpdate order by n.lastUpdateTimestamp desc ")
    public Page<Notification> findByTargetUnseenPaging( @Param("target") User target, @Param("lastId") Long lastId,  @Param("lastUpdate") long lastUpdateTimestamp, Pageable pageable);

    public FollowingNotification findOneByTypeAndTargetAndEntityContentFoll(NotificationDTO.NotificationType type, User target, Following entityContentFoll);

    public PostNotification findOneByTypeAndTargetAndEntityContent(NotificationDTO.NotificationType type, User target, Post entityContent);

    @Query("DELETE from PostNotification sn  where sn.entityContent = :post")
    @Modifying
    public void removeNotificationsByPost(@Param("post") Post post);

    @Query("DELETE from FollowingNotification fn  where fn.entityContentFoll = :following")
    @Modifying
    public void removeNotificationsByFollowing(@Param("following") Following following);


}
