package com.filip.versu.entity.validation.abs;


import com.filip.versu.entity.dto.abs.AbsBaseEntityDTO;
import com.filip.versu.entity.validation.Validation;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.exception.ParameterException;

public abstract class AbsBaseEntityValidation<L, K extends AbsBaseEntityDTO<L>> implements Validation<L,K> {

    /**
     * This validates the @param entity without the ID. This method is used for validation in create() methods.
     * @param param
     */
    @Override
    public void validate(K param) {
        if(param == null) {
            throw new ParameterException(ExceptionMessages.ParameterException.PARAM_MISSING);
        }
    }

    /**
     * This validates the @param entity including the ID. This method is used for validation in update() methods.
     * @param param
     */
    @Override
    public void validateWithID(K param) {
        if(param == null) {
            throw new ParameterException(ExceptionMessages.ParameterException.PARAM_MISSING);
        }
        if(param.id == null) {
            throw new ParameterException(ExceptionMessages.ParameterException.ID_MISSING);
        }
        validate(param);
    }

}
