package com.filip.versu.service.impl;

import com.filip.versu.entity.model.DeviceInfo;
import com.filip.versu.entity.model.User;
import com.filip.versu.exception.EntityNotExistsException;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.exception.UnauthorizedException;
import com.filip.versu.repository.DeviceInfoRepository;
import com.filip.versu.service.DeviceInfoService;
import com.filip.versu.service.UserService;
import com.filip.versu.service.impl.abs.AbsCrudAuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DeviceInfoServiceImpl extends AbsCrudAuthServiceImpl<DeviceInfo, Long, DeviceInfoRepository> implements DeviceInfoService {

    @Autowired
    private UserService userService;

    @Override
    protected void verifyExistingRelationships(DeviceInfo entity, User requester) {
        User user = userService.get(entity.getOwner().getId());
        if(user == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.USER);
        }

        DeviceInfo existing = repository.findOneByDeviceRegistrationID(entity.getDeviceRegistrationID());
        //if there is already a device persisted with this registration id.
        if(existing != null) {
            delete(existing.getId());
        }
    }

    @Override
    public void removeOfUser(User user, User requester) {
        User getUser = userService.get(user.getId());

        if(getUser == null) {
            return;
        }

        if(!getUser.getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }
        repository.removeByOwner(user);
    }

    @Override
    public Page<DeviceInfo> listOfUser(Long userID, Pageable pageable, User requester) {
        User user = userService.get(userID);

        //if user does not exists
        if(user == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.USER);
        }

        if(!user.getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }
        return repository.findByOwner(user, pageable);
    }


    @Override
    public DeviceInfo transferUpdateFields(DeviceInfo getEntity, DeviceInfo updatedEntity) {
        getEntity.setDeviceInformation(updatedEntity.getDeviceInformation());
        getEntity.setDeviceRegistrationID(updatedEntity.getDeviceRegistrationID());
        getEntity.setOwner(updatedEntity.getOwner());
        return getEntity;
    }
}
