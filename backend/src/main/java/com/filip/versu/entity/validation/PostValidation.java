package com.filip.versu.entity.validation;

import com.filip.versu.entity.dto.PostDTO;
import com.filip.versu.entity.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.entity.dto.PostPhotoDTO;
import com.filip.versu.entity.dto.UserDTO;
import com.filip.versu.entity.validation.abs.AbsBaseEntityWithOwnerValidation;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.exception.ParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostValidation extends AbsBaseEntityWithOwnerValidation<Long, PostDTO> {

    @Autowired
    private PostPhotoValidation photoValidation;

    @Autowired
    private PostFeedbackPossibilityValidation postFeedbackPossibilityValidation;

    @Override
    public void validate(PostDTO param) {
        super.validate(param);

        if (param.viewers != null) {
            for (UserDTO userDTO : param.viewers) {
                userValidation.validateWithID(userDTO);
            }
        }

        if (param.postFeedbackPossibilities == null) {
            throw new ParameterException(ExceptionMessages.ParameterException.POST_FEEDBACK_POSSIBILITIES);
        }

        if (param.postFeedbackPossibilities.size() != 2) {
            throw new ParameterException(ExceptionMessages.ParameterException.POST_FEEDBACK_POSSIBILITIES);
        }

        if (param.postFeedbackPossibilities.get(0).equals(param.postFeedbackPossibilities.get(1))) {
            throw new ParameterException(ExceptionMessages.ParameterException.POST_FEEDBACK_POSSIBILITIES);
        }

        for(PostFeedbackPossibilityDTO postFeedbackPossibilityDTO: param.postFeedbackPossibilities) {
            postFeedbackPossibilityValidation.validate(postFeedbackPossibilityDTO);
        }

        if (param.accessType == null)

        {
            throw new ParameterException(ExceptionMessages.ParameterException.SHOPPING_ITEM_ACCESS_TYPE);
        }

        if (param.photos == null || param.photos.size() < 1)

        {
            throw new ParameterException(ExceptionMessages.ParameterException.SHOPPING_ITEM_PHOTO);
        }

        for (
                PostPhotoDTO photo
                : param.photos)

        {
            photoValidation.validate(photo);
        }

        if (param.description != null && param.description.length() > 255) {
            throw new ParameterException(ExceptionMessages.ParameterException.SHOPPING_ITEM_DESC);
        }
    }


}
