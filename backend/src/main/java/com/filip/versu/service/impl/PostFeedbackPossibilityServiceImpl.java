package com.filip.versu.service.impl;

import com.filip.versu.entity.model.PostFeedbackPossibility;
import com.filip.versu.repository.PostFeedbackPossibilityRepository;
import com.filip.versu.service.PostFeedbackPossibilityService;
import com.filip.versu.service.PostFeedbackVoteService;
import com.filip.versu.service.impl.abs.AbsCrudServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostFeedbackPossibilityServiceImpl
        extends AbsCrudServiceImpl<PostFeedbackPossibility, Long, PostFeedbackPossibilityRepository>
        implements PostFeedbackPossibilityService {

    @Autowired
    private PostFeedbackVoteService postFeedbackVoteService;

    @Override
    public PostFeedbackPossibility transferUpdateFields(PostFeedbackPossibility getEntity, PostFeedbackPossibility updatedEntity) {
        getEntity.setDeleted(updatedEntity.isDeleted());
        return getEntity;
    }

    @Override
    public PostFeedbackPossibility delete(Long entityID) {
        PostFeedbackPossibility entity = get(entityID);

        if(entity == null) {
            return entity;
        }

        postFeedbackVoteService.removeByFeedbackPossibility(entity, entity.getPost().getOwner());

        entity.setDeleted(true);
        return update(entity);
    }
}
