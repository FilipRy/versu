package com.filip.versu.service.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.filip.versu.entity.dto.URLWrapperDTO;
import com.filip.versu.entity.model.*;
import com.filip.versu.exception.EntityNotExistsException;
import com.filip.versu.exception.ExceptionMessages;
import com.filip.versu.exception.ForbiddenException;
import com.filip.versu.exception.UnauthorizedException;
import com.filip.versu.repository.PostRepository;
import com.filip.versu.service.*;
import com.filip.versu.service.impl.abs.AbsCrudAuthServiceImpl;
import com.google.common.collect.Lists;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


@Service
public class PostServiceImpl extends AbsCrudAuthServiceImpl<Post, Long, PostRepository> implements PostService {

    @Autowired
    private UserService userService;

    @Autowired
    private FollowingService followingService;

    @Autowired
    private PostPhotoService postPhotoService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private FavouriteService favouriteService;

    @Autowired
    private PostFeedbackVoteService postFeedbackVoteService;

    @Autowired
    private PostFeedbackPossibilityService postFeedbackPossibilityService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PostLocationService postLocationService;

    private SecureRandom secureRandom;

    private final static Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    private String amazonS3AccessKey;

    private String amazonS3SecretKey;

    private String bucketName;

    @Autowired
    public PostServiceImpl(Environment env) {
        amazonS3AccessKey = env.getProperty("amazon.s3.access-key");
        amazonS3SecretKey = env.getProperty("amazon.s3.secret-key");
        bucketName = env.getProperty("amazon.s3.bucket.name");
        secureRandom = new SecureRandom();
    }

    @Override
    public Post create(Post entity, User requester) {

        if (entity.getLocation() != null) {
            PostLocation createdLocation = postLocationService.create(entity.getLocation());
            entity.setLocation(createdLocation);
        }

        if (entity.getAccessType() == Post.AccessType.specific) {
            notificationService.createForPost(entity);
        }

        //meaning user wants to generate a public url to share
        if (entity.getSecretUrl() != null) {
            String url = generatePublicUrl(entity, requester);
            entity.setSecretUrl(url);
        }

        return super.create(entity, requester);
    }

    /**
     * Generates the URL, which can be used to share a post outside the app.
     *
     * @param post
     * @param user
     * @return
     */
    private String generatePublicUrl(Post post, User user) {

        Post getPost = get(post.getId());//getPost can be null if invoked by create method.
        if (getPost != null && !getPost.getOwner().equals(user)) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        byte[] randomBytes = new byte[16];//using 128 bits as MD5, having 2^128 possibilities

        //TODO use a new seed
        secureRandom.nextBytes(randomBytes);

        String publicUrl = DigestUtils.sha1Hex(randomBytes);

        return publicUrl;
    }

