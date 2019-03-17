
package com.filip.versu.controller.abs;


import com.filip.versu.entity.dto.abs.AbsBaseEntityDTO;
import com.filip.versu.entity.model.ExternalAccount;
import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.abs.AbsBaseEntity;
import com.filip.versu.entity.model.User;
import com.filip.versu.entity.validation.Validation;
import com.filip.versu.exception.AuthenticationException;
import com.filip.versu.security.ExternalUserDTO;
import com.filip.versu.security.IAuthenticator;
import com.filip.versu.service.PostService;
import com.filip.versu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;


public abstract class AbsAuthController<L, K extends AbsBaseEntity<L>, T extends AbsBaseEntityDTO<L>> {

    public static final String API_URL_PREFIX = "/api";

    public static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    protected UserService userService;

    @Autowired
    protected PostService postService;

    @Autowired
    protected Validation<L, T> validation;

    @Autowired
    protected IAuthenticator authenticator;

    /**
     * This validates the access token against external authorization server (facebook ...).
     *
     * @param accessToken
     * @return
     */
    protected ExternalUserDTO externalAuthentication(String accessToken) {
        ExternalUserDTO externalUser = authenticator.authenticate(accessToken);
        if (externalUser == null) {
            throw new AuthenticationException();
        }
        return externalUser;
    }

    /**
     * This validates the secretUrl used for a sharing a post, when inviting users to the app.
     * This method is used for registering user only with the name.
     *
     * @param secretPostUrl
     * @return
     */
    protected ExternalUserDTO externalAnonymAuthentication(String secretPostUrl) {
        Post post = postService.getPostBySecretUrl(secretPostUrl);

        if (post == null) {
            throw new AuthenticationException();
        }

        ExternalUserDTO externalUserDTO = new ExternalUserDTO();
        externalUserDTO.accountProvider = ExternalAccount.ExternalAccountProvider.ANONYM_NAME;
        externalUserDTO.id = secretPostUrl;

        return externalUserDTO;
    }


    /**
     * This validates the access token against external authorization server and retrieves corresponding user of this app.
     *
     * @param accessToken
     * @return
     */
    protected User authenticateUser(String accessToken) {
        ExternalUserDTO externalUser = externalAuthentication(accessToken);
        User user = userService.findOnyByExternalAccountId(externalUser.id, externalUser.accountProvider);
        if (user == null) {
            throw new AuthenticationException();
        }
        userService.uploadProfilePhoto(user);//TODO remove this, this is only to to copy facebook photos of current users to amazon s3

        user.setUserRole(User.UserRole.APP_USER);
        return user;
    }

    /**
     * Returns a user of the app based on the accessToken.
     *
     * @param accessToken
     * @return user who has registered with facebook or only with name.
     */
    protected User authenticateUserWithSecretLinkAccess(String accessToken, String secretUrl) {

        Long anonymUserAccessToken = 0l;
        User user = null;
        try {
            anonymUserAccessToken = Long.parseLong(accessToken);
            user = userService.get(anonymUserAccessToken);//if user has registered only with name at web client.
            if (user == null) {
                user = authenticateUser(accessToken);//if user has registered with facebook
            } else {
                ExternalAccount externalAccount = user.getExternalAccounts().get(0);//now all account has only 1 external account.
                if (externalAccount.getProvider() != ExternalAccount.ExternalAccountProvider.ANONYM_NAME) {
                    throw new AuthenticationException();
                }
            }
        } catch (NumberFormatException e) {
            user = authenticateUser(accessToken);//if user has registered with facebook
        }

        user.setSecretUrl(secretUrl);
        user.setUserRole(User.UserRole.USER_WITH_LINK);

        return user;
    }


    /**
     * This is used only at requesting post with secret link.
     *
     * @param secretUrl
     * @return
     */
    protected User authenticateUserBySecretPostUrl(String secretUrl) {
        User user = new User();
        user.setId(-1l);//initialize with some invalid id here
        user.setUserRole(User.UserRole.USER_WITH_LINK);
        user.setSecretUrl(secretUrl);

        return user;
    }

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
