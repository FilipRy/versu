package com.filip.versu.entity.dto;


import com.filip.versu.entity.dto.abs.AbsFeedbackPostDTO;
import com.filip.versu.entity.model.Comment;

public class CommentDTO extends AbsFeedbackPostDTO {

    public String content;

    public CommentDTO() {
        super();
    }

    public CommentDTO(Comment other, boolean createdByShopItem) {
        super(other, createdByShopItem);
        this.content = other.getContent();
    }

}
