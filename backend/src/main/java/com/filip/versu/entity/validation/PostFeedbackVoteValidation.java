package com.filip.versu.entity.validation;

import com.filip.versu.entity.dto.PostFeedbackVoteDTO;
import com.filip.versu.entity.validation.abs.AbsBaseEntityWithOwnerValidation;
import org.springframework.stereotype.Component;

@Component
public class PostFeedbackVoteValidation extends AbsBaseEntityWithOwnerValidation<Long, PostFeedbackVoteDTO> {

    @Override
    public void validate(PostFeedbackVoteDTO param) {
        super.validate(param);

    }
}
