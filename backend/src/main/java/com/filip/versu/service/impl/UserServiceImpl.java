package com.filip.versu.service.impl;


import com.filip.versu.entity.model.GoogleLocation;
import com.filip.versu.entity.model.User;
import com.filip.versu.entity.model.UserRole;
import com.filip.versu.exception.EntityExistsException;
import com.filip.versu.exception.EntityNotExistsException;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.exception.UnauthorizedException;
import com.filip.versu.repository.UserRepository;
import com.filip.versu.service.*;
import com.filip.versu.service.abs.GoogleLocationService;
import com.filip.versu.service.impl.abs.AbsCrudServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
public class UserServiceImpl extends AbsCrudServiceImpl<User, Long, UserRepository> implements UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    @Autowired
    private FollowingService followingService;

    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private GoogleLocationService googleLocationService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String amazonS3AccessKey;

    private String amazonS3SecretKey;

    private String bucketName;

    private String bucketUrl;

    private String profilePicsDirName;

    @Autowired
    public UserServiceImpl(Environment env) {
        amazonS3AccessKey = env.getProperty("amazon.s3.access-key");
        amazonS3SecretKey = env.getProperty("amazon.s3.secret-key");
        bucketName  = env.getProperty("amazon.s3.bucket.name");
        bucketUrl  = env.getProperty("amazon.s3.bucket.url");
        profilePicsDirName  = env.getProperty("amazon.s3.directory.profile_pics");
    }

    /**
     * @param entity    - must contain only externalAccount of user, which is to be linked with this user now.
     * @param requester
     * @return
     */
    @Override
    public User create(User entity, User requester) {
        entity.setRegistrationTime(System.currentTimeMillis());

        User userWithSameUsername = findOneByUsername(entity.getUsername());
        if (userWithSameUsername != null) {
            throw new EntityExistsException(ExceptionMessages.EntityExistsException.USERNAME_TAKEN);
        }
        User userWithSameEmail = findOneByEmail(entity.getEmail());
        if (userWithSameEmail != null) {
            throw new EntityExistsException(ExceptionMessages.EntityExistsException.EMAIL_TAKEN);
        }

        if (entity.getLocation() != null) {
            GoogleLocation userLocation = entity.getLocation();
            userLocation = googleLocationService.create(userLocation);
            entity.setLocation(userLocation);
        }

        entity.setRoles(Arrays.asList(new UserRole("USER")));
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));

        logger.info("User with name: " + entity.getUsername() + " has been registered.");

        return super.create(entity);
    }

    @Override
    public Page<User> findAll(Pageable pageable, User requester) {
        //TODO read authorization
        return super.findAll(pageable);
    }

    @Override
    public User get(Long id, User requester) {
        User user = get(id);

        if (user == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.USER);
        }

        if (!user.getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }
        return super.get(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public User get(Long id) {
        if (id == null) {
            return null;
        }
        User user = super.get(id);
        if (user == null) {
            return null;
        }
        return user;
    }

    @Override
    public User findOneByUsername(String name, User requester) {
        if (!requester.getUsername().equals(name)) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }
        return repository.findOneByUsername(name);
    }

    @Override
    public User findOneByUsername(String name) {
        return repository.findOneByUsername(name);
    }

    @Override
    public User findOneByEmail(String email) {
        return repository.findOneByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        return repository.findOneByUsername(name);
    }

    @Override
    public Page<User> findByNameLike(String name, Pageable pageable) {
        return repository.findByUsernameLike(name, pageable);
    }

    @Override
    public User update(User entity, User requester) {

        User user = get(entity.getId());

        if (user == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.USER);
        }

        if (!requester.getId().equals(user.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        //checking on existing username, only if username was updated
        if (!entity.getUsername().equals(user.getUsername())) {
            User userWithSameUsername = findOneByUsername(entity.getUsername());
            if (userWithSameUsername != null && !userWithSameUsername.equals(user)) {
                throw new EntityExistsException(ExceptionMessages.EntityExistsException.USERNAME_TAKEN);
            }
        }

        //checking on existing email, only if email was updated
        if (!entity.getEmail().equals(user.getEmail())) {
            User userWithSameEmail = findOneByEmail(entity.getEmail());
            if (userWithSameEmail != null && !userWithSameEmail.equals(user)) {
                throw new EntityExistsException(ExceptionMessages.EntityExistsException.EMAIL_TAKEN);
            }
        }

        return super.update(entity);
    }

    @Override
    public void updateLastNotificationRefreshTimestamp(Long userId, long timestamp, User requester) {
        if (!requester.getId().equals(userId)) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }
        repository.updateLastNotificationRefreshTimestamp(userId, timestamp);
    }

    @Override
    public User transferUpdateFields(User getEntity, User updatedEntity) {
        getEntity.setUsername(updatedEntity.getUsername());
        getEntity.setEmail(updatedEntity.getEmail());
        if (updatedEntity.getPassword() != null) {
            getEntity.setPassword(updatedEntity.getPassword());
        }
        getEntity.setQuote(updatedEntity.getQuote());

        if (updatedEntity.getLocation() != null) {
            if (updatedEntity.getLocation().getId() == null) {
                GoogleLocation userLocation = updatedEntity.getLocation();
                userLocation = googleLocationService.create(userLocation);
                updatedEntity.setLocation(userLocation);
            }
        }
        getEntity.setLocation(updatedEntity.getLocation());

        return getEntity;
    }

    @Override
    public User delete(Long entityID, User requester) {
        User entity = get(entityID);

        if (entity == null) {
            return null;
        }

        if (!requester.getId().equals(entity.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        followingService.removeOfUser(entity, requester);
        deviceInfoService.removeOfUser(entity, requester);

        postService.removeOfUser(entity, requester);

        commentService.removeOfUser(entity, requester);
        notificationService.removeByUser(entity);


        entity.setDeleted(true);
        return update(entity, requester);
    }

}
