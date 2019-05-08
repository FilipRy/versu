package com.filip.versu.entity.dto;

import com.filip.versu.entity.dto.abs.AbsBaseEntityWithOwnerDTO;
import com.filip.versu.entity.model.PostFeedbackVote;


public class PostFeedbackVoteDTO extends AbsBaseEntityWithOwnerDTO<Long> {

    public PostFeedbackPossibilityDTO feedbackPossibilityDTO;

    public PostFeedbackVoteDTO() {
        super();
    }

    public PostFeedbackVoteDTO(PostFeedbackVote other, boolean createdByShopItem) {
        super(other);
        this.feedbackPossibilityDTO = new PostFeedbackPossibilityDTO(other.getPostFeedbackPossibility(), createdByShopItem);
    }
}
