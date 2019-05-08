package com.filip.versu.entity.model.abs;

import com.filip.versu.entity.dto.abs.AbsBaseEntityWithOwnerDTO;
import com.filip.versu.entity.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
public abstract class AbsBaseEntityWithOwner<K> extends AbsBaseEntity<K> {

    @ManyToOne
    @Getter
    @Setter
    private User owner;

    public AbsBaseEntityWithOwner() {
    }

    public AbsBaseEntityWithOwner(AbsBaseEntityWithOwnerDTO<K> other) {
        super(other);
        this.owner = new User(other.owner);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AbsBaseEntityWithOwner<?> that = (AbsBaseEntityWithOwner<?>) o;

        return !(owner != null ? !owner.equals(that.owner) : that.owner != null);

    }
}
