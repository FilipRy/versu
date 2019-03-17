package com.filip.versu.security;



import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonException;
import com.restfb.json.JsonObject;
import com.restfb.types.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FacebookConnector implements IAuthenticator {

    private String facebookAppId;
    private String facebookAppSecret;

    private FacebookClient.AccessToken appAccessToken;


    @Autowired
    public FacebookConnector(Environment environment) {
        facebookAppId = environment.getRequiredProperty("facebook.app-id");
        facebookAppSecret = environment.getRequiredProperty("facebook.app-secret");
        appAccessToken = new DefaultFacebookClient(Version.VERSION_2_6).obtainAppAccessToken(facebookAppId, facebookAppSecret);
    }

    @Override
    public ExternalUserDTO authenticate(String accessToken) {
        if (accessToken == null) {
            return null;
        }
        try {
            /**
             * Debugging accessToken to protect against Token hijacking
             */
            FacebookClient.DebugTokenInfo debugTokenInfo = new DefaultFacebookClient(appAccessToken.getAccessToken(),
                    Version.VERSION_2_4).debugToken(accessToken);

            if (debugTokenInfo != null) {
                if (debugTokenInfo.getAppId() != null) {
                    if (!debugTokenInfo.getAppId().equals(facebookAppId)) {
                        return null;
                    }
                } else {
                    return null;
                }
                if (debugTokenInfo.isValid() != null) {
                    if (!debugTokenInfo.isValid()) {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
            return fetchUserData(accessToken);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ExternalUserDTO fetchUserData(String userAccessToken) {
        FacebookClient facebookClient = new DefaultFacebookClient(userAccessToken, facebookAppSecret, Version.VERSION_2_6);
        User user = facebookClient.fetchObject("me", User.class, Parameter.with("fields", "email, name"));

        ExternalUserDTO externalUser = new ExternalUserDTO(user);
        return externalUser;
    }

    /**
     * Precondition: authenticate method must be called before this method.
     *
     * @param accessToken
     * @return
     */
    @Override
    public String retrieveProfilePhotoURL(String accessToken) {
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken, facebookAppSecret, Version.VERSION_2_6);
        JsonObject picture = facebookClient.fetchObject("me/picture", JsonObject.class, Parameter.with("redirect", "false"), Parameter.with("type", "large"));
        JsonObject dataObject = (JsonObject) picture.get("data");
        return dataObject.getString("url");
    }

    public List<ExternalUserDTO> retrieveAppExternalUsers(int count) {
        FacebookClient facebookClient = new DefaultFacebookClient(appAccessToken.getAccessToken(), facebookAppSecret, Version.VERSION_2_6);
        JsonObject json = facebookClient.fetchObject(facebookAppId + "/accounts/test-users", JsonObject.class);

        List<ExternalUserDTO> externalUsers = new ArrayList<>();
        JsonArray jsonArray = null;
        try {
            jsonArray = new JsonArray(json.getString("data"));
            int size = Math.min(jsonArray.length(), count);
            for (int i = 0; i < size; i++) {
                JsonObject jsonObject = jsonArray.getJsonObject(i);
                String userAccessToken = jsonObject.getString("access_token");
                ExternalUserDTO externalUser = fetchUserData(userAccessToken);
                externalUsers.add(externalUser);
            }

        } catch (JsonException e) {
            e.printStackTrace();
        }
        return externalUsers;
    }


}
