package com.filip.versu.service.impl;

import com.filip.versu.entity.model.Favourite;
import com.filip.versu.entity.model.User;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.exception.FeedbackActionExistsException;
import com.filip.versu.repository.FavouriteRepository;
import com.filip.versu.service.FavouriteService;
import com.filip.versu.service.impl.abs.AbsFeedbackPostServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class FavouriteServiceImpl extends AbsFeedbackPostServiceImpl<Favourite, FavouriteRepository> implements FavouriteService {

    @Override
    protected void verifyExistingRelationships(Favourite entity, User requester) {
        super.verifyExistingRelationships(entity, requester);
        checkAlreadyExisting(entity);
    }

    /**
     * This method checks if the feedback action (favourite) was already created on the shopping item by the same creator.
     * @param entity
     */
    protected void checkAlreadyExisting(Favourite entity) {

        Favourite existing = findByCreatorAndShoppingItem(entity.getOwner(), entity.getPost(), entity.getOwner());
        if(existing != null) {
            throw new FeedbackActionExistsException(ExceptionMessages.FeedbackActionExistsException.FEEDBACK_ACTION);
        }

    }
}
