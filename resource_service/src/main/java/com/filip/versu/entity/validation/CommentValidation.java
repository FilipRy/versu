package com.filip.versu.entity.validation;

import com.filip.versu.entity.dto.CommentDTO;
import com.filip.versu.entity.validation.abs.AbsBaseEntityWithOwnerValidation;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.exception.ParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentValidation extends AbsBaseEntityWithOwnerValidation<Long, CommentDTO> {

    @Autowired
    private PostValidation postValidation;

    @Override
    public void validate(CommentDTO param) {
        super.validate(param);
        postValidation.validate(param.postDTO);
        if(param.content == null || param.content.length() < 2 || param.content.length() > 191) {
            throw new ParameterException(ExceptionMessages.ParameterException.COMMENT_CONTENT);
        }
    }
}
