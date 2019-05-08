package com.filip.versu.entity.dto.abs;


import com.filip.versu.entity.model.abs.AbsBaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class AbsBaseEntityDTO<K> implements Serializable {

    @Getter
    @Setter
    public K id;

    public AbsBaseEntityDTO() {
    }

    public AbsBaseEntityDTO(AbsBaseEntity<K> other) {
        setId(other.getId());
    }


}
