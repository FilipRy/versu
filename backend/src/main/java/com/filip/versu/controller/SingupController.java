package com.filip.versu.controller;

import com.filip.versu.controller.abs.AbsAuthController;
import com.filip.versu.entity.dto.UserDTO;
import com.filip.versu.entity.model.User;
import com.filip.versu.entity.model.UserRole;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;


@RestController
@RequestMapping("/signup")
public class SingupController extends AbsAuthController<Long, User, UserDTO> {


    @RequestMapping(method = RequestMethod.POST)
    public UserDTO create(@RequestBody UserDTO userDTO) {

        validation.validate(userDTO);

        User user = new User(userDTO);
        user.setRoles(Arrays.asList(new UserRole("USER")));

        user = userService.create(user, user);
        return new UserDTO(user);
    }




    @Override
    protected UserDTO createDTOFromModel(User model) {
        return new UserDTO(model);
    }

    @Override
    protected User createModelFromDTO(UserDTO dto) {
        return new User(dto);
    }
}
