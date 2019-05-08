package com.filip.versu.entity.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.filip.versu.entity.dto.abs.AbsBaseEntityWithOwnerDTO;
import com.filip.versu.entity.model.DeviceInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceInfoDTO extends AbsBaseEntityWithOwnerDTO<Long> {

    public String deviceRegistrationID;
    public String deviceInformation;
    public long registrationTime;

    public DeviceInfoDTO() {

    }


    public DeviceInfoDTO(DeviceInfo other) {
        super(other);
        this.deviceRegistrationID = other.getDeviceRegistrationID();
        this.deviceInformation = other.getDeviceInformation();
        this.registrationTime = other.getRegistrationTime();
    }

}
