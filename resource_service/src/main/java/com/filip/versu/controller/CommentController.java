package com.filip.versu.controller;


import com.filip.versu.controller.abs.AbsAuthController;
import com.filip.versu.controller.abs.AbsFeedbackPostController;
import com.filip.versu.entity.dto.CommentDTO;
import com.filip.versu.entity.model.Comment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AbsAuthController.API_URL_PREFIX + "/comment")
public class CommentController extends AbsFeedbackPostController<CommentDTO, Comment> {

    @Override
    protected CommentDTO createDTOFromModel(Comment model) {
        return new CommentDTO(model, false);
    }

    @Override
    protected Comment createModelFromDTO(CommentDTO dto) {
        return new Comment(dto);
    }
}