    @Override
    protected void verifyExistingRelationships(Post entity, User requester) {
        User getOwner = userService.get(entity.getOwner().getId());
        if (getOwner == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.USER);
        }
        if (entity.getAccessType() == Post.AccessType.specific) {
            verifyExistingViewers(entity);
        }
        entity.setPublishTime(System.currentTimeMillis());
    }


    private void verifyExistingViewers(Post entity) {
        if (entity.getViewers() != null) {
            for (int i = 0; i < entity.getViewers().size(); i++) {
                User viewer = entity.getViewers().get(i);
                User getViewer = userService.get(viewer.getId());
                if (getViewer == null) {
                    entity.getViewers().remove(viewer);
                    i++;
                }
            }
        }
    }

    @Override
    public boolean canUserReadPost(User user, Post post) {
        boolean hasUserLink = user.getUserRole() == User.UserRole.USER_WITH_LINK;
        if (hasUserLink) {//this is for the case a non-registered user having the secret url wants to read this post.
            if (post.getSecretUrl() != null && post.getSecretUrl().equals(user.getSecretUrl())) {
                return true;
            }
            return false;
        }
        return repository.isUserTheViewerOfPost(post.getId(), user);
    }

    @Override
    public Post getDetails(Long shoppingItemID, User requester) {
        Post post = get(shoppingItemID);
        if (post == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.SHOPPING_ITEM);
        }
        if (!canUserReadPost(requester, post)) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }


        post = initializeWithMyFeedbackAction(post, requester);


        return post;
    }


    @Override
    public Post getDetailsBySecretUrl(String secretUrl, User requester) {
        Post post = repository.findOneBySecretUrl(secretUrl);

        if (post == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.SHOPPING_ITEM);
        }

        return getDetails(post.getId(), requester);
    }

    public Post getPostBySecretUrl(String secretUrl) {
        return repository.findOneBySecretUrl(secretUrl);
    }

    @Override
    public Page<Post> findPostsVisibleForViewer(Long userID, Pageable pageable, User requester, Long lastLoadedId) {
        User user = userService.get(userID);

        //if user deleted his account
        if (user == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.USER);
        }

        Page<Post> postPage;

        if (lastLoadedId == null) {
            postPage = repository.findPostsVisibleForViewer(requester, user, pageable);
        } else {
            postPage = repository.findPostsVisibleForViewerPaging(requester, user, lastLoadedId, pageable);
        }


        return initializeWithMyFeedbackAction(postPage, requester);
    }

    @Override
    public Page<Post> findForUserByTime(Long userID, Long lastLoadedId, Pageable pageable, User requester) {
        User user = userService.get(userID);
        if (user == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.USER);
        }
        if (!requester.getId().equals(user.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }
        Page<Post> postPage;

        if (lastLoadedId == null) {
            postPage = repository.findActiveForUserByTime(user, pageable);
        } else {
            postPage = repository.findActiveForUserByTimePaging(user, lastLoadedId, pageable);
        }

        return initializeWithMyFeedbackAction(postPage, requester);
    }

    @Override
    public Page<Post> findForUserByLocation(Long userID, Pageable pageable, User requester, double latitude, double longitude) {
        User user = userService.get(userID);

        if (user == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.USER);
        }
        if (!requester.getId().equals(user.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        Page<Post> postPage = repository.findActiveForUserByLocation(user, latitude, longitude, NEAR_BY_MAX_DISTANCE_KM, pageable);

        return initializeWithMyFeedbackAction(postPage, user);
    }

    @Override
    public List<String[]> findVSPossibilities(String pattern, User requester) {
        List<String[]> parsedPossibilities = new ArrayList<>();
        List<String> feedbackPossibilites = repository.searchVSPossibilitiesLike("%" + pattern + "%");

        if (feedbackPossibilites != null) {
            for (String possibility : feedbackPossibilites) {
                parsedPossibilities.add(possibility.split(PostRepository.POSSIBLITIES_SEPARATOR, 2));
            }
        }

        return parsedPossibilities;
    }


    @Override
    public Page<Post> findByFeedbackPossibilitiesName(String feedbackPossibilityA, String feedbackPossibilityB, User requester, Pageable pageable) {

        return repository.findPostsByFeedbackPossibilitesName(feedbackPossibilityA,
                feedbackPossibilityB, requester, pageable);

    }


    @Override
    public Page<Post> findByLocationGoogleId(String googleId, User requester, Pageable pageable, Long lastLoadedId) {

        Page<Post> postPage;

        if (lastLoadedId == null) {
            postPage = repository.findByLocationGoogleId(googleId, requester, pageable);
        } else {
            postPage = repository.findByLocationGoogleIdPaging(googleId, lastLoadedId, requester, pageable);
        }

        return initializeWithMyFeedbackAction(postPage, requester);
    }


    private Page<Post> listAllMyPosts(Long userID, Pageable pageable, User requester) {
        User user = userService.get(userID);
        //if user deleted his account
        if (user == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.USER);
        }
        if (!requester.getId().equals(user.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }
        return repository.findByOwnerOrderByPublishTimeDesc(user, pageable);
    }

    /**
     * This method is called only by previous authorized methods.
     *
     * @param shoppingItems
     * @param requester
     * @return
     */
    @Override
    public Page<Post> initializeWithMyFeedbackAction(Page<Post> shoppingItems, User requester) {
        if (shoppingItems == null) {
            return shoppingItems;
        }
        for (Post post : shoppingItems.getContent()) {
            initializeWithMyFeedbackAction(post, requester);
        }
        return shoppingItems;
    }


    @Override
    public Post initializeWithMyFeedbackAction(Post post, User requester) {

        if (post == null) {//this can happen if user wants to see his favourites/likes ... but the shoppingitem owner removed the shoppingitem, which was liked/favourited by the user.
            return post;
        }

        Favourite myFavourite = favouriteService.findByCreatorAndShoppingItem(requester, post, requester);
        if (myFavourite != null) {
            post.setMyFavourite(myFavourite);
        }

        PostFeedbackVote myPostFeedbackVote = postFeedbackVoteService.findByCreatorAndPost(requester, post, requester);
        if (myPostFeedbackVote != null) {
            post.setMyPostFeedbackVote(myPostFeedbackVote);
        }

        //initializing post possibilities counts
        for (PostFeedbackPossibility votingPossibility : post.getPostFeedbackPossibilities()) {
            int count = postFeedbackVoteService.countByFeedbackPossibility(votingPossibility, requester);
            votingPossibility.setCount(count);
        }

        List<Comment> mostRecentComments = commentService.listByShoppingItemMostRecent(post);
        mostRecentComments = Lists.reverse(mostRecentComments);
        post.setMostRecentComment(mostRecentComments);
        return post;
    }

    @Override
    public List<User> listViewers(Long shoppingItemID, User requester) {
        Post post = get(shoppingItemID);

        if (post == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.SHOPPING_ITEM);
        }

        if (!post.getOwner().getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        if (post.getAccessType() != Post.AccessType.specific) {
            throw new ForbiddenException(ExceptionMessages.ForbiddenException.SHOPPING_ITEM);
        }

        //lazy loading
        post.getViewers().size();
        return post.getViewers();
    }


    @Override
    public Post update(Post entity, User requester) {

        Post getSI = get(entity.getId());

        if (getSI == null) {
            throw new EntityNotExistsException(ExceptionMessages.EntityNotExistsException.SHOPPING_ITEM);
        }

        return super.update(entity, requester);

    }


    @Override
    public Post transferUpdateFields(Post getEntity, Post updatedEntity) {
        getEntity.setAccessType(updatedEntity.getAccessType());
        getEntity.setDeleted(updatedEntity.isDeleted());
        getEntity.setDescription(updatedEntity.getDescription());

        if (updatedEntity.getSecretUrl() == null) {
            getEntity.setSecretUrl(null);//link can be updated only to null here
        } else if (updatedEntity.getSecretUrl().equals("generate me")) {
            getEntity.setSecretUrl(generatePublicUrl(getEntity, getEntity.getOwner()));
        }

        getEntity.setTimer(updatedEntity.getTimer());

        if (updatedEntity.getAccessType() == Post.AccessType.specific) {
            getEntity.setViewers(updatedEntity.getViewers());
            verifyExistingViewers(getEntity);
        }

        //if someone wants to mark a possibility other than in this post as chosen at this post
        if (updatedEntity.getChosenFeedbackPossibility() != null) {
            if (!getEntity.getPostFeedbackPossibilities().contains(updatedEntity.getChosenFeedbackPossibility())) {
                throw new ForbiddenException(ExceptionMessages.ForbiddenException.SHOPPING_ITEM);
            }
        }

        getEntity.setChosenFeedbackPossibility(updatedEntity.getChosenFeedbackPossibility());

        return getEntity;
    }

    @Override
    public Post delete(Long entityID, User requester) {
        Post entity = get(entityID);

        if (entity == null) {
            return null;
        }

        if (!entity.getOwner().getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        commentService.removeOfShoppingItem(entity, entity.getOwner());

        notificationService.removeByShoppingItem(entity);

        for (PostFeedbackPossibility possibility : entity.getPostFeedbackPossibilities()) {
            postFeedbackPossibilityService.delete(possibility.getId());
        }

        for (PostPhoto photo : entity.getPhotos()) {
            postPhotoService.delete(photo.getId());
        }

        entity.setDeleted(true);
        return update(entity);
    }

    @Override
    public void removeOfUser(User user, User requester) {

        User getUser = userService.get(user.getId());

        if (getUser == null) {
            return;
        }

        if (!getUser.getId().equals(requester.getId())) {
            throw new UnauthorizedException(ExceptionMessages.UnauthorizedException.UNAUTHORIZED);
        }

        Pageable pageable = new PageRequest(0, 50);
        Page<Post> shoppingItems = null;

        do {
            shoppingItems = listAllMyPosts(getUser.getId(), pageable, requester);

            for (Post post : shoppingItems) {
                delete(post.getId(), requester);
            }
            pageable = shoppingItems.nextPageable();
        } while (shoppingItems.hasNext());

    }

    @Override
    public URLWrapperDTO generatePreSignedURL(URLWrapperDTO objectKeys) {

        System.setProperty(SDKGlobalConfiguration.ENABLE_S3_SIGV4_SYSTEM_PROPERTY, "true");
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(amazonS3AccessKey, amazonS3SecretKey);
        AmazonS3 s3client = new AmazonS3Client(awsCredentials);
        try {

            for (URLWrapperDTO.PhotoObject photoObject : objectKeys.objectKeys) {
                if (logger.isInfoEnabled()) {
                    logger.info("Generating pre-signed URL.");
                }
                java.util.Date expiration = new java.util.Date();
                long milliSeconds = expiration.getTime();
                milliSeconds += PRE_SIGNED_URL_TTL;
                expiration.setTime(milliSeconds);

                GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, photoObject.key);
                generatePresignedUrlRequest.setMethod(HttpMethod.PUT);
                generatePresignedUrlRequest.setExpiration(expiration);
                generatePresignedUrlRequest.setContentType(photoObject.contentType);

                ResponseHeaderOverrides headerOverrides = new ResponseHeaderOverrides();
                headerOverrides.setContentType(photoObject.contentType);

                generatePresignedUrlRequest.setResponseHeaders(headerOverrides);


                URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest);

                if (logger.isInfoEnabled()) {
                    logger.info("Pre-Signed URL = " + url.toString());
                }

                objectKeys.urls.add(url.toString());
            }
            return objectKeys;

        } catch (AmazonServiceException exception) {
            if (logger.isInfoEnabled()) {
                logger.info("Caught an AmazonServiceException, which means your request made it to Amazon S3, but was rejected with an error response for some reason.");
                logger.info("Error Message: " + exception.getMessage());
                logger.info("HTTP  Code: " + exception.getStatusCode());
                logger.info("AWS Error Code:" + exception.getErrorCode());
                logger.info("Error Type:    " + exception.getErrorType());
                logger.info("Request ID:    " + exception.getRequestId());
            }
        } catch (AmazonClientException ace) {
            if (logger.isInfoEnabled()) {
                logger.info("Caught an AmazonClientException, " +
                        "which means the client encountered " +
                        "an internal error while trying to communicate" +
                        " with S3, " +
                        "such as not being able to access the network.");
                logger.info("Error Message: " + ace.getMessage());
            }
        }
        return null;
    }


}
