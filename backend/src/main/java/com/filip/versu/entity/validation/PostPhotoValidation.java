package com.filip.versu.entity.validation;

import com.filip.versu.entity.dto.PostPhotoDTO;
import com.filip.versu.entity.validation.abs.AbsBaseEntityValidation;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.exception.ParameterException;
import org.springframework.stereotype.Component;


@Component
public class PostPhotoValidation extends AbsBaseEntityValidation<Long, PostPhotoDTO> {


    @Override
    public void validate(PostPhotoDTO param) {
        super.validate(param);

        if(param == null || param.path == null || param.path.isEmpty() || param.takenTime <= 0) {
            throw new ParameterException(ExceptionMessages.ParameterException.SHOPPING_ITEM_PHOTO);
        }
        //the shopping item of the photo does not need to be validated, because PhotoValidation is started only from ShoppingItemValidation and ShoppingItemValidation already validates post.
    }
}
