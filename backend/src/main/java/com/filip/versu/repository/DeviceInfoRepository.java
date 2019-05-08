package com.filip.versu.repository;

import com.filip.versu.entity.model.DeviceInfo;
import com.filip.versu.entity.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DeviceInfoRepository extends JpaRepository<DeviceInfo, Long> {

    public DeviceInfo findOneByDeviceRegistrationID(String deviceRegistrationID);

    public Page<DeviceInfo> findByOwner(User owner, Pageable pageable);

    public void removeByOwner(User owner);

}
