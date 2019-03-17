package com.filip.versu.service.impl;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.util.IOUtils;
import com.filip.versu.entity.model.ExternalAccount;
import com.filip.versu.entity.model.User;
import com.filip.versu.entity.model.UserLocation;
import com.filip.versu.exception.EntityExistsException;
import com.filip.versu.exception.EntityNotExistsException;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.exception.UnauthorizedException;
import com.filip.versu.repository.UserRepository;
import com.filip.versu.service.*;
import com.filip.versu.service.impl.abs.AbsCrudServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl extends AbsCrudServiceImpl<User, Long, UserRepository> implements UserService {

    @Autowired
    private FollowingService followingService;

    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private FavouriteService favouriteService;

    @Autowired
    private UserLocationService userLocationService;

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
        /**
         * Checking if the "requester" is already associated with some facebook ID.
         */
        List<User> users = new ArrayList<>();
        for (ExternalAccount externalAccount : entity.getExternalAccounts()) {
            if (externalAccount.getExternalUserId() != null) {
                users = repository.findByExternalAccountID(externalAccount.getExternalUserId(), externalAccount.getProvider());
            }
        }

        if (users != null && users.size() > 0) {
            return users.get(0);
        } else {
            User userWithSameUsername = findOneByUsername(entity.getUsername());
            if (userWithSameUsername != null) {
                throw new EntityExistsException(ExceptionMessages.EntityExistsException.USERNAME_TAKEN);
            }
            User userWithSameEmail = findOneByEmail(entity.getEmail());
            if (userWithSameEmail != null) {
                throw new EntityExistsException(ExceptionMessages.EntityExistsException.EMAIL_TAKEN);
            }

            if (entity.getLocation() != null) {
                UserLocation userLocation = entity.getLocation();
                userLocation = userLocationService.create(userLocation);
                entity.setLocation(userLocation);
            }

            if (entity.getProfilePhotoURL() != null) {
                uploadProfilePhoto(entity);
            }

            return super.create(entity);
        }
    }

    public void uploadProfilePhoto(User user) {

        if(user.getProfilePhotoURL() == null) {
            return;
        }

        if(!user.getProfilePhotoURL().startsWith("https://scontent.xx.fbcdn.net")) {//TODO find better solutions, this is just to accept only photos from facebook
            return;
        }

        System.setProperty(SDKGlobalConfiguration.ENABLE_S3_SIGV4_SYSTEM_PROPERTY, "true");
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(amazonS3AccessKey, amazonS3SecretKey);
        AmazonS3 s3client = new AmazonS3Client(awsCredentials);

        try {
            URL url = new URL(user.getProfilePhotoURL());

            byte[] content = IOUtils.toByteArray(url.openStream());
            int contentLength = content.length;

            String profilePicName = DigestUtils.sha1Hex(user.getUsername());//username is unique
            String objectKey = profilePicsDirName + "/" + profilePicName + ".jpg";

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType("image/jpeg");
            objectMetadata.setContentLength(contentLength);
            s3client.putObject(bucketName, objectKey, url.openStream(), objectMetadata);

            user.setProfilePhotoURL(bucketUrl + objectKey);

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        user.getExternalAccounts().size();//force to load external accounts
        return user;
    }

    @Override
    public User findOnyByExternalAccountId(String externalId, ExternalAccount.ExternalAccountProvider provider) {
        List<User> users = repository.findByExternalAccountID(externalId, provider);
        if (users == null || users.size() == 0) {
            return null;
        }
        return users.get(0);
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
        getEntity.setPassword(updatedEntity.getPassword());
        getEntity.setQuote(updatedEntity.getQuote());

        if (updatedEntity.getLocation() != null) {
            if (updatedEntity.getLocation().getId() == null) {
                UserLocation userLocation = updatedEntity.getLocation();
                userLocation = userLocationService.create(userLocation);
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
        favouriteService.removeOfUser(entity, requester);

        entity.setDeleted(true);
        return update(entity, requester);
    }

}
