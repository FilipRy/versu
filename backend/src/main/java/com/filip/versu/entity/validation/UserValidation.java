package com.filip.versu.entity.validation;

import com.filip.versu.entity.dto.UserDTO;
import com.filip.versu.entity.validation.abs.AbsBaseEntityValidation;
import org.springframework.stereotype.Component;


@Component
public class UserValidation extends AbsBaseEntityValidation<Long, UserDTO> {

    @Override
    public void validate(UserDTO param) {
        super.validate(param);
        if(param.username == null) {

        }
        if(param.username.length() < 3 || param.username.length() > 255) {

        }
        if(param.email.length() < 7 || param.email.length() > 255) {

        }

        //TODO password

    }

}
