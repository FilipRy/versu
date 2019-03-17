package com.filip.versu.entity.validation.abs;

import com.filip.versu.entity.dto.abs.AbsBaseEntityWithOwnerDTO;
import com.filip.versu.entity.validation.UserValidation;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class AbsBaseEntityWithOwnerValidation<L, K extends AbsBaseEntityWithOwnerDTO<L>> extends AbsBaseEntityValidation<L, K> {

    @Autowired
    protected UserValidation userValidation;

    @Override
    public void validate(K param) {
        super.validate(param);
        userValidation.validateWithID(param.owner);
    }

}
