package com.filip.versu.service;


import com.filip.versu.entity.model.DeviceInfo;
import com.filip.versu.entity.model.User;
import com.filip.versu.service.abs.CrudAuthService;
import com.filip.versu.service.abs.UserCompositionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface DeviceInfoService extends CrudAuthService<DeviceInfo, Long>, UserCompositionService<DeviceInfo> {

    /**
     * List a page of entities created by user = @userID.
     * @param userID
     * @param pageable
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<DeviceInfo> listOfUser(Long userID, Pageable pageable, User requester);

}
