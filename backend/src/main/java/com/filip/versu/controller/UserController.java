package com.filip.versu.controller;


import com.filip.versu.controller.abs.AbsAuthController;
import com.filip.versu.entity.dto.UserDTO;
import com.filip.versu.entity.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AbsAuthController.API_URL_PREFIX + "/user")
public class UserController extends AbsAuthController<Long, User, UserDTO> {


//    /**
//     * This endpoint is to create an account with only the name.
//     * @param userDTO
//     * @param secretPostUrl - this is an at which, the user was invited to the app.
//     * @return
//     */
//    @RequestMapping(method = RequestMethod.POST, value = "/anonym")
//    public UserDTO createAnonym(@RequestBody UserDTO userDTO) {
//        ExternalUserDTO externalUserDTO = externalAnonymAuthentication(secretPostUrl);
//
//        String username = userDTO.username;
//        username = username + "_" + System.currentTimeMillis() % 10000;
//        userDTO.username = username;
//        userDTO.email = username + "anonym@mail.com";
//        userDTO.profilePhotoURL = "https://s3-eu-west-1.amazonaws.com/dress-me-test1/ic_account_circle_grey600_48dp.png";//setting default profile photo
//
//        externalUserDTO.id = externalUserDTO.id + "_" + username;//TODO
//
//        validation.validate(userDTO);
//
//        ExternalAccount externalAccount = new ExternalAccount(externalUserDTO);
//
//        User user = new User(userDTO);
//        user.getExternalAccounts().add(externalAccount);
//        externalAccount.setAppUser(user);
//
//        user = userService.create(user, user);
//        UserDTO registeredUser = new UserDTO(user);
//        registeredUser.accessToken = registeredUser.getId().toString();
//
//        return registeredUser;
//    }
//
//    /**
//     * This endpoint exchanges the @secretPostUrl with the user of the system. The user's id == null iff he/she is not registered on the system, otherwise user's id != null.
//     *
//     * @param accessToken
//     * @return
//     */
//    @RequestMapping(method = RequestMethod.GET, value = "/exchangeToken")
//    public UserDTO exchangeToken(@RequestHeader(AUTHORIZATION_HEADER) String accessToken) {
//
//        Long userId = 0l;
//        try {
//            userId = Long.parseLong(accessToken);
//            User user = userService.get(userId);
//            if(user != null) {
//                return new UserDTO(user);//if user has registered only with his name through web
//            }
//        } catch (NumberFormatException e) {
//
//        }
//
//        ExternalUserDTO externalRequester = externalAuthentication(accessToken);
//
//        User userModel = new User(externalRequester);
//
//        User appUser = userService.findOnyByExternalAccountId(externalRequester.id, externalRequester.accountProvider);
//
//        UserDTO userDTO;
//        if (appUser != null) {
//            userDTO = new UserDTO(appUser);
//        } else {
//            userDTO = new UserDTO(userModel);//this user has no ID, because is not registered in the app yet.
//        }
//        return userDTO;
//    }

    @RequestMapping(value = "/findByName/{name}", method = RequestMethod.GET)
    public Page<UserDTO> findByName(@PathVariable("name") String name,
                                    Pageable pageable) {

        User requester = authenticateUser();

        Page<User> userPage = userService.findByNameLike(name, pageable);

        return mapModelPageToDTOPage(userPage, pageable);
    }

    @RequestMapping(value = "/findOneByName/{name}", method = RequestMethod.GET)
    public UserDTO findOneByName(@PathVariable("name") String name) {
        User requester = authenticateUser();
        User returnedUser = userService.findOneByUsername(name, requester);

        UserDTO returnedUserDTO = new UserDTO(returnedUser);
        return returnedUserDTO;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User get(@PathVariable("id") Long id) {
        User requester = authenticateUser();
        return userService.get(id, requester);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public UserDTO update(@RequestBody UserDTO user) {
        User requester = authenticateUser();
        validation.validateWithID(user);

        User userModel = new User(user);
        userModel = userService.update(userModel, requester);

        UserDTO updatedUserDTO = new UserDTO(userModel);

        return updatedUserDTO;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public UserDTO delete(@PathVariable("id") Long id) {
        User requester = authenticateUser();

        User deletedUser = userService.delete(id, requester);
        UserDTO userDTO = new UserDTO(deletedUser);

        return userDTO;
    }

    @Override
    protected UserDTO createDTOFromModel(User model) {
        return new UserDTO(model);
    }

    @Override
    protected User createModelFromDTO(UserDTO dto) {
        return new User(dto);
    }
}
