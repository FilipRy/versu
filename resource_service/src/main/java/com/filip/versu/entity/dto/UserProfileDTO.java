package com.filip.versu.entity.dto;


import org.springframework.data.domain.Page;

import java.io.Serializable;

public class UserProfileDTO implements Serializable {

    public Page<PostDTO> userShoppingItems;

    public UserCardDTO userCard;

    public static class UserCardDTO {
        /**
         * The user, who this card is representing
         */
        public UserDTO userDTO;

        /**
         * following information about @userDTO
         */
        public int followersCount;
        public int followingsCount;

        /**
         * The following between "me" and @userDTO, can be null (if there is no such following).
         */
        public FollowingDTO followingDTO;
    }

}
