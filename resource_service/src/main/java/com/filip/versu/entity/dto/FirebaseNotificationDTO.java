package com.filip.versu.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
public class FirebaseNotificationDTO implements Serializable {

    public static final String COLLAPSE_KEY = "collapse_key";

    public static final int ONE_DAY_TTL = 24 * 60 * 60;

    @Getter
    @Setter
    private String collapse_key;

    /**
     * Whether notification should be delayed or not if device is offline.
     */
    @Getter
    @Setter
    private boolean delay_while_idle;

    /**
     * Time to live in seconds.
     */
    @Getter
    @Setter
    private int time_to_live;

    /**
     * this is a notification key
     */
    @Getter
    @Setter
    private String to;

    @Getter
    @Setter
    private FirebaseNotificationBody notification;

    @Getter
    @Setter
    private NotificationDTO data;

    public FirebaseNotificationDTO() {
        collapse_key = COLLAPSE_KEY;
        delay_while_idle = true;
        time_to_live = ONE_DAY_TTL;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FirebaseNotificationBody implements Serializable {

        public static final String TITLE_SYNC_PROMPT = "Versu";
        public static final String BODY_SYNC_PROMPT = "There is something new!";

        @Getter
        @Setter
        private String title;

        @Getter
        @Setter
        private String body;

        public FirebaseNotificationBody() {
            title = TITLE_SYNC_PROMPT;
            body = BODY_SYNC_PROMPT;
        }
    }

}
