package com.filip.versu.entity.model.notification;


import com.filip.versu.entity.model.Post;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class PostNotification extends Notification {

    //TODO put this field into super class!!
    @ManyToOne
    @Getter
    @Setter
    private Post entityContent;

    public PostNotification() {
    }

}
