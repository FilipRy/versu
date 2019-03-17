package com.filip.versu.entity.validation;

import com.filip.versu.entity.dto.CommentDTO;
import com.filip.versu.entity.validation.abs.AbsFeedbackShopItemValidation;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.exception.ParameterException;
import org.springframework.stereotype.Component;

@Component
public class CommentValidation extends AbsFeedbackShopItemValidation<CommentDTO> {

    @Override
    public void validate(CommentDTO param) {
        super.validate(param);
        if(param.content == null || param.content.length() < 2 || param.content.length() > 191) {
            throw new ParameterException(ExceptionMessages.ParameterException.COMMENT_CONTENT);
        }
    }
}
