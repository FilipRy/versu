package com.filip.versu.service.abs;

import com.filip.versu.entity.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * This is the CrudService with User parameters used for authentication
 * @param <T>
 * @param <K>
 */
public interface CrudAuthService<T, K> {

    @Transactional(propagation = Propagation.REQUIRED)
    public T create(T entity, User requester);

    public T get(K id);

    /**
     * Returns all T entities in pages.
     * @param pageable
     * @return
     */
    public Page<T> findAll(Pageable pageable, User requester);

    @Transactional(propagation = Propagation.REQUIRED)
    public T update(T entity, User requester);

    @Transactional(propagation = Propagation.REQUIRED)
    public T delete(K entityID, User requester);

}
