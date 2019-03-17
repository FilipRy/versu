package com.filip.versu.controller.abs;


import com.filip.versu.entity.dto.abs.AbsFeedbackPostPhotoDTO;
import com.filip.versu.entity.model.abs.AbsFeedbackPostPhoto;
import com.filip.versu.entity.model.User;
import com.filip.versu.service.abs.AbsFeedbackPostPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

public abstract class AbsFeedbackPostPhotoController<T extends AbsFeedbackPostPhotoDTO, L extends AbsFeedbackPostPhoto> extends AbsAuthController<Long, L, T> {

    @Autowired
    private AbsFeedbackPostPhotoService<L> feedbackPostPhotoService;

    @RequestMapping(method = RequestMethod.POST)
    public T create(@RequestBody T absFeedbackActionDTO, @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
        User requester = authenticateUser(accessToken);
        validation.validate(absFeedbackActionDTO);
        L absFeedbackActionModel = createModelFromDTO(absFeedbackActionDTO);
        T feedbackActionDTO = createDTOFromModel(feedbackPostPhotoService.create(absFeedbackActionModel, requester));
        return feedbackActionDTO;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findByUser/{id}")
    public Page<T> listByUser(@PathVariable("id") Long id, @RequestHeader(AUTHORIZATION_HEADER) String accessToken, Pageable pageable) {
        User requester = authenticateUser(accessToken);
        Page<L> modelPage = feedbackPostPhotoService.listOfUser(id, pageable, requester);
        return mapModelPageToDTOPage(modelPage, pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findByPhoto/{id}")
    public Page<T> listByPhoto(@PathVariable("id") Long id, @RequestHeader(AUTHORIZATION_HEADER) String accessToken, Pageable pageable) {
        User requester = authenticateUser(accessToken);
        Page<L> modelPage = feedbackPostPhotoService.listByPhoto(id, pageable, requester);
        return mapModelPageToDTOPage(modelPage, pageable);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public T delete(@PathVariable("id") Long id, @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
        User requester = authenticateUser(accessToken);
        return createDTOFromModel(feedbackPostPhotoService.delete(id, requester));
    }

}
