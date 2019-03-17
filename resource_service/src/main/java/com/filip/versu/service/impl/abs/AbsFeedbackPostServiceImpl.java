package com.filip.versu.service.impl.abs;

import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.abs.AbsFeedbackPost;
import com.filip.versu.entity.model.User;
import com.filip.versu.exception.EntityNotExistsException;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.exception.UnauthorizedException;
import com.filip.versu.repository.abs.AbsFeedbackPostRepository;
import com.filip.versu.service.abs.AbsFeedbackPostService;
import com.filip.versu.service.PostService;
import com.filip.versu.service.UserService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class AbsFeedbackPostServiceImpl<K extends AbsFeedbackPost, R extends AbsFeedbackPostRepository<K>>
        extends AbsCrudAuthServiceImpl<K, Long, R> implements AbsFeedbackPostService<K> {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;


    /**
     * This method is used by create method to verify, that the dependencies (creator, shopping item) were not removed.
     *
     * @param entity
     */
    @Override
    protected void verifyExistingRelationships(K entity, User requester) {

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
    public K transferUpdateFields(K getEntity, K updatedEntity) {
        getEntity.setDeleted(updatedEntity.isDeleted());
        return getEntity;
    }

    @Override
    public Page<K> listOfUser(Long userID, Long lastLoadedId, Pageable pageable, User requester) {
        User user = userService.get(userID);

        //if user does not exist
        if (user == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.USER);
        }

        if (!user.getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        Page<K> postFeedbacks;

        if (lastLoadedId == null) {
            postFeedbacks = repository.findByOwnerOrderByTimestampDesc(user, pageable);
        }
        else {
            postFeedbacks = repository.findByOwnerPaging(user, lastLoadedId, pageable);
        }

        for (K shopItemFeedback : postFeedbacks) {
            Post post = shopItemFeedback.getPost();
            post = postService.initializeWithMyFeedbackAction(post, requester);
            shopItemFeedback.setPost(post);
        }

        return postFeedbacks;
    }

    @Override
    public Page<K> listByPostReversePaging(Long postID, Long lastLoadedId, Pageable pageable, User requester) {
        Post post = postService.get(postID);

        //if post does not exist
        if (post == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.PHOTO);
        }

        if (!postService.canUserReadPost(requester, post)) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        Page<K> page = null;

        if(lastLoadedId == null) {
            page = repository.findByPostOrderByTimestampDesc(post, pageable);
        } else {
            page = repository.findByPostPaging(post, lastLoadedId, pageable);
        }

        //reversing order
        List<K> content = page.getContent();
        content = Lists.reverse(content);

        Page<K> pageReversed = new PageImpl<>(content, pageable, page.getTotalElements());


        return pageReversed;
    }


    @Override
    public Page<K> listByPostReversePaging(String secretUrl, Long lastLoadedId, Pageable pageable, User requester) {

        Post post = postService.getPostBySecretUrl(secretUrl);

        //if post does not exist
        if (post == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.PHOTO);
        }


        return listByPostReversePaging(post.getId(), lastLoadedId, pageable, requester);
    }

    @Override
    public K findByCreatorAndShoppingItem(User creator, Post post, User requester) {
        if (!creator.getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }
        return repository.findOneByOwnerAndPost(creator, post);
    }

    @Override
    public int countAtShoppingItem(Post post) {
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
}
