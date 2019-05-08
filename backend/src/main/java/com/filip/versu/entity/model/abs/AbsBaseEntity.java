package com.filip.versu.entity.model.abs;


import com.filip.versu.entity.dto.abs.AbsBaseEntityDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@MappedSuperclass
public abstract class AbsBaseEntity<K> {

    @Id
    @GeneratedValue
    @Getter
    @Setter
    private K id;

    public AbsBaseEntity() {
    }

    public AbsBaseEntity(AbsBaseEntityDTO<K> other) {
        this.id = other.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbsBaseEntity<?> that = (AbsBaseEntity<?>) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

}
