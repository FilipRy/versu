package com.filip.versu.dto;


import com.filip.versu.entity.dto.PostDTO;
import com.filip.versu.entity.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.entity.dto.PostFeedbackVoteDTO;
import com.filip.versu.entity.dto.PostPhotoDTO;
import com.filip.versu.service.PostServiceTest;
import com.filip.versu.service.UserServiceTest;
import com.filip.versu.entity.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;


public class PostDTOTest {


//    @Test
    public void test_Post_shouldOK() {

        User owner = UserServiceTest.createUser("owner");
        User viewer = UserServiceTest.createUser("viewer1");

        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);

        Post post = PostServiceTest.createPost(owner, viewers);

        PostFeedbackVote postFeedbackVote = new PostFeedbackVote();
        postFeedbackVote.setPostFeedbackPossibility(post.getPostFeedbackPossibilities().get(0));
        postFeedbackVote.setOwner(owner);

        post.setMyPostFeedbackVote(postFeedbackVote);
        post.setChosenFeedbackPossibility(post.getPostFeedbackPossibilities().get(0));

        //creating DTO from model
        PostDTO postDTO = new PostDTO(post);

        for(PostPhotoDTO postPhotoDTO: postDTO.photos) {
            assertTrue(postPhotoDTO.post == null);//photos don't have a back reference
        }

        for (PostFeedbackPossibilityDTO possibility: postDTO.postFeedbackPossibilities) {
            assertTrue(possibility.postDTO == null);//no back reference to post
        }

        assertTrue(postDTO.myPostFeedback != null);
        assertTrue(postDTO.myPostFeedback.feedbackPossibilityDTO != null);
        assertTrue(postDTO.myPostFeedback.feedbackPossibilityDTO.name.equals(postFeedbackVote.getPostFeedbackPossibility().getName()));
        assertTrue(postDTO.myPostFeedback.feedbackPossibilityDTO.postDTO == null);//no back reference to post


        //create model from DTO
        Post postModel = new Post(postDTO);

        checkPostModel(postModel);

        assertTrue(postModel.getChosenFeedbackPossibility().getPost() == postModel);

    }

//    @Test
    public void test_PostFeedback_shouldOK () {

        User owner = UserServiceTest.createUser("owner");
        User viewer = UserServiceTest.createUser("viewer1");

        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);

        Post post = PostServiceTest.createPost(owner, viewers);

        PostDTO postDTO = new PostDTO(post);
        postDTO.postFeedbackPossibilities.get(0).postDTO = postDTO;//this must be done in front end.


        //creating vote DTO
        PostFeedbackVoteDTO postFeedbackVoteDTO = new PostFeedbackVoteDTO();
        postFeedbackVoteDTO.feedbackPossibilityDTO = postDTO.postFeedbackPossibilities.get(0);
        postFeedbackVoteDTO.owner = postDTO.owner;

        PostFeedbackVote postFeedbackVote = new PostFeedbackVote(postFeedbackVoteDTO);

        Post votedOnPost = postFeedbackVote.getPostFeedbackPossibility().getPost();

        checkPostModel(votedOnPost);

        //vote's possibility is the same as one of post's possibilities
        boolean isSame = false;
        for(PostFeedbackPossibility possibility: votedOnPost.getPostFeedbackPossibilities()) {
            if(possibility.equals(postFeedbackVote.getPostFeedbackPossibility())) {
                isSame = true;
            }
        }

        assertTrue(isSame);


    }

    private void checkPostModel(Post postModel) {
        for(PostPhoto postPhoto: postModel.getPhotos()) {
            assertTrue(postPhoto.getPost() == postModel);
        }

        for(PostFeedbackPossibility possibility: postModel.getPostFeedbackPossibilities()) {
            assertTrue(possibility.getPost() == postModel);
        }
    }

}
