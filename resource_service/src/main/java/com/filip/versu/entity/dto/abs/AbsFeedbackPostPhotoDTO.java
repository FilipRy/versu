package com.filip.versu.entity.dto.abs;


import com.filip.versu.entity.dto.PostPhotoDTO;
import com.filip.versu.entity.model.abs.AbsFeedbackPostPhoto;

public abstract class AbsFeedbackPostPhotoDTO extends AbsBaseEntityWithOwnerDTO<Long> {

    public PostPhotoDTO photo;
    public long timestamp;


    /**
     *
     * @param other
     * @param createdByPhoto == true iff this constructor is invoked by photoDTO constructor, then I am not creating photo here to avoid stack overflow.
     */
    public AbsFeedbackPostPhotoDTO(AbsFeedbackPostPhoto other, boolean createdByPhoto) {
        super(other);
        if(!createdByPhoto) {
            this.photo = new PostPhotoDTO(other.getPhoto(), false);
        }
        this.timestamp = other.getTimestamp();
    }

    public AbsFeedbackPostPhotoDTO() {
        super();
    }
}
