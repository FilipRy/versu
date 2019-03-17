package com.filip.versu.entity.model.notification;

import com.filip.versu.entity.model.Following;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
public class FollowingNotification extends Notification {

    //TODO put this field into super class!!
    @ManyToOne
    private Following entityContentFoll;

    public FollowingNotification() {
    }

    public Following getEntityContent() {
        return entityContentFoll;
    }

    public void setEntityContent(Following entityContent) {
        this.entityContentFoll = entityContent;
    }
}
