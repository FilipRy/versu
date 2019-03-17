package com.filip.versu.security;


public interface IAuthenticator {

    public ExternalUserDTO authenticate(String accessToken);

    /**
     *
     * @param accessToken - access token of a user provided by e.g. facebook
     * @return
     */
    public ExternalUserDTO fetchUserData(String accessToken);

    public String retrieveProfilePhotoURL(String accessToken);

}
