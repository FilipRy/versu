package com.filip.versu.bootstrap;

import com.filip.versu.entity.model.*;
import com.filip.versu.repository.CommentRepository;
import com.filip.versu.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private FollowingService followingService;

    @Autowired
    private PostService postService;

    @Autowired
    private Environment environment;

    @Autowired
    private PostFeedbackVoteService postFeedbackVoteService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;



    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        String ddlProperty = environment.getProperty("spring.jpa.hibernate.ddl-auto");

        commentRepository.setNamesToUtf8Mb4();//this is a hack to set name, otherwise emojis cannot be saved

        if (ddlProperty.contains("create") ) {
            init();
        }


    }


    private void init() {

        List<User> registeredUsers = new ArrayList<>();

        String nameFilip = "Filip Rydzi";

        User userFilip = new User();
        userFilip.setUsername(nameFilip);
        userFilip.setPassword("000" + nameFilip);
        userFilip.setEmail(nameFilip + "@" + nameFilip + ".com");

        userFilip.setProfilePhotoURL("https://s3-eu-west-1.amazonaws.com/dress-me-test1/profile_pics/7e2173e7502e4c3f35312b5379039e0d39207754.jpg");
        userFilip = userService.create(userFilip, userFilip);

        String usernames[] = { "John Walker", "Alex Green", "Victoria Malkovich", "Emily Tomato", "Jeny White", "George High", "Ema Tavares", "Rome Johnson", "Robert Geisen", "Simona Lee" };

        for (String username: usernames) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(username.toLowerCase().replaceAll(" ", "")+"@test.com");
            user.setPassword("123456789");
            user.setQuote("Im the best");
            user = userService.create(user, user);
            registeredUsers.add(user);
        }

        List<Following> filipsFollowers = new ArrayList<>();

        for (int i = 0; i < registeredUsers.size() / 2; i++) {
            User follower = registeredUsers.get(i);
            Following following = new Following();
            following.setTarget(userFilip);
            following.setCreator(follower);
            following = followingService.create(following, follower);
            filipsFollowers.add(following);
        }

        for (int i = registeredUsers.size() / 2; i < registeredUsers.size(); i++) {
            User followingUser = registeredUsers.get(i);
            Following following = new Following();
            following.setCreator(userFilip);
            following.setTarget(followingUser);
            followingService.create(following, userFilip);
        }

        List<Post> posts = new ArrayList<>();

        boolean isDouble[] = {false, true, false, true, false};

        String titles[] = {"which you like more ?", "what to visit ?", "where to go on erasmus ?", "holidays ?", "Hmmm ?"};

        String possibilities[] = {"blue", "red", "BACastle", "Devin", "Amsterdam", "Berlin", "Sea", "Mountains", "Trump", "Clinton"};

        String urls[] = {"https://static.pexels.com/photos/539/man-person-legs-grass.jpg",
                "https://pixabay.com/static/uploads/photo/2016/04/12/15/50/bratislava-1324684_960_720.jpg",
                "https://pixabay.com/static/uploads/photo/2013/07/08/14/58/castle-143992_960_720.jpg",
                "https://static.pexels.com/photos/26393/pexels-photo-26393-large.jpg",
                "https://static.pexels.com/photos/59040/pexels-photo-59040-large.jpeg",
                "https://pixabay.com/static/uploads/photo/2016/02/21/16/13/rock-1213806_960_720.jpg",
                "https://static.pexels.com/photos/129112/pexels-photo-129112.jpeg"};


        Post filipsPost = null;

        int urlIndex = 0;
        for (int j = 0; j < 100; j++) {

            int i = j % 5;
            urlIndex = urlIndex % urls.length;

            User user = registeredUsers.get(i);

            Post post;

            if (isDouble[i]) {
                post = createVotingShoppingItem(user, null);
                post.getPostFeedbackPossibilities().clear();
                post.getPhotos().get(0).setPath(urls[urlIndex++]);
                post.getPhotos().get(1).setPath(urls[urlIndex++]);
            } else {
                post = createShoppingItem(user, null);
                post.getPostFeedbackPossibilities().clear();
                post.getPhotos().get(0).setPath(urls[urlIndex++]);
            }

//            post.setSecretUrl("generate me");//forcing to generate an url
            post.setAccessType(Post.AccessType.PUBLICC);

            PostFeedbackPossibility possibility1 = new PostFeedbackPossibility();
            possibility1.setName(possibilities[i * 2]);
            possibility1.setPost(post);

            PostFeedbackPossibility possibility2 = new PostFeedbackPossibility();
            possibility2.setName(possibilities[i * 2 + 1]);
            possibility2.setPost(post);

            post.getPostFeedbackPossibilities().add(possibility1);
            post.getPostFeedbackPossibilities().add(possibility2);

            post.setDescription(titles[i] + "_ " + j);

            if (i == 1) {
                post.setOwner(userFilip);
                post = postService.create(post, userFilip);
            } else {
                post = postService.create(post, user);
            }

            posts.add(post);

        }

        Post postToVote = posts.get(0);

