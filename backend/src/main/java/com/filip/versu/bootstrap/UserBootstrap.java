package com.filip.versu.bootstrap;

import com.filip.versu.entity.model.*;
import com.filip.versu.repository.CommentRepository;
import com.filip.versu.service.*;
import com.filip.versu.service.impl.NotificationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class UserBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    private final static Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);


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

        List<String> profiles = Arrays.asList(environment.getActiveProfiles());


        commentRepository.setNamesToUtf8Mb4();//this is a hack to set name, otherwise emojis cannot be saved

        if (ddlProperty.contains("create") && !profiles.contains("test")) {
            init();
        }

    }


    private void init() {

        logger.info("Initializing DB with test data");

        List<User> registeredUsers = new ArrayList<>();

        String usernames[] = {"Emily", "Alex", "Victoria"};
        String quotes[] = {"Stydying...", "If you can dream it you can do it", "Fashion expert"};
        String profilePhotos[] = { "https://images.pexels.com/photos/1130626/pexels-photo-1130626.jpeg?auto=format%2Ccompress&cs=tinysrgb&dpr=2&h=750&w=1260",
                                    "https://images.pexels.com/photos/1121796/pexels-photo-1121796.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260",
                                    "https://images.pexels.com/photos/1892994/pexels-photo-1892994.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"};

        int i = 0;
        for (String username : usernames) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(username.toLowerCase().replaceAll(" ", "") + "@some-emailaddress.com");
            user.setPassword("123456789");
            user.setProfilePhotoURL(profilePhotos[i]);
            user.setQuote(quotes[i++]);

            user = userService.create(user, user);
            registeredUsers.add(user);
        }

        for (i = 0; i < registeredUsers.size(); i++) {
            for (int j = 0; j < registeredUsers.size(); j++) {
                if (i != j) {
                    Following following = new Following();
                    following.setCreator(registeredUsers.get(i));
                    following.setTarget(registeredUsers.get(j));
                    followingService.create(following, registeredUsers.get(i));
                }
            }
        }

        List<Post> posts = new ArrayList<>();

        boolean isDouble[] = {false, true, false, true, false};

        Post erasmusPost = this.createErasmusPost(registeredUsers.get(0));
        Post stanleyCupPost = this.createStanleyPost(registeredUsers.get(1));
        Post fashionPost = this.createFashionPost(registeredUsers.get(2));

        PostFeedbackVote vote1 = new PostFeedbackVote();
        vote1.setPostFeedbackPossibility(erasmusPost.getPostFeedbackPossibilities().get(1));
        vote1.setOwner(registeredUsers.get(1));
        vote1 = postFeedbackVoteService.create(vote1, registeredUsers.get(1));

        PostFeedbackVote vote2 = new PostFeedbackVote();
        vote2.setPostFeedbackPossibility(erasmusPost.getPostFeedbackPossibilities().get(1));
        vote2.setOwner(registeredUsers.get(2));
        vote2 = postFeedbackVoteService.create(vote2, registeredUsers.get(2));


        Comment comment = new Comment();
        comment.setContent("I am voting for Berlin!");
        comment.setPost(erasmusPost);
        comment.setOwner(registeredUsers.get(1));

        comment = commentService.create(comment, registeredUsers.get(1));

        Comment comment1 = new Comment();
        comment1.setContent("Thank you for your feedback!");
        comment1.setPost(erasmusPost);
        comment1.setOwner(erasmusPost.getOwner());

        comment1 = commentService.create(comment1, registeredUsers.get(0));

    }

    private Post createErasmusPost(User owner) {

        Post erasmusPost = new Post();
        erasmusPost.setOwner(owner);
        erasmusPost.setAccessType(Post.AccessType.FOLLOWERS);
        erasmusPost.setDescription("Where to go on erasmus ?");
        PostPhoto postPhoto = new PostPhoto();
        postPhoto.setPost(erasmusPost);
        postPhoto.setPath("https://static.pexels.com/photos/26393/pexels-photo-26393-large.jpg");
        List<PostPhoto> postPhotos = new ArrayList<>();
        postPhotos.add(postPhoto);
        erasmusPost.setPhotos(postPhotos);
        PostFeedbackPossibility possibility1 = new PostFeedbackPossibility();
        possibility1.setPost(erasmusPost);
        possibility1.setName("Amsterdam");
        PostFeedbackPossibility possibility2 = new PostFeedbackPossibility();
        possibility2.setPost(erasmusPost);
        possibility2.setName("Berlin");
        List<PostFeedbackPossibility> possibilities = new ArrayList<>();
        possibilities.add(possibility1);
        possibilities.add(possibility2);
        erasmusPost.setPostFeedbackPossibilities(possibilities);
        erasmusPost = postService.create(erasmusPost, owner);

        return erasmusPost;
    }

    private Post createStanleyPost(User owner) {

        Post stanleyCupPost = new Post();
        stanleyCupPost.setOwner(owner);
        stanleyCupPost.setAccessType(Post.AccessType.PUBLICC);
        stanleyCupPost.setDescription("Who will win the Stanley cup in 2019 ?");
        PostPhoto charaPhoto = new PostPhoto();
        charaPhoto.setPost(stanleyCupPost);
        charaPhoto.setPath("https://s3.eu-central-1.amazonaws.com/versu-app/1_1561365618528_0");
        List<PostPhoto> postPhotos = new ArrayList<>();
        postPhotos.add(charaPhoto);
        stanleyCupPost.setPhotos(postPhotos);

        PostFeedbackPossibility possibility1 = new PostFeedbackPossibility();
        possibility1.setPost(stanleyCupPost);
        possibility1.setName("Bruins");
        PostFeedbackPossibility possibility2 = new PostFeedbackPossibility();
        possibility2.setPost(stanleyCupPost);
        possibility2.setName("Blues");
        List<PostFeedbackPossibility> possibilities = new ArrayList<>();
        possibilities.add(possibility1);
        possibilities.add(possibility2);
        stanleyCupPost.setPostFeedbackPossibilities(possibilities);

        stanleyCupPost = postService.create(stanleyCupPost, owner);

        return stanleyCupPost;
    }

    private Post createFashionPost(User owner) {

        Post fashionPost = new Post();
        fashionPost.setOwner(owner);
        fashionPost.setAccessType(Post.AccessType.FOLLOWERS);
        fashionPost.setDescription("What to wear on first day at work ?");
        PostPhoto photo1 = new PostPhoto();
        photo1.setPost(fashionPost);
        photo1.setPath("https://image.dhgate.com/0x0/f2/albu/g1/M01/51/A5/rBVaGFVfXd-AJQF_AAFxSWL_Fx0684.jpg");
        photo1.setPost(fashionPost);
        PostPhoto photo2 = new PostPhoto();
        photo2.setPost(fashionPost);
        photo2.setPath("https://highfashionusa.com/wp-content/uploads/2018/03/8301-9vfdzv.jpg");
        List<PostPhoto> postPhotos = new ArrayList<>();
        postPhotos.add(photo1);
        postPhotos.add(photo2);

        fashionPost.setPhotos(postPhotos);

        PostFeedbackPossibility possibility1 = new PostFeedbackPossibility();
        possibility1.setPost(fashionPost);
        possibility1.setName("Black");
        PostFeedbackPossibility possibility2 = new PostFeedbackPossibility();
        possibility2.setPost(fashionPost);
        possibility2.setName("Navy");
        List<PostFeedbackPossibility> possibilities = new ArrayList<>();
        possibilities.add(possibility1);
        possibilities.add(possibility2);
        fashionPost.setPostFeedbackPossibilities(possibilities);

        fashionPost = postService.create(fashionPost, owner);

        return fashionPost;
    }

}
