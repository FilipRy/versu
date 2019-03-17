package com.filip.versu.entity.model.abs;


import com.filip.versu.entity.dto.abs.AbsFeedbackPostDTO;
import com.filip.versu.entity.model.Post;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * This is a abstract super class for feedback action on post (comment, favourite).
 */
@MappedSuperclass
public abstract class AbsFeedbackPost extends AbsBaseEntityWithOwner<Long> {

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter
    @Setter
    private Post post;

    @Getter
    @Setter
    private long timestamp;

    @Getter
    @Setter
    private boolean isDeleted;

    public AbsFeedbackPost() {
        super();
    }

    public AbsFeedbackPost(AbsFeedbackPostDTO feedbackShopItemDTO) {
        super(feedbackShopItemDTO);
        this.post = new Post(feedbackShopItemDTO.postDTO);
        this.timestamp = feedbackShopItemDTO.timestamp;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;

        AbsFeedbackPost that = (AbsFeedbackPost) object;

        if (timestamp != that.timestamp) return false;
        if (isDeleted != that.isDeleted) return false;
        return !(post != null ? !post.equals(that.post) : that.post != null);

    }

}
