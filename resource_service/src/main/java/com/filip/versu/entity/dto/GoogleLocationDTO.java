package com.filip.versu.entity.dto;

import com.filip.versu.entity.dto.abs.AbsBaseEntityDTO;
import com.filip.versu.entity.model.GoogleLocation;


public class GoogleLocationDTO extends AbsBaseEntityDTO<Long> {

    public double latitude;
    public double longitude;

    /**
     * This is an ID by google for this place.
     */
    public String googleID;

    /**
     * This is a human-readable name of the location
     */
    public String name;

    public GoogleLocationDTO() {
        super();
    }

    public GoogleLocationDTO(GoogleLocation other) {
        super(other);
        this.name = other.getName();
        this.latitude = other.getLatitude();
        this.longitude = other.getLongitude();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoogleLocationDTO that = (GoogleLocationDTO) o;

        if (Double.compare(that.latitude, latitude) != 0) return false;
        if (Double.compare(that.longitude, longitude) != 0) return false;
        if (googleID != null ? !googleID.equals(that.googleID) : that.googleID != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

}
