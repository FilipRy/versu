package com.filip.versu.entity.model;

import com.filip.versu.entity.dto.PostPhotoDTO;
import com.filip.versu.entity.model.abs.AbsBaseEntity;
import com.filip.versu.repository.DBHelper;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = DBHelper.TablesNames.POST_PHOTO)
@Where(clause = "is_deleted=0")
public class PostPhoto extends AbsBaseEntity<Long> {

    @Getter
    @Setter
    private String path;
    /**
     * This is the time, when the PostPhoto was created.
     * UTC time.
     */
    @Getter
    @Setter
    private long takenTime;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    @ManyToOne
    private Post post;

    @Getter
    @Setter
    private boolean isDeleted;

    public PostPhoto() {

    }

    public PostPhoto(String path, long takenTime) {
        this.path = path;
        this.takenTime = takenTime;
    }

    //this constructor ignores: comments, votes, favourites. because no client sends the shopping item with comments, votes, favourites to backend.
    //The post is set by PostDTO to avoid stackoverflow in constructor.
    public PostPhoto(PostPhotoDTO photo) {
        super(photo);
        this.path = photo.path;
        this.takenTime = photo.takenTime;
        this.description = photo.description;

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PostPhoto photo = (PostPhoto) o;

        if (takenTime != photo.takenTime) return false;
        if (isDeleted != photo.isDeleted) return false;
        if (path != null ? !path.equals(photo.path) : photo.path != null) return false;
        return !(description != null ? !description.equals(photo.description) : photo.description != null);

    }
}
