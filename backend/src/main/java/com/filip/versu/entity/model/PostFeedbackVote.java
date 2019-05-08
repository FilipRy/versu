package com.filip.versu.entity.model;

import com.filip.versu.entity.dto.PostFeedbackVoteDTO;
import com.filip.versu.entity.model.abs.AbsBaseEntityWithOwner;
import com.filip.versu.repository.DBHelper;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This entity represents an answer of a voting defined by post owner
 */
@Entity
@Table(name = DBHelper.TablesNames.POST_FEEDBACK_VOTE)
@Where(clause = "is_deleted=0")
public class PostFeedbackVote extends AbsBaseEntityWithOwner<Long> {

    @Getter
    @Setter
    @ManyToOne
    private PostFeedbackPossibility postFeedbackPossibility;

    @Getter
    @Setter
    private long timestamp;

    @Getter
    @Setter
    private boolean isDeleted;

    public PostFeedbackVote() {
        super();
    }

    public PostFeedbackVote(PostFeedbackVoteDTO voteAnswer) {
        super(voteAnswer);
        this.postFeedbackPossibility = new PostFeedbackPossibility(voteAnswer.feedbackPossibilityDTO, false);
    }



}
