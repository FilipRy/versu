package com.filip.versu.entity.dto;

import com.filip.versu.entity.dto.abs.AbsBaseEntityDTO;
import com.filip.versu.entity.model.PostFeedbackPossibility;


public class PostFeedbackPossibilityDTO extends AbsBaseEntityDTO<Long> {

    public String name;

    public Integer count;


    public PostDTO postDTO;

    public PostFeedbackPossibilityDTO() {

    }

    public PostFeedbackPossibilityDTO(PostFeedbackPossibility postFeedbackPossibility, boolean createdByPost) {
        super(postFeedbackPossibility);
        this.name = postFeedbackPossibility.getName();
        this.count = postFeedbackPossibility.getCount();
        if(!createdByPost) {
            this.postDTO = new PostDTO(postFeedbackPossibility.getPost());
        }

    }
}
