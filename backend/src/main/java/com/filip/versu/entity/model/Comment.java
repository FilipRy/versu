package com.filip.versu.entity.model;


import com.filip.versu.entity.dto.CommentDTO;
import com.filip.versu.entity.model.abs.AbsBaseEntityWithOwner;
import com.filip.versu.repository.DBHelper;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = DBHelper.TablesNames.COMMENT)
@Where(clause = "is_deleted=0")
public class Comment extends AbsBaseEntityWithOwner<Long> {

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter
    @Setter
    private Post post;

    @Getter
    @Setter
    private String content;

    @Getter
    @Setter
    private long timestamp;

    @Getter
    @Setter
    private boolean isDeleted;

    public Comment() {
        super();
    }


    public Comment(CommentDTO other) {
        super(other);
        this.content = other.content;
        this.post = new Post(other.postDTO);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Comment comment = (Comment) o;

        return !(content != null ? !content.equals(comment.content) : comment.content != null);

    }

}
