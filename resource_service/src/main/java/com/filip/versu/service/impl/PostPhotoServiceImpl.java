package com.filip.versu.service.impl;

import com.filip.versu.entity.model.PostPhoto;
import com.filip.versu.repository.PostPhotoRepository;
import com.filip.versu.service.CommentService;
import com.filip.versu.service.PostPhotoService;
import com.filip.versu.service.impl.abs.AbsCrudServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PostPhotoServiceImpl extends AbsCrudServiceImpl<PostPhoto, Long, PostPhotoRepository> implements PostPhotoService {


    @Autowired
    private CommentService commentService;

    @Override
    public PostPhoto transferUpdateFields(PostPhoto getEntity, PostPhoto updatedEntity) {
        getEntity.setDescription(updatedEntity.getDescription());
        getEntity.setDeleted(updatedEntity.isDeleted());
        return getEntity;
    }

    /**
     * This method is called only from authorized method!!!
     * @param entityID
     * @return
     */
    @Override
    public PostPhoto delete(Long entityID) {
        PostPhoto entity = get(entityID);
        if(entity == null) {
            return null;
        }
        //I am not deleting yes-votes here, because they are used to compute user's score.
        //photoVoteService.removeOfPhoto(entity, entity.getShoppingItem().getOwner());

        entity.setDeleted(true);
        return update(entity);
    }
}
