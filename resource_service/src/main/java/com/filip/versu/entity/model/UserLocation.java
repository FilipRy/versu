package com.filip.versu.entity.model;

import com.filip.versu.entity.dto.LocationDTO;
import com.filip.versu.entity.model.abs.AbsLocation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("U")
public class UserLocation extends AbsLocation {

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "location")
    @Getter
    @Setter
    private List<User> users = new ArrayList<>();

    public UserLocation(LocationDTO other) {
        super(other);
    }

    public UserLocation() {
        super();
    }
}
