package com.filip.versu.entity.validation.abs;

import com.filip.versu.entity.dto.abs.AbsFeedbackPostPhotoDTO;
import com.filip.versu.entity.validation.PostPhotoValidation;
import org.springframework.beans.factory.annotation.Autowired;



public abstract class AbsFeedbackActionValidation<K extends AbsFeedbackPostPhotoDTO> extends AbsBaseEntityWithOwnerValidation<Long, K> {

    @Autowired
    private PostPhotoValidation photoValidation;

    @Override
    public void validate(K param) {
        super.validate(param);
        photoValidation.validateWithID(param.photo);
    }
}
