package com.filip.versu.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * This is a DTO to deliver a response from Google Maps API for reverse geocoding.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodeResultDTO implements Serializable {

    @Getter
    @Setter
    private String status;

    @Getter
    @Setter
    private List<FormattedAddress> results = new ArrayList<>();

    public GeocodeResultDTO() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FormattedAddress implements Serializable {

        @Getter
        @Setter
        private String formatted_address;

        @Getter
        @Setter
        private Geometry geometry;

        @Getter
        @Setter
        private String place_id;

        public FormattedAddress() {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Geometry implements Serializable {

        @Getter
        @Setter
        private Location location;

        public Geometry() {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Location implements Serializable {

            @Getter
            @Setter
            private double lat;

            @Getter
            @Setter
            private double lng;

            public Location() {
            }
        }

    }


}
