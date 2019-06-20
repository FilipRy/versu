package com.filip.versu.service.impl.abs;

import com.filip.versu.entity.model.abs.AbsBaseEntity;
import com.filip.versu.exception.EntityExistsException;
import com.filip.versu.exception.EntityNotExistsException;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.service.abs.CrudService;
import com.filip.versu.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;


@Service
public abstract class AbsCrudServiceImpl<T extends AbsBaseEntity<K>, K extends Serializable, R extends JpaRepository<T, K>> implements CrudService<T, K> {

    private final static Logger logger = LoggerFactory.getLogger(AbsCrudServiceImpl.class);


    @Autowired
    protected R repository;

    protected Class<T> clazz;

    public AbsCrudServiceImpl() {
        this.clazz = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public T create(T entity) {

        T existing = get(entity.getId());

        if(existing != null) {
            throw new EntityExistsException(ExceptionMessages.EntityExistsException.OBJECT_EXISTS);
        }

        entity = repository.save(entity);

        logger.info("Creating " + clazz.getSimpleName() + " with id = " + entity.getId());

        return entity;
    }

    @Override
    public T get(K id) {
        if(id == null) {
            return null;
        }
        Optional<T> returned =  repository.findById(id);
        if (returned != null && returned.isPresent()) {
            return returned.get();
        } else {
            return null;
        }
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public T update(T entity) {
        T getEntity = get(entity.getId());
        if(getEntity == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.ENTITY_UPDATE);
        }
        entity = transferUpdateFields(getEntity, entity);
        if(getEntity.equals(entity)) {
            return entity;
        }
        return repository.save(entity);
    }

    /**
     * Transfers all parameters of @T, which can be updated to the entity got from DB.
     * @param getEntity - the entity got from DB by ID
     * @param updatedEntity - the entity containing parameters to be updated
     * @return - the updated entity
     */
    public abstract T transferUpdateFields(T getEntity, T updatedEntity);

    @Override
    public T delete(K entityID) {
        T entity = get(entityID);
        if(entity == null) {
            return entity;
        }
        repository.delete(entity);
        return entity;
    }
}
