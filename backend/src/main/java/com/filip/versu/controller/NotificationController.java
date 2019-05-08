package com.filip.versu.controller;


import com.filip.versu.controller.abs.AbsAuthController;
import com.filip.versu.entity.dto.NotificationDTO;
import com.filip.versu.entity.model.User;
import com.filip.versu.entity.model.Notification;
import com.filip.versu.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AbsAuthController.API_URL_PREFIX + "/notification")
public class NotificationController extends AbsAuthController<Long, Notification, NotificationDTO> {

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public List<NotificationDTO> listNotificationsOfUser(@PathVariable("id") Long userID,
                                                         @RequestParam(value = "lastId", required = false) Long lastLoadedId,
                                                         Pageable pageable) {
        User requester = authenticateUser();
        return notificationService.listNotificationsOfUser(userID, lastLoadedId, pageable, requester, false);
    }

    @RequestMapping(value = "/{id}/seen", method = RequestMethod.GET)
    public boolean markAsSeen(@PathVariable("id") Long notificationId) {
        User requester = authenticateUser();
        return notificationService.markAsSeen(notificationId, requester);
    }


    @Override
    protected NotificationDTO createDTOFromModel(Notification model) {
        return null;
    }

    @Override
    protected Notification createModelFromDTO(NotificationDTO dto) {
        return null;
    }
}
