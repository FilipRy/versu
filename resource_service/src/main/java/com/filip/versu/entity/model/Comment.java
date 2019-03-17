package com.filip.versu.entity.model;


import com.filip.versu.entity.dto.CommentDTO;
import com.filip.versu.entity.model.abs.AbsFeedbackPost;
import com.filip.versu.repository.DBHelper;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = DBHelper.TablesNames.COMMENT)
@Where(clause = "is_deleted=0")
public class Comment extends AbsFeedbackPost {

    @Getter
    @Setter
    private String content;

    public Comment() {
        super();
    }


    public Comment(CommentDTO other) {
        super(other);
        this.content = other.content;
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
