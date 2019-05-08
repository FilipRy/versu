package com.filip.versu.controller;


import com.filip.versu.controller.abs.AbsAuthController;
import com.filip.versu.entity.dto.*;
import com.filip.versu.entity.model.Following;
import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.User;
import com.filip.versu.service.FollowingService;
import com.filip.versu.service.PostService;
import com.filip.versu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(AbsAuthController.API_URL_PREFIX + "/post")
public class PostController extends AbsAuthController<Long, Post, PostDTO> {

    @Autowired
    private PostService postService;

    @Autowired
    private FollowingService followingService;

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST)
    public PostDTO create(@RequestBody PostDTO shoppingItem) {
        User requester = authenticateUser();
        validation.validate(shoppingItem);
        Post postModel = postService.create(createModelFromDTO(shoppingItem), requester);
        return createDTOFromModel(postModel);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public PostDTO getDetails(@PathVariable("id") Long postId) {
        User requester = authenticateUser();
        Post post = postService.getDetails(postId, requester);
        return createDTOFromModel(post);
    }

//    @RequestMapping(method = RequestMethod.GET, value = "/details/{hexId}")
//    public PostDTO getDetailsBySecretUrl(@PathVariable("hexId") String hexId) {
//        User user = authenticateUserBySecretPostUrl(hexId);
//
//        Post post = postService.getDetailsBySecretUrl(hexId, user);
//        return createDTOFromModel(post);
//    }

//    @RequestMapping(method = RequestMethod.GET, value = "/detailsauth/{hexId}")
//    public PostDTO getDetailsBySecretUrlAuth(@PathVariable("hexId") String secretUrl) {
//
//        User requester = authenticateUserWithSecretLinkAccess(accessToken, secretUrl);
//
//        Post post = postService.getDetailsBySecretUrl(secretUrl, requester);
//        return createDTOFromModel(post);
//    }

    @RequestMapping(method = RequestMethod.GET, value = "/findForUser/{id}/time")
    public Page<PostDTO> findForUserByTime(@PathVariable("id") Long userID,
                                           @RequestParam(value = "lastId", required = false) Long lastLoadedId,
                                           Pageable pageable) {
        User requester = authenticateUser();
        Page<Post> shoppingItemPage = postService.findForUserByTime(userID, lastLoadedId, pageable, requester);
        return mapModelPageToDTOPage(shoppingItemPage, pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findForUser/{id}/location")
    public Page<PostDTO> findForUserByLocation(@PathVariable("id") Long userID,
                                               @RequestParam("lat") double latitude,
                                               @RequestParam("lng") double longitude,
                                               Pageable pageable) {
        User requester = authenticateUser();
        Page<Post> postPage = postService.findForUserByLocation(userID, pageable, requester, latitude, longitude);
        return mapModelPageToDTOPage(postPage, pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findByPossibilityNames")
    public Page<PostDTO> findByPossibilityNames(@RequestParam("possA") String possA,
                                                @RequestParam("possB") String possB,
                                                Pageable pageable) {
        User requester = authenticateUser();

        Page<Post> postPage = postService.findByFeedbackPossibilitiesName(possA, possB, requester, pageable);

        return mapModelPageToDTOPage(postPage, pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findByLocationGoogleId/{googleID}")
    public Page<PostDTO> findByLocationGoogleId(@PathVariable("googleID") String googleID,
                                                @RequestParam(value = "lastId", required = false) Long lastLoadedId,
                                                Pageable pageable) {
        User requester = authenticateUser();

        Page<Post> postPage = postService.findByLocationGoogleId(googleID, requester, pageable, lastLoadedId);
        return mapModelPageToDTOPage(postPage, pageable);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findVSPossibilities/{pattern}")
    public List<String[]> findVSPossibilities(@PathVariable("pattern") String pattern) {
        User requester = authenticateUser();
        List<String[]> vsPossibilitiesList = postService.findVSPossibilities(pattern, requester);
        return vsPossibilitiesList;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/userProfile/{id}")
    public UserProfileDTO getUserProfile(@PathVariable("id") Long userID,
                                         @RequestParam(value = "lastId", required = false) Long lastLoadedId,
                                         Pageable pageable) {

        User requester = authenticateUser();
        Page<Post> shoppingItemModelPage = postService.findPostsVisibleForViewer(userID, pageable, requester, lastLoadedId);

        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.userShoppingItems = mapModelPageToDTOPage(shoppingItemModelPage, pageable);

        User profileOwner = userService.get(userID);
        UserDTO profileOwnerDTO = new UserDTO(profileOwner);

        UserProfileDTO.UserCardDTO userCardDTO = new UserProfileDTO.UserCardDTO();
        userCardDTO.userDTO = profileOwnerDTO;

        int followingsCount = followingService.countFollowedByOfUser(userID);
        int followersCount = followingService.countFollowersOfUser(userID);

        userCardDTO.followersCount = followersCount;
        userCardDTO.followingsCount = followingsCount;

        //there cannot be a following between "me" and "me"
        if (!profileOwner.equals(requester)) {
            Following following = followingService.getFollowingByCreatorAndTarget(requester, profileOwner, requester);
            if (following != null) {
                FollowingDTO followingDTO = new FollowingDTO(following);
                userCardDTO.followingDTO = followingDTO;
            }
        }

        userProfileDTO.userCard = userCardDTO;

        return userProfileDTO;

    }


    @RequestMapping(method = RequestMethod.GET, value = "/viewers/{id}")
    public List<UserDTO> listViewersOfShoppingItem(@PathVariable("id") Long shoppingItemID) {
        User requester = authenticateUser();

        List<User> userModelList = postService.listViewers(shoppingItemID, requester);
        List<UserDTO> userDTOList = new ArrayList<>();

        if (userModelList != null) {
            for (User user : userModelList) {
                userDTOList.add(new UserDTO(user));
            }
        }

        return userDTOList;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public PostDTO update(@RequestBody PostDTO shoppingItem) {
        User requester = authenticateUser();

        validation.validateWithID(shoppingItem);

        Post postModel = createModelFromDTO(shoppingItem);

        return createDTOFromModel(postService.update(postModel, requester));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public PostDTO delete(@PathVariable("id") Long id) {
        User requester = authenticateUser();
        return createDTOFromModel(postService.delete(id, requester));
    }

    @RequestMapping(value = "generatePreSignedURL", method = RequestMethod.POST)
    public URLWrapperDTO getGeneratedPreSignedURL(@RequestBody URLWrapperDTO urlWrapperDTO) {
        User requester = authenticateUser();
        return postService.generatePreSignedURL(urlWrapperDTO);
    }


    @Override
    protected PostDTO createDTOFromModel(Post model) {
        return new PostDTO(model);
    }

    @Override
    protected Post createModelFromDTO(PostDTO dto) {
        return new Post(dto);
    }
}
