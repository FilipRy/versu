package com.filip.versu.service;

import com.filip.versu.entity.model.ExternalAccount;
import com.filip.versu.entity.model.User;
import com.filip.versu.service.abs.CrudAuthService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


public interface UserService extends CrudAuthService<User, Long> {


    public void uploadProfilePhoto(User user);

    /**
     * This method is invoked only by AbsAuthController to authenticate user.
     * @param
     * @return
     */
    public User findOnyByExternalAccountId(String externalId, ExternalAccount.ExternalAccountProvider provider);

    public User findOneByUsername(String name);

    public User findOneByEmail(String email);

    /**
     * This method can be invoked by client, the other get() does not has authorization
     * @param id
     * @param requester
     * @return
     */
    public User get(Long id, User requester);

    /**
     * This is an update method, for updating only user's lastNotificationRefresh timestamp, to avoid some locks on DB...
     * @param userId
     * @param timestamp
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateLastNotificationRefreshTimestamp(Long userId, long timestamp, User requester);

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<User> findByNameLike(String name, Pageable pageable);

}
