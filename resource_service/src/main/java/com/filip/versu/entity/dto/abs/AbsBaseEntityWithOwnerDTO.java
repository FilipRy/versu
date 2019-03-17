package com.filip.versu.entity.dto.abs;


import com.filip.versu.entity.dto.UserDTO;
import com.filip.versu.entity.model.abs.AbsBaseEntityWithOwner;
import org.springframework.stereotype.Component;

@Component
public abstract class AbsBaseEntityWithOwnerDTO<K> extends AbsBaseEntityDTO<K> {

    public UserDTO owner;

    public AbsBaseEntityWithOwnerDTO(AbsBaseEntityWithOwner<K> other) {
        super(other);
        this.owner = new UserDTO(other.getOwner());
    }

    public AbsBaseEntityWithOwnerDTO() {
    }
}
