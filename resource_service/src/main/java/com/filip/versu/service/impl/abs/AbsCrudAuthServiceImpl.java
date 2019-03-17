package com.filip.versu.service.impl.abs;

import com.filip.versu.entity.model.abs.AbsBaseEntityWithOwner;
import com.filip.versu.entity.model.User;
import com.filip.versu.exception.EntityNotExistsException;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.exception.UnauthorizedException;
import com.filip.versu.service.abs.CrudAuthService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.io.Serializable;


@Service
public abstract class AbsCrudAuthServiceImpl<T extends AbsBaseEntityWithOwner<K>, K extends Serializable, R extends JpaRepository<T, K>> extends AbsCrudServiceImpl<T, K, R> implements CrudAuthService<T, K> {


    @Override
    public T create(T entity, User requester) {
        if(!entity.getOwner().getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }
        verifyExistingRelationships(entity, requester);

        return super.create(entity);
    }


    protected abstract void verifyExistingRelationships(T entity, User requester);

    @Override
    public Page<T> findAll(Pageable pageable, User requester) {
        //TODO read authorization admin

        return super.findAll(pageable);
    }

    @Override
    public T update(T entity, User requester) {
        T getEntity = super.get(entity.getId());
        if(getEntity == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.ENTITY_UPDATE);
        }
        if(!getEntity.getOwner().getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }
        return super.update(entity);
    }

    @Override
    public T delete(K entityID, User requester) {
        T entity = get(entityID);
        if(entity == null) {
            return entity;
        }
        if(!entity.getOwner().getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        return super.delete(entityID);
    }
}