//        for (int i = 0; i < 40; i++) {
//            String name = "name_" + i;
//            User user = createAnonymUser(postToVote.getSecretUrl(), name);
//
//            user = userService.create(user, user);
//
//            user.setSecretUrl(postToVote.getSecretUrl());
//            user.setUserRole(User.UserRole.USER_WITH_LINK);
//
//            PostFeedbackVote postFeedbackVote = new PostFeedbackVote();
//            postFeedbackVote.setPostFeedbackPossibility(postToVote.getPostFeedbackPossibilities().get(0));
//            postFeedbackVote.setOwner(user);
//
//            Comment comment = new Comment();
//            comment.setOwner(user);
//            comment.setPost(postToVote);
//            comment.setContent("this is beautiful, I am voting for " + postToVote.getPostFeedbackPossibilities().get(0).getName());
//
//            postFeedbackVote = postFeedbackVoteService.create(postFeedbackVote, user);
//
//            comment = commentService.create(comment, user);
//        }

    }

    public static Post createShoppingItem(User owner, List<User> viewers) {
        Post.Timer timer = new Post.Timer(System.currentTimeMillis(), 3600 * 1000);
        PostPhoto photo = new PostPhoto("https://pbs.twimg.com/media/CFJ_k30WYAAPd4Z.jpg", System.currentTimeMillis());

        Post post = new Post();

        List<PostPhoto> photos = new ArrayList<>();
        photo.setPost(post);
        photos.add(photo);

        PostFeedbackPossibility possibility1 = new PostFeedbackPossibility();
        possibility1.setPost(post);
        possibility1.setName("like");

        PostFeedbackPossibility possibility2 = new PostFeedbackPossibility();
        possibility2.setPost(post);
        possibility2.setName("love");

        List<PostFeedbackPossibility> postFeedbackPossibilities = new ArrayList<>();
        postFeedbackPossibilities.add(possibility1);
        postFeedbackPossibilities.add(possibility2);

        post.setPostFeedbackPossibilities(postFeedbackPossibilities);

        post.setOwner(owner);
        post.setAccessType(Post.AccessType.SPECIFIC);
        post.setPhotos(photos);
        post.setTimer(timer);
        post.setDescription("some desc");

        GoogleLocation location = new GoogleLocation();
        location.setGoogleID("ChIJhT-9kj-JbEcRFjPcvf5V47s");
        location.setLatitude(48.14050109999999);
        location.setLongitude(17.1213257);

        post.setLocation(null);

        if (viewers != null) {
            post.setViewers(viewers);
        }

        return post;
    }

    public static Post createVotingShoppingItem(User owner, List<User> viewers) {
        Post post = createShoppingItem(owner, viewers);

        List<PostPhoto> photos = new ArrayList<>();
        PostPhoto photo = new PostPhoto("http://g02.a.alicdn.com/kf/HTB1dkhpKVXXXXcrXFXXq6xXFXXXp/Red-Black-font-b-Grey-b-font-Women-Autumn-Winter-Three-Quarter-Sleeve-Pleated-Party-font.jpg", System.currentTimeMillis());
        photo.setPost(post);
        PostPhoto photo2 = new PostPhoto("http://www.stylishwife.com/wp-content/uploads/2015/04/Real-Women-Outfits-No-Models-to-Try-This-Year-15.jpg", System.currentTimeMillis());
        photo2.setPost(post);

        photos.add(photo);
        photos.add(photo2);

        post.setPhotos(photos);

        return post;
    }


//    /**
//     * Precondition: secretUrl must be associated with a post.
//     *
//     * @param secretUrl
//     * @param username
//     * @return
//     */
//    public static User createAnonymUser(String secretUrl, String username) {
//
//        UserDTO userDTO = new UserDTO();
//        userDTO.username = username;
//        userDTO.email = username + "anonym@mail.com";
//        userDTO.profilePhotoURL = "https://pixel.nymag.com/imgs/fashion/daily/2016/04/06/06-emily-ratajkowski.w330.h412.jpg";
//
//        ExternalUserDTO externalUserDTO = new ExternalUserDTO();
//        externalUserDTO.accountProvider = ExternalAccount.ExternalAccountProvider.ANONYM_NAME;
//        externalUserDTO.id = secretUrl + "_" + username;
//
//        ExternalAccount externalAccount = new ExternalAccount(externalUserDTO);
//
//        User user = new User(userDTO);
//        user.getExternalAccounts().add(externalAccount);
//        externalAccount.setAppUser(user);
//
//        return user;
//
//    }

}
