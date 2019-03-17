package com.filip.versu.service.impl;

import com.filip.versu.entity.model.Comment;
import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.User;
import com.filip.versu.repository.CommentRepository;
import com.filip.versu.service.CommentService;
import com.filip.versu.service.NotificationService;
import com.filip.versu.service.impl.abs.AbsFeedbackPostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CommentServiceImpl extends AbsFeedbackPostServiceImpl<Comment, CommentRepository> implements CommentService {

    @Autowired
    private NotificationService notificationService;

    @Override
    public Comment create(Comment entity, User requester) {
        Comment comment = super.create(entity, requester);
        notificationService.createForComment(comment);
        return comment;
    }

    @Override
    public Comment transferUpdateFields(Comment getEntity, Comment updatedEntity) {
        getEntity = super.transferUpdateFields(getEntity, updatedEntity);
        getEntity.setContent(updatedEntity.getContent());
        return getEntity;
    }

    @Override
    public List<Comment> listByShoppingItemMostRecent(Post post) {
        return repository.findTop2ByPostOrderByIdDesc(post);
    }
}
