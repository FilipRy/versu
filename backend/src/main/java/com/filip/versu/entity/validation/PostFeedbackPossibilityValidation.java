package com.filip.versu.entity.validation;

import com.filip.versu.entity.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.entity.validation.abs.AbsBaseEntityValidation;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.exception.ParameterException;
import org.springframework.stereotype.Component;


@Component
public class PostFeedbackPossibilityValidation extends AbsBaseEntityValidation<Long, PostFeedbackPossibilityDTO> {

    @Override
    public void validate(PostFeedbackPossibilityDTO param) {
        super.validate(param);

        String possibility = param.name;
        if (possibility == null || possibility.isEmpty()) {
            throw new ParameterException(ExceptionMessages.ParameterException.POST_FEEDBACK_POSSIBILITIES);
        }

        int spacePrafixLength = 0;
        for (int j = 0; j < possibility.length(); j++) {
            if (possibility.charAt(j) != ' ') {
                spacePrafixLength = j;
                break;
            }
        }
        possibility = possibility.substring(spacePrafixLength);

        int spaceSufixLenght = possibility.length();
        for (int j = possibility.length() - 1; j >= 0; j--) {
            if (possibility.charAt(j) != ' ') {
                spaceSufixLenght = j + 1;
                break;
            }
        }

        possibility = possibility.substring(0, spaceSufixLenght);

        possibility = possibility.replaceAll("#", "");
        possibility = possibility.replaceAll(" ", "_");

        param.name = possibility;

        if (param.name.length() > 255 || param.name.length() == 0) {
            throw new ParameterException(ExceptionMessages.ParameterException.POST_FEEDBACK_POSSIBILITIES);
        }

    }
}
