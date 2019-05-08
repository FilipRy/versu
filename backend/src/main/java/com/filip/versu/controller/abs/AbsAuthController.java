
package com.filip.versu.controller.abs;


import com.filip.versu.entity.dto.abs.AbsBaseEntityDTO;
import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.abs.AbsBaseEntity;
import com.filip.versu.entity.model.User;
import com.filip.versu.entity.validation.Validation;
import com.filip.versu.exception.AuthenticationException;
import com.filip.versu.service.PostService;
import com.filip.versu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;


public abstract class AbsAuthController<L, K extends AbsBaseEntity<L>, T extends AbsBaseEntityDTO<L>> {

    public static final String API_URL_PREFIX = "/api/v1";

    @Autowired
    protected UserService userService;

    @Autowired
    protected PostService postService;

    @Autowired
    protected Validation<L, T> validation;

//    /**
//     * This validates the access token against external authorization server (facebook ...).
//     *
//     * @param accessToken
//     * @return
//     */
//    protected ExternalUserDTO externalAuthentication(String accessToken) {
//        ExternalUserDTO externalUser = authenticator.authenticate(accessToken);
//        if (externalUser == null) {
//            throw new AuthenticationException();
//        }
//        return externalUser;
//    }
//
//    /**
//     * This validates the secretUrl used for a sharing a post, when inviting users to the app.
//     * This method is used for registering user only with the name.
//     *
//     * @param secretPostUrl
//     * @return
//     */
//    protected ExternalUserDTO externalAnonymAuthentication(String secretPostUrl) {
//        Post post = postService.getPostBySecretUrl(secretPostUrl);
//
//        if (post == null) {
//            throw new AuthenticationException();
//        }
//
//        ExternalUserDTO externalUserDTO = new ExternalUserDTO();
//        externalUserDTO.accountProvider = ExternalAccount.ExternalAccountProvider.ANONYM_NAME;
//        externalUserDTO.id = secretPostUrl;
//
//        return externalUserDTO;
//    }



    protected User authenticateUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = this.userService.findOneByUsername(authentication.getName());
        if (user == null) {
            throw new AuthenticationException();
        }
//        user.setUserRole(User.UserRole.APP_USER);


        return user;
    }

//    /**
//     * Returns a user of the app based on the accessToken.
//     *
//     * @param accessToken
//     * @return user who has registered with facebook or only with name.
//     */
//    protected User authenticateUserWithSecretLinkAccess(String accessToken, String secretUrl) {
//
//        Long anonymUserAccessToken = 0l;
//        User user = null;
//        try {
//            anonymUserAccessToken = Long.parseLong(accessToken);
//            user = userService.get(anonymUserAccessToken);//if user has registered only with name at web client.
//            if (user == null) {
//                user = authenticateUser(accessToken);//if user has registered with facebook
//            } else {
//                ExternalAccount externalAccount = user.getExternalAccounts().get(0);//now all account has only 1 external account.
//                if (externalAccount.getProvider() != ExternalAccount.ExternalAccountProvider.ANONYM_NAME) {
//                    throw new AuthenticationException();
//                }
//            }
//        } catch (NumberFormatException e) {
//            user = authenticateUser(accessToken);//if user has registered with facebook
//        }
//
//        user.setSecretUrl(secretUrl);
//        user.setUserRole(User.UserRole.USER_WITH_LINK);
//
//        return user;
//    }


//    /**
//     * This is used only at requesting post with secret link.
//     *
//     * @param secretUrl
//     * @return
//     */
//    protected User authenticateUserBySecretPostUrl(String secretUrl) {
//        User user = new User();
//        user.setId(-1l);//initialize with some invalid id here
//        user.setUserRole(User.UserRole.USER_WITH_LINK);
//        user.setSecretUrl(secretUrl);
//
//        return user;
//    }

    protected Page<T> mapModelPageToDTOPage(Page<K> modelPage, Pageable pageable) {

        List<K> modelList = modelPage.getContent();
        List<T> dtoList = new ArrayList<>();

        if (modelList != null) {
            for (K model : modelList) {
                dtoList.add(createDTOFromModel(model));
            }
        }

        Page<T> dtoPage = new PageImpl<T>(dtoList, pageable, modelPage.getTotalElements());

        return dtoPage;
    }

    protected abstract T createDTOFromModel(K model);

    protected abstract K createModelFromDTO(T dto);

}
