package com.filip.versu.service;

import com.filip.versu.entity.model.Comment;
import com.filip.versu.entity.model.Post;
import com.filip.versu.service.abs.AbsFeedbackPostService;

import java.util.List;


public interface CommentService extends AbsFeedbackPostService<Comment> {

    /**
     * This method is called only from shoppingItemServiceImpl.initWithFeedbackAction, which is already authorized -> no need for auth here.
     * This method returns the @count most recent comments given on a shoppingitem.
     * @param post
     * @return
     */
    public List<Comment> listByShoppingItemMostRecent(Post post);
}
