package com.filip.versu.entity.model;

import com.filip.versu.entity.dto.FollowingDTO;
import com.filip.versu.entity.model.abs.AbsBaseEntity;
import com.filip.versu.repository.DBHelper;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = DBHelper.TablesNames.FOLLOWING)
public class Following extends AbsBaseEntity<Long> {

    /**
     * This user is the creator of following (follower).
     */
    @ManyToOne
    private User creator;

    /**
     * This user is the target of following.
     */
    @ManyToOne
    private User target;

    /**
     * The UTC time
     */
    private long createTime;


    public Following() {

    }

    public Following(FollowingDTO other) {
        super(other);
        this.creator = new User(other.creator);
        this.target = new User(other.target);
        this.createTime = other.createTime;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Following following = (Following) o;

        if (createTime != following.createTime) return false;
        if (creator != null ? !creator.equals(following.creator) : following.creator != null) return false;
        return !(target != null ? !target.equals(following.target) : following.target != null);

    }

}
