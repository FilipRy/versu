package com.filip.versu.entity.model;


import com.filip.versu.entity.dto.FavouriteDTO;
import com.filip.versu.entity.model.abs.AbsFeedbackPost;
import com.filip.versu.repository.DBHelper;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = DBHelper.TablesNames.FAVOURITE)
@Where(clause = "is_deleted=0")
public class Favourite extends AbsFeedbackPost {

    public Favourite() {}

    public Favourite(FavouriteDTO other) {
        super(other);
    }
}
