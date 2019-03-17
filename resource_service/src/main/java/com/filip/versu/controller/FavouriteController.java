package com.filip.versu.controller;


import com.filip.versu.controller.abs.AbsAuthController;
import com.filip.versu.controller.abs.AbsFeedbackPostController;
import com.filip.versu.entity.dto.FavouriteDTO;
import com.filip.versu.entity.model.Favourite;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AbsAuthController.API_URL_PREFIX + "/favourite")
public class FavouriteController extends AbsFeedbackPostController<FavouriteDTO, Favourite> {

    @Override
    protected FavouriteDTO createDTOFromModel(Favourite model) {
        return new FavouriteDTO(model, false);
    }

    @Override
    protected Favourite createModelFromDTO(FavouriteDTO dto) {
        return new Favourite(dto);
    }
}
