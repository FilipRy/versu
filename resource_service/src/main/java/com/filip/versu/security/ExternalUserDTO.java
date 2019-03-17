package com.filip.versu.security;

import com.filip.versu.entity.model.ExternalAccount;
import com.restfb.types.User;

/**
 * This class represents an external user, who does not need to be registered in the app.
 */
public class ExternalUserDTO {

    public String id;

    public String username;
    public String lastname;
    public String firstname;
    public String email;

    public String photoURL;

    public ExternalAccount.ExternalAccountProvider accountProvider;

    public ExternalUserDTO() {

    }

    public ExternalUserDTO(User facebookUser) {
        this.id = facebookUser.getId();
        this.username = facebookUser.getName();
        this.email = facebookUser.getEmail();
        this.lastname = facebookUser.getLastName();
        this.firstname = facebookUser.getFirstName();
        accountProvider = ExternalAccount.ExternalAccountProvider.FACEBOOK;
    }

    public static ExternalUserDTO createForAnonymUsername(String username) {
        ExternalUserDTO externalUserDTO = new ExternalUserDTO();
        externalUserDTO.username = username;
        externalUserDTO.accountProvider = ExternalAccount.ExternalAccountProvider.ANONYM_NAME;
        return  externalUserDTO;
    }


}
