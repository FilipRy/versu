package com.filip.versu.entity.dto;


import com.filip.versu.entity.dto.abs.AbsBaseEntityDTO;
import com.filip.versu.entity.model.Following;

public class FollowingDTO extends AbsBaseEntityDTO<Long> {

    public UserDTO creator;
    public UserDTO target;

    public long createTime;

    public FollowingDTO() {

    }

    public FollowingDTO(Following other) {
        super(other);
        this.creator = new UserDTO(other.getCreator());
        this.target = new UserDTO(other.getTarget());
        this.createTime = other.getCreateTime();
    }

}
