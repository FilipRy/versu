package com.filip.versu.entity.validation;

import com.filip.versu.entity.dto.DeviceInfoDTO;
import com.filip.versu.entity.validation.abs.AbsBaseEntityWithOwnerValidation;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.exception.ParameterException;
import org.springframework.stereotype.Component;

@Component
public class DeviceInfoValidation extends AbsBaseEntityWithOwnerValidation<Long, DeviceInfoDTO> {

    @Override
    public void validate(DeviceInfoDTO param) {
        super.validate(param);

        if(param.deviceInformation == null || param.deviceInformation.isEmpty()) {
            throw new ParameterException(ExceptionMessages.ParameterException.DEVICE_INFO_MISSING);
        }

        if(param.deviceRegistrationID == null || param.deviceRegistrationID.isEmpty()) {
            throw new ParameterException(ExceptionMessages.ParameterException.DEVICE_INFO_REGISTRATION_ID);
        }

    }
}
