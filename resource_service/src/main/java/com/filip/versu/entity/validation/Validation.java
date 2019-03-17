package com.filip.versu.entity.validation;

import com.filip.versu.entity.dto.abs.AbsBaseEntityDTO;


public interface Validation<L, K extends AbsBaseEntityDTO<L>> {
    void validate(K param);

    void validateWithID(K param);
}
