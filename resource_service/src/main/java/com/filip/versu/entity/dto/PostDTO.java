package com.filip.versu.entity.dto;


import com.filip.versu.entity.dto.abs.AbsBaseEntityWithOwnerDTO;
import com.filip.versu.entity.model.*;

import java.util.ArrayList;
import java.util.List;

public class PostDTO extends AbsBaseEntityWithOwnerDTO<Long> {

    public String description;

    /**
     * The UTC time when the shopping item was published.
     */
    public long publishTime;

    /**
     * Timer represents how long the shopping item remains visible.
     */
    public Post.Timer timer;

    public GoogleLocationDTO location;

    public List<PostPhotoDTO> photos;

    public Post.AccessType accessType;

    public List<UserDTO> viewers;

    public PostFeedbackVoteDTO myPostFeedback;

    /**
     * This is a list of the last two comments given on the shopping item.
     */
    public List<CommentDTO> comments = new ArrayList<>();


    /**
     * Represents all possible voting answers for a post.
     */
    public List<PostFeedbackPossibilityDTO> postFeedbackPossibilities = new ArrayList<>();

    public PostFeedbackPossibilityDTO chosenFeedbackPossibility;

    public String secretUrl;

    public PostDTO() {
        super();
    }

    public PostDTO(Post other) {
        super(other);
        this.description = other.getDescription();
        this.publishTime = other.getPublishTime();
        this.timer = other.getTimer();
        this.secretUrl = other.getSecretUrl();
        if(other.getLocation() != null) {
            this.location = new GoogleLocationDTO(other.getLocation());
        }
        this.photos = new ArrayList<>();

        for(PostFeedbackPossibility postFeedbackPossibility: other.getPostFeedbackPossibilities()) {
            PostFeedbackPossibilityDTO postFeedbackPossibilityDTO = new PostFeedbackPossibilityDTO(postFeedbackPossibility, true);
            this.postFeedbackPossibilities.add(postFeedbackPossibilityDTO);
        }

        if(other.getChosenFeedbackPossibility() != null) {
            this.chosenFeedbackPossibility = new PostFeedbackPossibilityDTO(other.getChosenFeedbackPossibility(), true);
        }

        if(other.getPhotos() != null) {
            for(PostPhoto photo: other.getPhotos()) {
                PostPhotoDTO postPhotoDTO = new PostPhotoDTO(photo, true);
                //postPhotoDTO.post = this;//setting reference to this postDTO (to avoid stackoverflow in PostDTO -> PostPhotoDTO -> PostDTO ...)
                //does not setting reference here to avoid cyclic dependency
                photos.add(postPhotoDTO);
            }
        }
        this.accessType = other.getAccessType();
        this.viewers = new ArrayList<>();
        if(other.getViewers() != null) {
            for(User user: other.getViewers()) {
                viewers.add(new UserDTO(user));
            }
        }

        if(other.getMostRecentComment() != null) {
            for (Comment comment: other.getMostRecentComment()) {
                CommentDTO commentDTO = new CommentDTO(comment, true);
                this.comments.add(commentDTO);
                //does not setting backreference to shopping item to avoid cyclic dependency
            }
        }

        if(other.getMyPostFeedbackVote() != null) {
            myPostFeedback = new PostFeedbackVoteDTO(other.getMyPostFeedbackVote(), true);
            //myPostFeedback.postDTO = this;
            //does not setting reference here to avoid cyclic dependency
        }

    }
}
