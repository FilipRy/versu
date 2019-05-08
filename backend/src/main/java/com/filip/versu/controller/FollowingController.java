package com.filip.versu.controller;


import com.filip.versu.controller.abs.AbsAuthController;
import com.filip.versu.entity.dto.FollowingDTO;
import com.filip.versu.entity.model.Following;
import com.filip.versu.entity.model.User;
import com.filip.versu.service.FollowingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AbsAuthController.API_URL_PREFIX + "/following")
public class FollowingController extends AbsAuthController<Long, Following, FollowingDTO> {

    @Autowired
    private FollowingService followingService;

    private final static Logger logger = LoggerFactory.getLogger(FollowingController.class);


    @RequestMapping(method = RequestMethod.POST)
    public FollowingDTO create(@RequestBody FollowingDTO following) {
        User requester = authenticateUser();

        validation.validate(following);

        Following followingModel = new Following(following);
        followingModel = followingService.create(followingModel, requester);

        FollowingDTO createdFollowingDTO = new FollowingDTO(followingModel);

        return createdFollowingDTO;
    }

    @RequestMapping(value = "/followers/{id}", method = RequestMethod.GET)
    public Page<FollowingDTO> listFollowers(@PathVariable("id") Long userID,
                                            @RequestParam(value = "lastId", required = false) Long lastLoadedId,
                                            Pageable pageable) {

        User requester = authenticateUser();

        if (logger.isInfoEnabled()) {
            logger.info("Arrived request to read FOLLOWERS of user " + userID + " by user " + requester.getId());
        }

        Page<Following> followingPage = followingService.listFollowersOfUser(userID, lastLoadedId, pageable, requester);

        Page<FollowingDTO> followingDTOPage = super.mapModelPageToDTOPage(followingPage, pageable);

        if (logger.isInfoEnabled()) {
            logger.info("Finished request to read FOLLOWERS of user " + userID + " by user " + requester.getId());
        }

        return followingDTOPage;
    }

    @RequestMapping(value = "/following/{id}", method = RequestMethod.GET)
    public Page<FollowingDTO> listFollowings(@PathVariable("id") Long userID,
                                             @RequestParam(value = "lastId", required = false) Long lastLoadedId,
                                             Pageable pageable) {

        User requester = authenticateUser();

        if (logger.isInfoEnabled()) {
            logger.info("Arrived request to read followings of user " + userID + " by user " + requester.getId());
        }

        Page<Following> followingPage = followingService.listFollowedByOfUser(userID, lastLoadedId, pageable, requester);

        Page<FollowingDTO> followingDTOPage = super.mapModelPageToDTOPage(followingPage, pageable);


        if (logger.isInfoEnabled()) {
            logger.info("Finished request to read followings of user " + userID + " by user " + requester.getId());
        }

        return followingDTOPage;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public FollowingDTO delete(@PathVariable("id") Long id) {

        User requester = authenticateUser();
        return createDTOFromModel(followingService.delete(id, requester));
    }

    @Override
    protected FollowingDTO createDTOFromModel(Following model) {
        return new FollowingDTO(model);
    }

    @Override
    protected Following createModelFromDTO(FollowingDTO dto) {
        return new Following(dto);
    }
}
