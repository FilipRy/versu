package com.filip.versu.entity.dto;


import com.filip.versu.entity.dto.abs.AbsBaseEntityWithOwnerDTO;
import com.filip.versu.entity.model.Comment;

public class CommentDTO extends AbsBaseEntityWithOwnerDTO<Long> {

    public String content;
    public PostDTO postDTO;
    public long timestamp;

    public CommentDTO() {
        super();
    }

    public CommentDTO(Comment other, boolean createdByPost) {
        super(other);
        if(!createdByPost) {
            this.postDTO = new PostDTO(other.getPost());
        }
        this.timestamp = other.getTimestamp();
        this.content = other.getContent();
    }

}
