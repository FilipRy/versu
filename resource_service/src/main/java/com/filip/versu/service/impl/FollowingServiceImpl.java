package com.filip.versu.service.impl;

import com.filip.versu.entity.model.Following;
import com.filip.versu.entity.model.User;
import com.filip.versu.exception.*;
import com.filip.versu.repository.FollowingRepository;
import com.filip.versu.service.FollowingService;
import com.filip.versu.service.NotificationService;
import com.filip.versu.service.UserService;
import com.filip.versu.service.impl.abs.AbsCrudServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FollowingServiceImpl extends AbsCrudServiceImpl<Following, Long, FollowingRepository> implements FollowingService {

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public Following create(Following entity, User requester) {


        User creator = userService.get(entity.getCreator().getId());

        if(creator == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.USER);
        }

        if(!requester.getId().equals(creator.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        User target = userService.get(entity.getTarget().getId());
        if(target == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.USER);
        }

        //creator and target of a following must be different
        if(creator.equals(target)) {
            throw new ForbiddenException(ExceptionMessages.ForbiddenException.FOLLOWING);
        }

        if(existsFollowingByCreatorAndTarget(creator, target)) {
            throw new EntityExistsException(ExceptionMessages.EntityExistsException.OBJECT_EXISTS);
        }

        notificationService.createForFollowing(entity);

        return super.create(entity);
    }

    @Override
    public Page<Following> findAll(Pageable pageable, User requester) {
        return super.findAll(pageable);
    }

    @Override
    public Following update(Following entity, User requester) {

        User userA = entity.getCreator();
        User userB = entity.getTarget();

        if(!requester.getId().equals(userA.getId()) && !requester.getId().equals(userB.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        return super.update(entity);
    }


    @Override
    public Following transferUpdateFields(Following getEntity, Following updatedEntity) {
        getEntity.setTarget(updatedEntity.getTarget());
        return getEntity;
    }

    @Override
    public Following delete(Long entityID, User requester) {
        Following entity = super.get(entityID);

        if(entity == null) {
            return  null;
        }

        User userA = entity.getCreator();
        User userB = entity.getTarget();

        if(!requester.getId().equals(userA.getId()) && !requester.getId().equals(userB.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        notificationService.removeByFollowing(entity);

        return super.delete(entityID);

    }

    /**
     * Returns if @creator follows @target.
     * @param creator
     * @param target
     * @return
     */
    public boolean existsFollowingByCreatorAndTarget(User creator, User target) {
        return repository.existsByCreatorAndTarget(creator, target);
    }

    @Override
    public Following getFollowingByCreatorAndTarget(User creator, User target, User requestor) {
        if(!creator.equals(requestor) && !target.equals(requestor)) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }
        return repository.findOneByCreatorAndTarget(creator, target);
    }

    @Override
    public Page<Following> listFollowersOfUser(Long userID, Long lastLoadedId, Pageable pageable, User requester) {
        User user = userService.get(userID);

        if(user == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.USER);
        }


        if (lastLoadedId == null) {
            return repository.findByTarget(user, pageable);
        } else {
            return repository.findByTargetPaging(user, lastLoadedId, pageable);
        }

    }

    @Override
    public int countFollowersOfUser(Long userID) {
        User user = userService.get(userID);
        return repository.countByTarget(user).intValue();
    }

    @Override
    public int countFollowedByOfUser(Long userID) {
        User user = userService.get(userID);
        return repository.countByCreator(user).intValue();
    }

    @Override
    public Page<Following> listFollowedByOfUser(Long userID, Long lastLoadedId, Pageable pageable, User requester) {
        User user = userService.get(userID);

        if(user == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.USER);
        }

        if(lastLoadedId == null) {
            return repository.findByCreator(user, pageable);
        } else {
            return repository.findByCreatorPaging(user, lastLoadedId, pageable);
        }

    }


    @Override
    public void removeOfUser(User user, User requester) {

        User getUser = userService.get(user.getId());

        if(getUser == null) {
            return;
        }

        if(!getUser.getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        Pageable pageable = new PageRequest(0, 50);
        Page<Following> followings = null;

        do {
            followings = listFollowedByOfUser(user.getId(), null, pageable, requester);

            for(Following following: followings) {
                delete(following.getId());
            }
            pageable = followings.nextPageable();
        } while (followings.hasNext());


        pageable = new PageRequest(0, 50);
        followings = null;

        do {
            followings = listFollowersOfUser(user.getId(), null, pageable, requester);

            for(Following following: followings) {
                delete(following.getId());
            }
            pageable = followings.nextPageable();
        } while (followings.hasNext());

        //repository.removeByCreatorOrTarget(user, user);
    }

    @Override
    public Following delete(Long entityID) {
        Following following = get(entityID);

        if(following != null) {
            notificationService.removeByFollowing(following);
        }

        return super.delete(entityID);
    }
}
