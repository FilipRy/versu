package com.filip.versu.service;

import com.filip.versu.entity.model.Comment;
import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.User;
import com.filip.versu.exception.EntityNotExistsException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Filip on 2/14/2016.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Test
    public void test_createComment_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);
        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);

        Post post = PostServiceTest.createPost(owner, viewers);
        post = postService.create(post, owner);

        Comment comment = new Comment();
        comment.setOwner(viewer);
        comment.setPost(post);
        comment.setContent("some good content");

        comment = commentService.create(comment, viewer);

        Comment getComment = commentService.get(comment.getId());
        assertTrue(comment.equals(getComment));

    }

    /**
     * This method shows, that its possible to create a comment on shopping item, which was updated in meantime.
     */
    @Test
    public void test_createCommentShoppingItemWasUpdated_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);
        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);

        Post post = PostServiceTest.createPost(owner, viewers);
        post = postService.create(post, owner);

        Comment comment = new Comment();
        comment.setOwner(viewer);
        comment.setPost(post);
        comment.setContent("some good content");

        Post postCopy = PostServiceTest.createPost(owner, viewers);
        postCopy.setId(post.getId());
        postCopy.setPhotos(post.getPhotos());

        postCopy.setDescription("some new desc");
        postCopy = postService.update(postCopy, owner);

        comment = commentService.create(comment, viewer);

        Comment getComment = commentService.get(comment.getId());

        /**
         * comment was created on updated shopping item
         */
        assertTrue(getComment.getPost().equals(postCopy));
        comment.setPost(postCopy);

        assertTrue(comment.equals(getComment));
    }

    @Test(expected = EntityNotExistsException.class)
    public void test_createCommentOnNonExistingShoppingItem_shouldThrowEntityNotExistsException() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);
        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);

        Post post = PostServiceTest.createPost(owner, viewers);

        Comment comment = new Comment();
        comment.setOwner(viewer);
        comment.setPost(post);;
        comment.setContent("some good content");

        /**
         * the exception should be thrown here
         */
        commentService.create(comment, viewer);
    }


    @Test
    public void test_listByPostReversePaging_testPaging_shouldOK() {


        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);

        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);

        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);

        Post post = PostServiceTest.createPost(owner, viewers);
        post = postService.create(post, owner);

        List<Comment> commentList = new ArrayList<>();

        int commentize = 10;

        for(int i = 0; i < 10; i++) {
            Comment comment = new Comment();
            comment.setOwner(viewer);
            comment.setPost(post);
            comment.setContent("some good content "+i);
            comment = commentService.create(comment, viewer);

            commentList.add(comment);
        }

        int pageSize = 5;

        Page<Comment> commentPage = commentService.listByPostReversePaging(post.getId(), null, new PageRequest(0, pageSize), viewer);

        assertTrue(commentPage.getContent().size() == pageSize);

        //the returned comments should be sorted by id from the youngest to the oldest
        for (int i = 0; i < pageSize - 1; i++) {
            assertTrue(commentPage.getContent().get(i).getId() < commentPage.getContent().get(i+1).getId());
        }

        Comment firstComment = commentPage.getContent().get(0);

        commentPage = commentService.listByPostReversePaging(post.getId(), firstComment.getId(), new PageRequest(0, 2), viewer);

        assertTrue(commentPage.getContent().get(1).getId() == firstComment.getId() - 1);//the new returned page contains comments right in from of the firstComment
        assertTrue(commentPage.getContent().get(0).getId() < commentPage.getContent().get(1).getId());


    }

    @Test
    public void test_listCommentByShoppingItem_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        User viewer2 = UserServiceTest.createUser("viewer2");
        viewer = userService.create(viewer, viewer);
        viewer2 = userService.create(viewer2, viewer2);
        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);
        viewers.add(viewer2);

        Post post = PostServiceTest.createPost(owner, viewers);
        post = postService.create(post, owner);

        Comment comment = new Comment();
        comment.setOwner(viewer);
        comment.setPost(post);
        comment.setContent("some good content");

        comment = commentService.create(comment, viewer);

        Comment comment2 = new Comment();
        comment2.setOwner(viewer);
        comment2.setPost(post);
        comment2.setContent("some good content");

        comment2 = commentService.create(comment2, viewer);

        Page<Comment> comments = commentService.listByPostReversePaging(post.getId(), null, new PageRequest(0, 20), viewer);
        assertTrue(comments.getContent().contains(comment));
        assertTrue(comments.getContent().contains(comment2));

    }

    @Test(expected = EntityNotExistsException.class)
    public void test_listCommentsOnDeletedShoppingItem_shouldReturnEmptyList() {

        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        User viewer2 = UserServiceTest.createUser("viewer2");
        viewer = userService.create(viewer, viewer);
        viewer2 = userService.create(viewer2, viewer2);
        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);
        viewers.add(viewer2);

        Post post = PostServiceTest.createPost(owner, viewers);
        post = postService.create(post, owner);

        Comment comment = new Comment();
        comment.setOwner(viewer);
        comment.setPost(post);;
        comment.setContent("some good content");

        comment = commentService.create(comment, viewer);

        postService.delete(post.getId(), owner);

        /**
         * This should throw EntityNotExistsException, because the ShoppingItem was deleted.
         */
        Page<Comment> comments = commentService.listByPostReversePaging(post.getId(), null, new PageRequest(0, 20), owner);

        assertTrue(comments.getContent().size() == 0);

    }

    @Test
    public void test_countCommentByShoppingItem_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        User viewer2 = UserServiceTest.createUser("viewer2");
        viewer = userService.create(viewer, viewer);
        viewer2 = userService.create(viewer2, viewer2);
        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);
        viewers.add(viewer2);

        Post post = PostServiceTest.createPost(owner, viewers);
        post = postService.create(post, owner);

        Comment comment = new Comment();
        comment.setOwner(viewer);
        comment.setPost(post);;
        comment.setContent("some good content");

        comment = commentService.create(comment, viewer);

        Comment comment2 = new Comment();
        comment2.setOwner(viewer);
        comment2.setPost(post);
        comment2.setContent("some good content");

        comment2 = commentService.create(comment2, viewer);

        assertTrue(commentService.countAtPost(post) == 2);
    }

    @Test
    public void test_countCommentByShoppingItem_OneRemoved_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        User viewer2 = UserServiceTest.createUser("viewer2");
        viewer = userService.create(viewer, viewer);
        viewer2 = userService.create(viewer2, viewer2);
        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);
        viewers.add(viewer2);

        Post post = PostServiceTest.createPost(owner, viewers);
        post = postService.create(post, owner);

        Comment comment = new Comment();
        comment.setOwner(viewer);
        comment.setPost(post);;
        comment.setContent("some good content");

        comment = commentService.create(comment, viewer);

        Comment comment2 = new Comment();
        comment2.setOwner(viewer2);
        comment2.setPost(post);
        comment2.setContent("some good content");

        comment2 = commentService.create(comment2, viewer2);

        /**
         * soft delete of comment2
         */
        commentService.removeOfUser(viewer2, viewer2);

        assertTrue(commentService.countAtPost(post) == 1);
    }

    @Test
    public void test_removeCommentByShoppingItem_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        User viewer2 = UserServiceTest.createUser("viewer2");
        viewer = userService.create(viewer, viewer);
        viewer2 = userService.create(viewer2, viewer2);
        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);
        viewers.add(viewer2);

        Post post = PostServiceTest.createPost(owner, viewers);
        post = postService.create(post, owner);

        Comment comment = new Comment();
        comment.setOwner(viewer);
        comment.setPost(post);;
        comment.setContent("some good content");

        comment = commentService.create(comment, viewer);

        Comment comment2 = new Comment();
        comment2.setOwner(viewer);
        comment2.setPost(post);
        comment2.setContent("some good content");

        comment2 = commentService.create(comment2, viewer);

        commentService.removeOfShoppingItem(post, owner);
        Page<Comment> comments = commentService.listByPostReversePaging(post.getId(), null, new PageRequest(0, 20), viewer);
        assertFalse(comments.getContent().contains(comment));
        assertFalse(comments.getContent().contains(comment2));

    }

    @Test
    public void test_removeCommentByUser_shouldOK() {
        User owner = UserServiceTest.createUser("owner");
        owner = userService.create(owner, owner);
        User viewer = UserServiceTest.createUser("viewer1");
        viewer = userService.create(viewer, viewer);
        List<User> viewers = new ArrayList<>();
        viewers.add(viewer);

        Post post = PostServiceTest.createPost(owner, viewers);
        post = postService.create(post, owner);

        Comment comment = new Comment();
        comment.setOwner(viewer);
        comment.setPost(post);;
        comment.setContent("some good content");

        comment = commentService.create(comment, viewer);

        commentService.removeOfUser(viewer, viewer);

        Comment getComment = commentService.get(comment.getId());
        assertTrue(getComment == null);
    }

}
