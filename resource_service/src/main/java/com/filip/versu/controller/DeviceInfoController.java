package com.filip.versu.controller;


import com.filip.versu.controller.abs.AbsAuthController;
import com.filip.versu.entity.dto.DeviceInfoDTO;
import com.filip.versu.entity.model.DeviceInfo;
import com.filip.versu.entity.model.User;
import com.filip.versu.service.DeviceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AbsAuthController.API_URL_PREFIX + "/deviceinfo")
public class DeviceInfoController extends AbsAuthController<Long, DeviceInfo, DeviceInfoDTO> {

    @Autowired
    private DeviceInfoService deviceInfoService;

    @RequestMapping(method = RequestMethod.POST)
    public DeviceInfoDTO create(@RequestBody DeviceInfoDTO deviceInfoDTO, @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
        User requester = authenticateUser(accessToken);

        DeviceInfo deviceInfo = deviceInfoService.create(createModelFromDTO(deviceInfoDTO), requester);
        return createDTOFromModel(deviceInfo);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public DeviceInfoDTO update(@RequestBody DeviceInfoDTO deviceInfoDTO, @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
        User requester = authenticateUser(accessToken);

        DeviceInfo deviceInfo = createModelFromDTO(deviceInfoDTO);

        return createDTOFromModel(deviceInfoService.update(deviceInfo, requester));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public DeviceInfoDTO delete(@PathVariable("id") Long id, @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
        User requester = authenticateUser(accessToken);

        DeviceInfo deletedDeviceInfo = deviceInfoService.delete(id, requester);

        return deletedDeviceInfo == null ? null : createDTOFromModel(deletedDeviceInfo);
    }

    @Override
    protected DeviceInfoDTO createDTOFromModel(DeviceInfo model) {
        return new DeviceInfoDTO(model);
    }

    @Override
    protected DeviceInfo createModelFromDTO(DeviceInfoDTO dto) {
        return new DeviceInfo(dto);
    }
}
