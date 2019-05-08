package com.filip.versu.service.impl;

import com.filip.versu.entity.model.Comment;
import com.filip.versu.entity.model.Notification;
import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.User;
import com.filip.versu.exception.EntityNotExistsException;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.exception.UnauthorizedException;
import com.filip.versu.repository.CommentRepository;
import com.filip.versu.service.CommentService;
import com.filip.versu.service.NotificationService;
import com.filip.versu.service.PostService;
import com.filip.versu.service.UserService;
import com.filip.versu.service.impl.abs.AbsCrudAuthServiceImpl;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CommentServiceImpl extends AbsCrudAuthServiceImpl<Comment, Long, CommentRepository> implements CommentService {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public Comment create(Comment entity, User requester) {
        Comment comment = super.create(entity, requester);

        Notification notification = new Notification(entity.getPost().getOwner(), entity.getPost().getId(), Notification.NotificationType.comment, entity.getOwner());
        notificationService.createAsync(notification);

        return comment;
    }

    /**
     * This method is used by create method to verify, that the dependencies (creator, shopping item) were not removed.
     *
     * @param entity
     */
    @Override
    protected void verifyExistingRelationships(Comment entity, User requester) {

        String secretUrl = entity.getPost().getSecretUrl();

        Post post = postService.get(entity.getPost().getId());

        if (post == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.SHOPPING_ITEM);
        }

        if (secretUrl != null && !secretUrl.equals(post.getSecretUrl())) {
            //attacker has a correct secret url, but can try to create feedback action on another post
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        entity.setPost(post);//if the feedback action is created and only id of photo is sent to backend

        boolean canRead = postService.canUserReadPost(requester, post);
        if (!canRead) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        User user = userService.get(entity.getOwner().getId());
        if (user == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.USER);
        }

        entity.setTimestamp(System.currentTimeMillis());

    }

    @Override
    public Page<Comment> listOfUser(Long userID, Long lastLoadedId, Pageable pageable, User requester) {
        User user = userService.get(userID);

        //if user does not exist
        if (user == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.USER);
        }

        if (!user.getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        Page<Comment> postFeedbacks;

        if (lastLoadedId == null) {
            postFeedbacks = repository.findByOwnerOrderByTimestampDesc(user, pageable);
        }
        else {
            postFeedbacks = repository.findByOwnerPaging(user, lastLoadedId, pageable);
        }

        for (Comment shopItemFeedback : postFeedbacks) {
            Post post = shopItemFeedback.getPost();
            post = postService.initializeWithMyFeedbackAction(post, requester);
            shopItemFeedback.setPost(post);
        }

        return postFeedbacks;
    }

    @Override
    public Page<Comment> listByPostReversePaging(Long postID, Long lastLoadedId, Pageable pageable, User requester) {
        Post post = postService.get(postID);

        //if post does not exist
        if (post == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.PHOTO);
        }

        if (!postService.canUserReadPost(requester, post)) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        Page<Comment> page = null;

        if(lastLoadedId == null) {
            page = repository.findByPostOrderByTimestampDesc(post, pageable);
        } else {
            page = repository.findByPostPaging(post, lastLoadedId, pageable);
        }

        //reversing order
        List<Comment> content = page.getContent();
        content = Lists.reverse(content);

        Page<Comment> pageReversed = new PageImpl<>(content, pageable, page.getTotalElements());


        return pageReversed;
    }


    @Override
    public Page<Comment> listByPostReversePaging(String secretUrl, Long lastLoadedId, Pageable pageable, User requester) {

        Post post = postService.getPostBySecretUrl(secretUrl);

        //if post does not exist
        if (post == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.PHOTO);
        }


        return listByPostReversePaging(post.getId(), lastLoadedId, pageable, requester);
    }

    @Override
    public Comment findByCreatorAndShoppingItem(User creator, Post post, User requester) {
        if (!creator.getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }
        return repository.findOneByOwnerAndPost(creator, post);
    }

    @Override
    public int countAtPost(Post post) {
        Long count = repository.countByPost(post);
        return count == null ? 0 : count.intValue();
    }

    @Override
    public void removeOfShoppingItem(Post post, User requester) {
        Post getPost = postService.get(post.getId());

        //shopping item was already deleted
        if (getPost == null) {
            return;
        }

        if (!post.getOwner().equals(requester)) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        repository.markAsDeletedByPost(post);
    }

    @Override
    public void removeOfUser(User user, User requester) {
        User getUser = userService.get(user.getId());

        //the user is already deleted
        if (getUser == null) {
            return;
        }

        if (!getUser.getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        repository.markAsDeletedByCreator(user);
    }

    @Override
    public Comment transferUpdateFields(Comment getEntity, Comment updatedEntity) {
        getEntity.setDeleted(updatedEntity.isDeleted());
        getEntity.setContent(updatedEntity.getContent());
        return getEntity;
    }

    @Override
    public List<Comment> listByShoppingItemMostRecent(Post post) {
        return repository.findTop2ByPostOrderByIdDesc(post);
    }
}
