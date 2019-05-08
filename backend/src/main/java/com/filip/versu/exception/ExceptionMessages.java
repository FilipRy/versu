package com.filip.versu.exception;

/**
 * This class contains a list of Exception messages, which are being sent to the
 * client.
 * <p/>
 * This convention need to be followed at creating new Message: message = ""
 * while (STRING IS NOT REACHED) message = message "_" className go one level
 * below in class hierarchie
 * <p/>
 * message = message "_" STRING
 *
 * @author Filip
 */
public class ExceptionMessages {

    public static class AuthException {
        public static class UnAuthenticated {
            /**
             * User's access token is invalid.
             */
            public static final String USER = "AuthException_UnAuthenticated_USER";
        }

    }


    public static class EntityNotExistsException {
        public static final String ENTITY_UPDATE = "EntityNotExistsException_ENTITY_UPDATE";
        public static final String USER = "EntityNotExistsException_USER";
        public static final String SHOPPING_ITEM = "EntityNotExistsException_SHOPPING_ITEM";
        public static final String SHOPPING_ITEM_POSSIBILITY = "EntityNotExistsException_SHOPPING_ITEM_POSSIBILITY";
        public static final String PHOTO = "EntityNotExistsException_PHOTO";
    }

    public static class FeedbackActionExistsException {
        public static final String FEEDBACK_ACTION = "FeedbackActionExistsException_FEEDBACK_ACTION";
    }

    public static class EntityExistsException {
        public static final String OBJECT_EXISTS = "EntityExistsException_OBJECT_EXISTS";
        public static final String USERNAME_TAKEN = "EntityExistsException_USERNAME_TAKEN";
        public static final String EMAIL_TAKEN = "EntityExistsException_EMAIL_TAKEN";
    }

    public static class ForbiddenException {
        public static final String SHOPPING_ITEM = "EntityBadStateException_SHOPPING_ITEM";
        public static final String POST_FEEDBACK = "EntityBadStateException_POST_FEEDBACK";
        public static final String USER = "EntityBadStateException_USER";
        public static final String FOLLOWING = "EntityBadStateException_FOLLOWING";
        public static final String VOTE_NO = "ForbiddenException_VOTE_NO";
        public static final String FEEDBACK_ACTION = "ForbiddenException_FEEDBACK_ACTION";
    }

    public static class UnauthorizedException {
        public static final String UNAUTHORIZED = "UnauthorizedException_UNAUTHORIZED";
    }

    public static class ParameterException {
        public static final String PARAM_MISSING = "ParameterException_PARAM_MISSING";
        public static final String ID_MISSING = "ParameterException_ID_MISSING";

        public static final String FEEDBACK_ACTION_SHOPPING_ITEM = "ParameterException_FEEDBACK_ACTION_SHOPPING_ITEM";
        public static final String FEEDBACK_ACTION_PHOTO = "ParameterException_FEEDBACK_ACTION_PHOTO";

        public static final String SHOPPING_ITEM_ACCESS_TYPE = "ParameterException_SHOPPING_ITEM_ACCESS_TYPE";
        public static final String SHOPPING_ITEM_PHOTO = "ParameterException_SHOPPING_ITEM_PHOTO";
        public static final String SHOPPING_ITEM_TIMER = "ParameterException_SHOPPING_ITEM_TIMER";
        public static final String SHOPPING_ITEM_DESC = "ParameterException_SHOPPING_ITEM_DESC";
        public static final String POST_FEEDBACK_POSSIBILITIES = "ParameterException_POST_FEEDBACK_POSSIBILITIES";

        public static final String COMMENT_CONTENT = "ParameterException_COMMENT_CONTENT";

        public static final String DEVICE_INFO_MISSING = "ParameterException_DEVICE_INFO_MISSING";
        public static final String DEVICE_INFO_REGISTRATION_ID = "ParameterException_DEVICE_INFO_REGISTRATION_ID";
    }


