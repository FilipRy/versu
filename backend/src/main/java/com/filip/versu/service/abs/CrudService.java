package com.filip.versu.service.abs;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface CrudService<T, K> {

    @Transactional(propagation = Propagation.REQUIRED)
    public T create(T entity);

    public T get(K id);

    /**
     * Returns all T entities in pages.
     * @param pageable
     * @return
     */
    public Page<T> findAll(Pageable pageable);

    @Transactional(propagation = Propagation.REQUIRED)
    public T update(T entity);

    @Transactional(propagation = Propagation.REQUIRED)
    public T delete(K entityID);

}
