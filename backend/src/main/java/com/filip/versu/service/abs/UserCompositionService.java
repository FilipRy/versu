package com.filip.versu.service.abs;

import com.filip.versu.entity.model.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is the super-interface for entities, which have composition to user entity
 * (if user is removed then all its composition entities are removed).
 */
public interface UserCompositionService<T> {

    /**
     * This is used if user removes his account. This is used if user deletes his account.
     * This method needs to be called within a transaction, therefore the transaction is
     * started in UserService::delete method.
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeOfUser(User user, User requester);

}