    public static class ParameterAException {
        public static final String SIMPLE_POST_DESC_MISSING = "ParameterException_SIMPLE_POST_DESC_MISSING";
        public static final String SIMPLE_POST_PHOTOS_MISSING = "ParameterException_SIMPLE_POST_PHOTOS_MISSING";
        public static final String SIMPLE_POST_PHOTOS_MAX = "ParameterException_SIMPLE_POST_PHOTOS_MAX";
        public static final String SIMPLE_POST_NULL = "ParameterException_SIMPLE_POST_NULL";
        public static final String SIMPLE_POST_POSTED_BY_MISSING = "ParameterException_SIMPLE_POST_POSTED_BY_MISSING";
        public static final String SIMPLE_POST_GCS_URL_CONTENT_TYPE_MISSING = "ParameterException_SIMPLE_POST_GCS_URL_CONTENT_TYPE_MISSING";
        public static final String SIMPLE_POST_GCS_URL_OBJECT_NAME_MISSING = "ParameterException_SIMPLE_POST_GCS_URL_OBJECT_NAME_MISSING";

        public static final String ID_NOT_VALID = "ParameterException_ID_NOT_VALID";
        public static final String LAST_LOADED_ID_NOT_VALID = "ParameterException_LAST_LOADED_ID_NOT_VALID";
        /**
         * This is a description of an exception thrown by
         * listNew[Likes/Dislikes/Comment]OfPhoto.
         */
        public static final String INTEGER_NOT_VALID = "ParameterException_INTEGER_NOT_VALID";
        public static final String STRING_NOT_VALID = "ParameterException_STRING_NOT_VALID";

        public static final String COMMENT_NULL = "ParameterException_COMMENT_NULL";
        public static final String COMMENT_OWNER_MISSING = "ParameterException_COMMENT_OWNER_MISSING";
        public static final String COMMENT_PHOTO_MISSING = "ParameterException_COMMENT_PHOTO_MISSING";

        public static final String DEVICEINFO_NULL = "ParameterException_DEVICEINFO_NULL";
        public static final String DEVICEINFO_OWNER_MISSING = "ParameterException_DEVICEINFO_OWNER_MISSING";

        public static final String DISLIKES_NULL = "ParameterException_DISLIKES_NULL";
        public static final String DISLIKES_OWNER_MISSING = "ParameterException_DISLIKES_OWNER_MISSING";
        public static final String DISLIKES_PHOTO_MISSING = "ParameterException_DISLIKES_PHOTO_MISSING";

        public static final String FRIENDREQUEST_NULL = "ParameterException_FRIENDREQUEST_NULL";
        public static final String FRIENDREQUEST_USER_MISSING = "ParameterException_FRIENDREQUEST_USER_MISSING";

        public static final String FRIENDSHIP_NULL = "ParameterException_FRIENDSHIP_NULL";
        public static final String FRIENDSHIP_USER_MISSING = "ParameterException_FRIENDSHIP_USER_MISSING";

        public static final String LIKES_NULL = "ParameterException_LIKES_NULL";
        public static final String LIKES_OWNER_MISSING = "ParameterException_LIKES_OWNER_MISSING";
        public static final String LIKES_PHOTO_MISSING = "ParameterException_LIKES_PHOTO_MISSING";

        public static final String USER_NULL = "ParameterException_USER_NULL";

    }

    public static class GCSIOException {
        public static final String CANNOT_REMOVE_PHOTO = "GCSIOException_CANNOT_REMOVE_PHOTO";
    }

    public static final String DISLIKE_CREATE_MULTIPLE = "DISLIKE_CREATE_MULTIPLE";
    public static final String DISLIKE_CREATE_VOTING_POST = "DISLIKE_CREATE_VOTING_POST";
    public static final String DISLIKE_CREATE_LIKED_POST = "DISLIKE_CREATE_LIKED_POST";

    public static final String FRIENDREQUEST_CREATE_MULTIPLE = "FRIENDREQUEST_CREATE_MULTIPLE";
    public static final String FRIENDSHIP_CREATE_MULTIPLE = "FRIENDSHIP_CREATE_MULTIPLE";
    /**
     * User wants to send friendrequest to his friend.
     */
    public static final String FRIENDREQUEST_CREATE_FRIENDS = "FRIENDREQUEST_CREATE_FRIENDS";

    public static final String LIKE_CREATE_MULTIPLE = "LIKE_CREATE_MULTIPLE";
    public static final String LIKE_CREATE_DISLIKED_POST = "LIKE_CREATE_DISLIKED_POST";

}
