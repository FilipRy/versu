package com.filip.versu.entity.dto;


import com.filip.versu.entity.dto.abs.AbsFeedbackPostDTO;
import com.filip.versu.entity.model.Favourite;

public class FavouriteDTO extends AbsFeedbackPostDTO {

    public FavouriteDTO() {

    }

    public FavouriteDTO(Favourite other, boolean createdByShopItem) {
        super(other, createdByShopItem);
    }

}
