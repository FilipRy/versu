package com.filip.versu.entity.model;

import com.filip.versu.entity.dto.DeviceInfoDTO;
import com.filip.versu.entity.model.abs.AbsBaseEntityWithOwner;
import com.filip.versu.repository.DBHelper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = DBHelper.TablesNames.DEVICE_INFO)
public class DeviceInfo extends AbsBaseEntityWithOwner<Long> {

    /*
     * The Firebase Cloud Messaging registration token for the device. This token
	 * indicates that the device is able to receive messages sent via FCM.
	 */
    @Getter
    @Setter
    private String deviceRegistrationID;

    /*
     * Some identifying information about the device, such as its manufacturer
     * and product name.
     */
    @Getter
    @Setter
    private String deviceInformation;

    /*
     * Timestamp indicating when this device registered with the application.
     */
    @Getter
    @Setter
    private long registrationTime;

    public DeviceInfo() {

    }

    public DeviceInfo(DeviceInfoDTO other) {
        super(other);
        this.deviceRegistrationID = other.deviceRegistrationID;
        this.deviceInformation = other.deviceInformation;
        this.registrationTime = other.registrationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DeviceInfo that = (DeviceInfo) o;

        if (registrationTime != that.registrationTime) return false;
        if (deviceRegistrationID != null ? !deviceRegistrationID.equals(that.deviceRegistrationID) : that.deviceRegistrationID != null)
            return false;
        return !(deviceInformation != null ? !deviceInformation.equals(that.deviceInformation) : that.deviceInformation != null);
    }

}
