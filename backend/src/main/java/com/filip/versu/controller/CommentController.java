package com.filip.versu.controller;


import com.filip.versu.controller.abs.AbsAuthController;
import com.filip.versu.entity.dto.CommentDTO;
import com.filip.versu.entity.model.Comment;
import com.filip.versu.entity.model.User;
import com.filip.versu.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AbsAuthController.API_URL_PREFIX + "/comment")
public class CommentController extends AbsAuthController<Long, Comment, CommentDTO>  {

    @Autowired
    private CommentService commentService;

    @RequestMapping(method = RequestMethod.POST)
    public CommentDTO create(@RequestBody CommentDTO commentDTO) {

        User requester = authenticateUser();
        validation.validate(commentDTO);
        Comment comment = createModelFromDTO(commentDTO);
        return createDTOFromModel(commentService.create(comment, requester));
    }


//    /**
//     * @param commentDTO
//     * @return
//     */
//    @RequestMapping(method = RequestMethod.POST, value = "/anonym")
//    public CommentDTO createAnonym(@RequestBody CommentDTO commentDTO) {
//        validation.validate(commentDTO);
//
//        String secretUrl = commentDTO.postDTO.secretUrl;
//        User requester = authenticateUserWithSecretLinkAccess(accessToken, secretUrl);
//
//        Comment comment = createModelFromDTO(commentDTO);
//        return createDTOFromModel(commentService.create(comment, requester));
//    }


    @RequestMapping(method = RequestMethod.GET, value = "/findByUser/{id}")
    public Page<CommentDTO> listByUser(@PathVariable("id") Long id,
                              @RequestParam(value = "lastId", required = false) Long lastLoadedId,
                              Pageable pageable) {
        User requester = authenticateUser();
        Page<Comment> modelPage = commentService.listOfUser(id, lastLoadedId, pageable, requester);
        return mapModelPageToDTOPage(modelPage, pageable);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/findByPost/{id}")
    public Page<CommentDTO> listByPost(@PathVariable("id") Long id,
                              @RequestParam(value = "lastId", required = false) Long lastLoadedId,
                              Pageable pageable) {
        User requester = authenticateUser();
        Page<Comment> modelPage = commentService.listByPostReversePaging(id, lastLoadedId, pageable, requester);
        return mapModelPageToDTOPage(modelPage, pageable);
    }

//    @RequestMapping(method = RequestMethod.GET, value = "/findByPost/{secretUrl}")
//    public Page<CommentDTO> listByPost(@PathVariable("secretUrl") String secretUrl,
//                              @RequestParam(value = "lastId", required = false) Long lastLoadedId,
//                              Pageable pageable) {
//        User requester = authenticateUserWithSecretLinkAccess(accessToken, secretUrl);
//
//        Page<Comment> modelPage = commentService.listByPostReversePaging(secretUrl, lastLoadedId, pageable, requester);
//        return mapModelPageToDTOPage(modelPage, pageable);
//    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public CommentDTO delete(@PathVariable("id") Long id) {
//        User requester = authenticateUserWithSecretLinkAccess(accessToken, null);
        User requester = authenticateUser();
        return createDTOFromModel(commentService.delete(id, requester));
    }

    @Override
    protected CommentDTO createDTOFromModel(Comment model) {
        return new CommentDTO(model, false);
    }

    @Override
    protected Comment createModelFromDTO(CommentDTO dto) {
        return new Comment(dto);
    }
}
