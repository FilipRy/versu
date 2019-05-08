package com.filip.versu.entity.model;

import com.filip.versu.entity.dto.NotificationDTO;
import com.filip.versu.entity.model.abs.AbsBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
@AllArgsConstructor
public class Notification extends AbsBaseEntity<Long> {

    public enum NotificationType {
        following, post, comment, post_feedback
    }

    /**
     * This is the user, who receives this notification
     */
    @Getter
    @Setter
    @ManyToOne
    private User target;

    @Getter
    @Setter
    private Long payloadId;

    @Getter
    @Setter
    private NotificationType notificationType;

    /**
     * The user, whose action (creation of vote-yes, comment...) led to creation of this notification.
     */
    @Getter
    @Setter
    @ManyToOne
    private User creator;


    @Getter
    @Setter
    private long lastUpdateTimestamp;

    @Getter
    @Setter
    private boolean seen;

    public Notification() {
        super();
    }

    public Notification(User target, Long payloadId, NotificationType notificationType, User creator) {
        super();
        this.target = target;
        this.payloadId = payloadId;
        this.notificationType = notificationType;
        this.creator = creator;
        this.seen = false;
        this.lastUpdateTimestamp = System.currentTimeMillis();
    }


    public Notification(NotificationDTO notificationDTO) {
        super(notificationDTO);
        this.lastUpdateTimestamp = notificationDTO.creationTime;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "target=" + target +
                ", payloadId=" + payloadId +
                ", notificationType=" + notificationType +
                ", creator=" + creator +
                ", lastUpdateTimestamp=" + lastUpdateTimestamp +
                ", seen=" + seen +
                '}';
    }
}
