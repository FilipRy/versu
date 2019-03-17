package com.filip.versu.entity.validation;


import com.filip.versu.entity.dto.FollowingDTO;
import com.filip.versu.entity.validation.abs.AbsBaseEntityValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FollowingValidation extends AbsBaseEntityValidation<Long, FollowingDTO> {

    @Autowired
    private UserValidation userValidation;

    @Override
    public void validate(FollowingDTO param) {
        super.validate(param);
        userValidation.validateWithID(param.creator);
        userValidation.validateWithID(param.target);
    }
}
