package com.filip.versu.entity.dto;

import com.filip.versu.entity.dto.abs.AbsBaseEntityDTO;
import com.filip.versu.entity.model.User;


public class UserDTO extends AbsBaseEntityDTO<Long> {


    /**
     * The UTC time, when this user was registered
     */
    public long registrationTime;

    public String username;
    public String email;
    public String password;

    public String profilePhotoURL;

    public String quote;

    public GoogleLocationDTO location;

    public String accessToken;//this is an access token used for user registered with name through web.

    public UserDTO() {
        super();
    }

    //this constructor ignores password, because I cannot send the client passwords to the client
    public UserDTO(User other) {
        super(other);
        this.registrationTime = other.getRegistrationTime();
        this.username = other.getUsername();
        this.email = other.getEmail();
        this.profilePhotoURL = other.getProfilePhotoURL();
        this.quote = other.getQuote();
        if(other.getLocation() != null) {
            location = new GoogleLocationDTO(other.getLocation());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDTO userDTO = (UserDTO) o;

        if (registrationTime != userDTO.registrationTime) return false;
        if (username != null ? !username.equals(userDTO.username) : userDTO.username != null) return false;
        if (email != null ? !email.equals(userDTO.email) : userDTO.email != null) return false;
        if (password != null ? !password.equals(userDTO.password) : userDTO.password != null) return false;

        return profilePhotoURL != null ? profilePhotoURL.equals(userDTO.profilePhotoURL) : userDTO.profilePhotoURL == null;

    }

}
