package com.filip.versu.entity.validation.abs;


import com.filip.versu.entity.dto.abs.AbsFeedbackPostDTO;
import com.filip.versu.entity.validation.PostValidation;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbsFeedbackShopItemValidation<K extends AbsFeedbackPostDTO> extends AbsBaseEntityWithOwnerValidation<Long, K> {

    @Autowired
    private PostValidation postValidation;

    @Override
    public void validate(K param) {
        super.validate(param);
        postValidation.validate(param.postDTO);
    }
}
