package com.filip.versu.entity.model;

import com.filip.versu.entity.dto.GoogleLocationDTO;
import com.filip.versu.entity.model.abs.AbsBaseEntity;
import com.filip.versu.repository.DBHelper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
public class GoogleLocation extends AbsBaseEntity<Long> {


    public static final String NAME_UKNOWN = "Far, far away";
    public static final double LAT_UNKNOWN = -300;
    public static final double LON_UNKNOWN = -300;
    /**
     * -300 indicates longitude is unknown
     */
    @Getter
    @Setter
    protected double longitude;
    /**
     * -300 indicates latitude is unknown
     */
    @Getter
    @Setter
    protected double latitude;
    /**
     * This is a human-readable name of the location, e.g. Eurovea, Pribinova, Bratislava, Slovakia
     */
    @Getter
    @Setter
    protected String name;

    /**
     * This is the city name of this location.
     */
    @Getter
    @Setter
    protected String cityName;

    /**
     * This is googleID of city.
     */
    @Getter
    @Setter
    private String cityGoogleId;

    /**
     * This is an ID by google for this place.
     */
    @Getter
    @Setter
    private String googleID;


    public GoogleLocation(GoogleLocationDTO other) {
        super(other);
        this.latitude = other.latitude;
        this.longitude = other.longitude;
        this.name = other.name;
    }

    public GoogleLocation() {
        super();
        longitude = LON_UNKNOWN;
        latitude = LAT_UNKNOWN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GoogleLocation location = (GoogleLocation) o;

        if (Double.compare(location.longitude, longitude) != 0) return false;
        if (Double.compare(location.latitude, latitude) != 0) return false;
        return name != null ? name.equals(location.name) : location.name == null;

    }
}
