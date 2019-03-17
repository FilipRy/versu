package com.filip.versu.controller.abs;


import com.filip.versu.entity.dto.abs.AbsFeedbackPostDTO;
import com.filip.versu.entity.model.abs.AbsFeedbackPost;
import com.filip.versu.entity.model.User;
import com.filip.versu.service.abs.AbsFeedbackPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

public abstract class AbsFeedbackPostController<T extends AbsFeedbackPostDTO, L extends AbsFeedbackPost> extends AbsAuthController<Long, L, T> {

    @Autowired
    private AbsFeedbackPostService<L> feedbackPostService;


    @RequestMapping(method = RequestMethod.POST)
    public T create(@RequestBody T absFeedbackActionDTO, @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
        User requester = authenticateUser(accessToken);

        validation.validate(absFeedbackActionDTO);

        L absFeedbackActionModel = createModelFromDTO(absFeedbackActionDTO);

        return createDTOFromModel(feedbackPostService.create(absFeedbackActionModel, requester));
    }


    /**
     * @param absFeedbackActionDTO
     * @param accessToken
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/anonym")
    public T createAnonym(@RequestBody T absFeedbackActionDTO, @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
        validation.validate(absFeedbackActionDTO);

        String secretUrl = absFeedbackActionDTO.postDTO.secretUrl;
        User requester = authenticateUserWithSecretLinkAccess(accessToken, secretUrl);

        L absFeedbackActionModel = createModelFromDTO(absFeedbackActionDTO);
        return createDTOFromModel(feedbackPostService.create(absFeedbackActionModel, requester));
    }


    @RequestMapping(method = RequestMethod.GET, value = "/findByUser/{id}")
    public Page<T> listByUser(@PathVariable("id") Long id,
                              @RequestParam(value = "lastId", required = false) Long lastLoadedId,
                              @RequestHeader(AUTHORIZATION_HEADER) String accessToken,
                              Pageable pageable) {
        User requester = authenticateUser(accessToken);
        Page<L> modelPage = feedbackPostService.listOfUser(id, lastLoadedId, pageable, requester);
        return mapModelPageToDTOPage(modelPage, pageable);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/findByShoppingItem/{id}")
    public Page<T> listByPost(@PathVariable("id") Long id,
                              @RequestParam(value = "lastId", required = false) Long lastLoadedId,
                              @RequestHeader(AUTHORIZATION_HEADER) String accessToken,
                              Pageable pageable) {
        User requester = authenticateUser(accessToken);
        Page<L> modelPage = feedbackPostService.listByPostReversePaging(id, lastLoadedId, pageable, requester);
        return mapModelPageToDTOPage(modelPage, pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findByPost/{secretUrl}")
    public Page<T> listByPost(@PathVariable("secretUrl") String secretUrl,
                              @RequestParam(value = "lastId", required = false) Long lastLoadedId,
                              @RequestHeader(AUTHORIZATION_HEADER) String accessToken,
                              Pageable pageable) {
        User requester = authenticateUserWithSecretLinkAccess(accessToken, secretUrl);

        Page<L> modelPage = feedbackPostService.listByPostReversePaging(secretUrl, lastLoadedId, pageable, requester);
        return mapModelPageToDTOPage(modelPage, pageable);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public T delete(@PathVariable("id") Long id, @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
        User requester = authenticateUserWithSecretLinkAccess(accessToken, null);
        return createDTOFromModel(feedbackPostService.delete(id, requester));
    }

}



