package com.filip.versu.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a DTO to request for notification key from Firebase CM servers.
 */
public class FirebaseNotifKeyExchanger implements Serializable {

    @Getter
    @Setter
    private String operation;

    @Getter
    @Setter
    private String notification_key_name;

    @Getter
    @Setter
    private List<String> registration_ids = new ArrayList<>();

    public FirebaseNotifKeyExchanger() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FirebaseNotificationKey implements Serializable {

        @Getter
        @Setter
        private String notification_key;

        public FirebaseNotificationKey() {
        }
    }

}
