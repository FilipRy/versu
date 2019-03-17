package com.filip.versu.entity.model.abs;


import com.filip.versu.entity.dto.abs.AbsFeedbackPostPhotoDTO;
import com.filip.versu.entity.model.PostPhoto;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbsFeedbackPostPhoto extends AbsBaseEntityWithOwner<Long> {

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter
    @Setter
    private PostPhoto photo;

    @Getter
    @Setter
    private long timestamp;

    @Getter
    @Setter
    private boolean isDeleted;

    public AbsFeedbackPostPhoto() {
        super();
    }

    public AbsFeedbackPostPhoto(AbsFeedbackPostPhotoDTO other) {
        super(other);
        this.photo = new PostPhoto(other.photo);
        this.timestamp = other.timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AbsFeedbackPostPhoto that = (AbsFeedbackPostPhoto) o;

        if (timestamp != that.timestamp) return false;
        if (isDeleted != that.isDeleted) return false;
        return !(photo != null ? !photo.equals(that.photo) : that.photo != null);

    }
}
