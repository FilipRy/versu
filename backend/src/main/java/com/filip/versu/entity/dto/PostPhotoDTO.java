package com.filip.versu.entity.dto;


import com.filip.versu.entity.dto.abs.AbsBaseEntityDTO;
import com.filip.versu.entity.model.PostPhoto;

public class PostPhotoDTO extends AbsBaseEntityDTO<Long> {


    public String path;
    /**
     * This is the time, when the PostPhoto was created.
     * UTC time.
     */
    public long takenTime;
    public String description;

    public PostDTO post;

    public PostPhotoDTO() {

    }

    //this constructor ignores: comments, votes, favourites. because backend does not send shopping item with votes(myVoteYes, myVoteNo)...
    //The post is set by PostDTO to avoid stackoverflow in constructor.
    public PostPhotoDTO(PostPhoto photo, boolean createdByShoppingItem) {
        super(photo);
        this.path = photo.getPath();
        this.takenTime = photo.getTakenTime();
        this.description = photo.getDescription();
        if(!createdByShoppingItem) {
            this.post = new PostDTO(photo.getPost());
        }

    }



}
