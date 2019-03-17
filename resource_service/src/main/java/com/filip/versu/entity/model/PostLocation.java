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
@DiscriminatorValue("P")
public class PostLocation extends AbsLocation {


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "location")
    @Getter
    @Setter
    private List<Post> posts = new ArrayList<>();

    public PostLocation() {
        super();
    }

    public PostLocation(LocationDTO locationDTO) {
        super(locationDTO);
    }



}
