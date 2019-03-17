package com.filip.versu.entity.dto.abs;

import com.filip.versu.entity.dto.PostDTO;
import com.filip.versu.entity.model.abs.AbsFeedbackPost;


public abstract class AbsFeedbackPostDTO extends AbsBaseEntityWithOwnerDTO<Long> {

    public PostDTO postDTO;
    public long timestamp;

    public AbsFeedbackPostDTO() {
        super();
    }

    public AbsFeedbackPostDTO(AbsFeedbackPost other, boolean createdByShopItem) {
        super(other);
        if(!createdByShopItem) {
            this.postDTO = new PostDTO(other.getPost());
        }
        this.timestamp = other.getTimestamp();
    }

}
