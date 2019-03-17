package com.filip.versu.entity.model.notification;

import com.filip.versu.entity.dto.NotificationDTO;
import com.filip.versu.entity.model.abs.AbsBaseEntity;
import com.filip.versu.entity.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
public abstract class Notification extends AbsBaseEntity<Long> {


    @Getter
    @Setter
    private NotificationDTO.NotificationType type;

    /**
     * This is the user who receive this notification. target != entityContent.owner - consider the case when creating new post.
     */
    @Getter
    @Setter
    @ManyToOne
    private User target;

    /**
     * The user whose action (creation of vote-yes, comment...) created this notification.
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
    }

}
