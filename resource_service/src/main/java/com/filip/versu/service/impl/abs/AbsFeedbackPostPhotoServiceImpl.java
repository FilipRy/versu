package com.filip.versu.service.impl.abs;

import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.PostPhoto;
import com.filip.versu.entity.model.abs.AbsFeedbackPostPhoto;
import com.filip.versu.entity.model.User;
import com.filip.versu.exception.EntityNotExistsException;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.exception.FeedbackActionExistsException;
import com.filip.versu.exception.UnauthorizedException;
import com.filip.versu.repository.abs.AbsFeedbackPostPhotoRepository;
import com.filip.versu.service.abs.AbsFeedbackPostPhotoService;
import com.filip.versu.service.PostPhotoService;
import com.filip.versu.service.PostService;
import com.filip.versu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public abstract class AbsFeedbackPostPhotoServiceImpl<K extends AbsFeedbackPostPhoto, R extends AbsFeedbackPostPhotoRepository<K>> extends AbsCrudAuthServiceImpl<K, Long, R> implements AbsFeedbackPostPhotoService<K> {

    @Autowired
    private PostService postService;

    @Autowired
    private PostPhotoService postPhotoService;

    @Autowired
    private UserService userService;

    /**
     * This method is used by create method to verify, that the dependencies (creator, photo) were not removed.
     * @param entity
     */
    @Override
    protected void verifyExistingRelationships(K entity, User requester) {

        PostPhoto photo = postPhotoService.get(entity.getPhoto().getId());
        entity.setPhoto(photo);//if the feedback action is created and only id of photo is sent to backend
        if(photo == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.SHOPPING_ITEM);
        }

        boolean canRead = postService.canUserReadPost(requester, photo.getPost());
        if (!canRead) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }


        User user = userService.get(entity.getOwner().getId());
        if(user == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.USER);
        }

        entity.setTimestamp(System.currentTimeMillis());
    }


    @Override
    public K findByCreatorAndPhoto(User creator, PostPhoto photo, User requester) {
        if(!creator.getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }
        return repository.findOneByOwnerAndPhoto(creator, photo);
    }

    /**
     * This method checks if the feedback action (vote_yes yes, vote_yes no) was already created on the shopping item by the same creator.
     * @param entity
     */
    protected void checkAlreadyExisting(K entity) {

        K existing = findByCreatorAndPhoto(entity.getOwner(), entity.getPhoto(), entity.getOwner());
        if(existing != null) {
            throw new FeedbackActionExistsException(ExceptionMessages.FeedbackActionExistsException.FEEDBACK_ACTION);
        }

    }



    @Override
    public Page<K> listByPhoto(Long photoID, Pageable pageable, User requester) {
        PostPhoto photo = postPhotoService.get(photoID);

        //if shopping item does not exist
        if(photo == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.PHOTO);
        }

        if(!postService.canUserReadPost(requester, photo.getPost())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        return repository.findByPhoto(photo, pageable);
    }

    @Override
    public Page<K> listOfUser(Long userID, Pageable pageable, User requester) {

        User user = userService.get(userID);

        //if user does not exist
        if(user == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.USER);
        }

        if(!user.getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        Page<K> feedbackActions = repository.findByOwner(user, pageable);

        for(K feedbackAction: feedbackActions) {
            Post post = feedbackAction.getPhoto().getPost();
            post = postService.initializeWithMyFeedbackAction(post, requester);
            feedbackAction.getPhoto().setPost(post);
        }

        return feedbackActions;
    }

    @Override
    public int countAtPhoto(PostPhoto photo) {
        Long count = repository.countByPhoto(photo);
        return count == null ? 0 : count.intValue();
    }

    @Override
    public K transferUpdateFields(K getEntity, K updatedEntity) {
        getEntity.setDeleted(updatedEntity.isDeleted());
        return getEntity;
    }

    @Override
    public void removeOfPhoto(PostPhoto photo, User requester) {
        PostPhoto getPhoto = postPhotoService.get(photo.getId());

        //the photo is already deleted
        if(getPhoto == null) {
            return;
        }

        if(!getPhoto.getPost().getOwner().getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }
        repository.markAsDeletedByPhoto(photo);
    }

    /**
     * This method soft deletes the feedback actions of @userID.
     * @param user
     */
    @Override
    public void removeOfUser(User user, User requester) {
        User getUser = userService.get(user.getId());

        //the user is already deleted
        if(getUser == null) {
            return;
        }

        if(!getUser.getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        repository.markAsDeletedByCreator(user);
    }
}
