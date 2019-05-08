package com.filip.versu.repository;

import com.filip.versu.entity.model.User;
import com.filip.versu.entity.model.abs.AbsBaseEntity;
import com.filip.versu.entity.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    public Page<Notification> findByTargetOrderByLastUpdateTimestampDesc(User target, Pageable pageable);

    @Query("select n from Notification n where n.target = :target and n.id < :lastId order by n.lastUpdateTimestamp desc ")
    public Page<Notification> findByTargetPaging(@Param("target") User target, @Param("lastId") Long lastLoadedId, Pageable pageable);

    @Query("select n from Notification n where n.target = :target and n.lastUpdateTimestamp > :lastUpdateTimestamp and n.seen = :seen order by n.lastUpdateTimestamp desc ")
    public Page<Notification> findByTargetLastUpdateTimestampSeenOrderByLastUpdateTimestamp(@Param("target") User target,@Param("lastUpdateTimestamp") long lastUpdateTimestamp, @Param("seen") boolean seen, Pageable pageable);

    @Query("select n from Notification n where n.target = :target and n.id < :lastId and n.lastUpdateTimestamp > :lastUpdate and n.seen = false order by n.lastUpdateTimestamp desc ")
    public Page<Notification> findByTargetUnseenPaging(@Param("target") User target, @Param("lastId") Long lastId, @Param("lastUpdate") long lastUpdateTimestamp, Pageable pageable);

    public Notification findOneByTargetAndNotificationTypeAndPayloadId(User target, Notification.NotificationType notificationType, Long payloadId);

    public void deleteByCreator(User creator);

    public void deleteByTarget(User target);

    public void deleteByNotificationTypeAndPayloadId(Notification.NotificationType notificationType, Long payloadId);

}
