package com.filip.versu.service.impl;

import com.filip.versu.entity.model.*;
import com.filip.versu.exception.EntityNotExistsException;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.exception.ForbiddenException;
import com.filip.versu.exception.UnauthorizedException;
import com.filip.versu.repository.PostFeedbackVoteRepository;
import com.filip.versu.service.*;
import com.filip.versu.service.NotificationService;
import com.filip.versu.service.impl.abs.AbsCrudAuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class PostFeedbackVoteServiceImpl extends AbsCrudAuthServiceImpl<PostFeedbackVote, Long, PostFeedbackVoteRepository> implements PostFeedbackVoteService {

    @Autowired
    private PostService postService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostFeedbackPossibilityService postFeedbackPossibilityService;


    @Override
    public PostFeedbackVote create(PostFeedbackVote entity, User requester) {
        entity.setTimestamp(System.currentTimeMillis());
        PostFeedbackVote postFeedbackVote = super.create(entity, requester);

        Notification notification = new Notification(entity.getPostFeedbackPossibility().getPost().getOwner(), entity.getPostFeedbackPossibility().getPost().getId(), Notification.NotificationType.post_feedback, entity.getOwner());
        notificationService.createAsync(notification);

        return postFeedbackVote;
    }

    @Override
    protected void verifyExistingRelationships(PostFeedbackVote entity, User requester) {

        /**
         * feedback vote can be given only to non-chosen post
         */
        if(entity.getPostFeedbackPossibility().getPost().getChosenFeedbackPossibility() != null) {
            throw new ForbiddenException(ExceptionMessages.ForbiddenException.POST_FEEDBACK);
        }

        PostFeedbackPossibility postFeedbackPossibility = postFeedbackPossibilityService.get(entity.getPostFeedbackPossibility().getId());

        //if post postFeedbackPossibility not exist
        if(postFeedbackPossibility == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.SHOPPING_ITEM);
        }

        /**
         * check if there is already an existing answer by entity.getOwner()
         */
        PostFeedbackVote postFeedbackVote = findByCreatorAndPost(entity.getOwner(), entity.getPostFeedbackPossibility().getPost(), requester);
        if(postFeedbackVote != null) {
            delete(postFeedbackVote.getId(), requester);
        }

    }

    @Override
    public Page<PostFeedbackVote> findByFeedbackPossibilityReversePaging(Long postFeedbackPossibilityId, Long lastLoadedId, Pageable pageable, User requester) {

        PostFeedbackPossibility postFeedbackPossibility = postFeedbackPossibilityService.get(postFeedbackPossibilityId);

        //if post postFeedbackPossibility not exist
        if(postFeedbackPossibility == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.SHOPPING_ITEM);
        }

        Long postId = postFeedbackPossibility.getPost().getId();
        String secretUrl = postFeedbackPossibility.getPost().getSecretUrl();

        Post post = postService.get(postId);

        //if post does not exist
        if(post == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.SHOPPING_ITEM);
        }

        //if we have only the id of the post
        if(secretUrl == null) {
            if(!postService.canUserReadPost(requester, post)) {
                throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
            }
        }


        if(lastLoadedId == null) {
            return repository.findByPostFeedbackPossibilityOrderByTimestampDesc(postFeedbackPossibility, pageable);
        }

        return repository.findByPostFeedbackPossibilityPaging(postFeedbackPossibility, lastLoadedId, pageable);
    }


    @Override
    public Page<PostFeedbackVote> findByUserPaging(Long userID, Pageable pageable, User requester, Long lastLoadedId) {

        User user = userService.get(userID);

        //if user does not exist
        if (user == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.USER);
        }

        if (!user.getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        Page<PostFeedbackVote> postFeedbackVotes = null;

        if(lastLoadedId == null || lastLoadedId == -1l) {
            postFeedbackVotes = repository.findByOwnerOrderByTimestampDesc(user, pageable);
        } else {
            postFeedbackVotes = repository.findByOwnerPaging(user, lastLoadedId, pageable);
        }



        for(PostFeedbackVote postFeedbackVote: postFeedbackVotes) {
            Post post = postFeedbackVote.getPostFeedbackPossibility().getPost();
            post = postService.initializeWithMyFeedbackAction(post, requester);
            postFeedbackVote.getPostFeedbackPossibility().setPost(post);
        }

        return postFeedbackVotes;
    }

    @Override
    public int countByFeedbackPossibility(PostFeedbackPossibility postFeedbackPossibility, User requester) {
        Long countLong = repository.countByPostFeedbackPossibility(postFeedbackPossibility);
        return countLong == null ? 0 : countLong.intValue();
    }

    @Override
    public PostFeedbackVote findByCreatorAndPost(User creator, Post post, User requester) {
        if (!creator.getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }
        return repository.findOneByOwnerAndPost(creator, post);
    }

    @Override
    public PostFeedbackVote transferUpdateFields(PostFeedbackVote getEntity, PostFeedbackVote updatedEntity) {
        getEntity.setDeleted(updatedEntity.isDeleted());
        return getEntity;
    }


    @Override
    public boolean removeByFeedbackPossibility(PostFeedbackPossibility postFeedbackPossibility, User requester) {
        postFeedbackPossibility = postFeedbackPossibilityService.get(postFeedbackPossibility.getId());

        if (postFeedbackPossibility == null) {
            return true;
        }

        Post post = postFeedbackPossibility.getPost();

        if (!post.getOwner().equals(requester)) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }
        repository.markAsDeletedByFeedbackPossibility(postFeedbackPossibility);

        return true;
    }

    @Override
    public boolean removeByCreator(User user, User requester) {
        User getUser = userService.get(user.getId());

        //the user is already deleted
        if (getUser == null) {
            return true;
        }

        if (!getUser.getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        repository.markAsDeletedByCreator(user);

        return true;
    }
}
