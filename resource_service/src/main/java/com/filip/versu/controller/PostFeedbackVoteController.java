package com.filip.versu.controller;


import com.filip.versu.controller.abs.AbsAuthController;
import com.filip.versu.entity.dto.PostFeedbackVoteDTO;
import com.filip.versu.entity.model.PostFeedbackVote;
import com.filip.versu.entity.model.User;
import com.filip.versu.service.PostFeedbackVoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AbsAuthController.API_URL_PREFIX + "/postfeedbackvote")
public class PostFeedbackVoteController extends AbsAuthController<Long, PostFeedbackVote, PostFeedbackVoteDTO> {

    @Autowired
    private PostFeedbackVoteService postFeedbackVoteService;

    @RequestMapping(method = RequestMethod.POST)
    public PostFeedbackVoteDTO create(@RequestBody PostFeedbackVoteDTO absFeedbackActionDTO) {

        User requester = authenticateUser();
        validation.validate(absFeedbackActionDTO);
        PostFeedbackVote absFeedbackActionModel = createModelFromDTO(absFeedbackActionDTO);
        return createDTOFromModel(postFeedbackVoteService.create(absFeedbackActionModel, requester));
    }


//    /**
//     * @param absFeedbackActionDTO
//     * @param accessToken
//     * @return
//     */
//    @RequestMapping(method = RequestMethod.POST, value = "/anonym")
//    public PostFeedbackVoteDTO createAnonym(@RequestBody PostFeedbackVoteDTO absFeedbackActionDTO, @RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
//        validation.validate(absFeedbackActionDTO);
//
//        String secretUrl = absFeedbackActionDTO.feedbackPossibilityDTO.postDTO.secretUrl;
//        User requester = authenticateUserWithSecretLinkAccess(accessToken, secretUrl);
//
//        PostFeedbackVote absFeedbackActionModel = createModelFromDTO(absFeedbackActionDTO);
//        return createDTOFromModel(postFeedbackVoteService.create(absFeedbackActionModel, requester));
//    }


    @RequestMapping(method = RequestMethod.GET, value = "/findByUser/{id}")
    public Page<PostFeedbackVoteDTO> listByUser(@PathVariable("id") Long id,
                                                @RequestParam(value = "lastId", required = false) Long lastLoadedId,
                                                Pageable pageable) {
        User requester = authenticateUser();
        Page<PostFeedbackVote> modelPage = postFeedbackVoteService.findByUserPaging(id, pageable, requester, lastLoadedId);
        return mapModelPageToDTOPage(modelPage, pageable);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/findByFeedbackPossibility/{id}")
    public Page<PostFeedbackVoteDTO> listByFeedbackPossibility(@PathVariable("id") Long id,
                                      @RequestParam(value = "lastId", required = false) Long lastLoadedId, Pageable pageable) {

        User requester = authenticateUser();
        Page<PostFeedbackVote> modelPage = postFeedbackVoteService.findByFeedbackPossibilityReversePaging(id, lastLoadedId, pageable, requester);

        return mapModelPageToDTOPage(modelPage, pageable);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public PostFeedbackVoteDTO delete(@PathVariable("id") Long id) {
//        User requester = authenticateUserWithSecretLinkAccess(accessToken, null);
        User requester = authenticateUser();
        return createDTOFromModel(postFeedbackVoteService.delete(id, requester));
    }



    @Override
    protected PostFeedbackVoteDTO createDTOFromModel(PostFeedbackVote model) {
        return new PostFeedbackVoteDTO(model, false);
    }

    @Override
    protected PostFeedbackVote createModelFromDTO(PostFeedbackVoteDTO dto) {
        return new PostFeedbackVote(dto);
    }
}
