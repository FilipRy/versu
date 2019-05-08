package com.filip.versu.entity.model;


import com.filip.versu.entity.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.entity.model.abs.AbsBaseEntity;
import com.filip.versu.repository.DBHelper;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = DBHelper.TablesNames.POST_FEEDBACK_POSSIBILITY)
@Where(clause = "is_deleted=0")
public class PostFeedbackPossibility extends AbsBaseEntity<Long> {

    /**
     * Name of the possibility
     */
    @Getter
    @Setter
    private String name;


    /**
     * How many people voted for this possibility
     */
    @Transient
    @Getter
    @Setter
    private Integer count;

    @Getter
    @Setter
    @ManyToOne
    private Post post;

    @Getter
    @Setter
    @OneToMany(mappedBy = "postFeedbackPossibility", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PostFeedbackVote> postFeedbackVoteList = new ArrayList<>();

    @Getter
    @Setter
    private boolean isDeleted;

    public PostFeedbackPossibility() {
        super();
    }

    public PostFeedbackPossibility(PostFeedbackPossibilityDTO other, boolean calledFromPost) {
        super(other);
        this.name = other.name;
        this.count = other.count;
        if (!calledFromPost) {
            this.post = new Post(other.postDTO);
        }
    }


/**
 * equals - post feedback possibilities are compared only at id, not need to override equals method
 */


}
