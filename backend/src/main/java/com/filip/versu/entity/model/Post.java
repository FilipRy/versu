package com.filip.versu.entity.model;


import com.filip.versu.entity.dto.PostDTO;
import com.filip.versu.entity.dto.PostFeedbackPossibilityDTO;
import com.filip.versu.entity.dto.PostPhotoDTO;
import com.filip.versu.entity.dto.UserDTO;
import com.filip.versu.entity.model.abs.AbsBaseEntityWithOwner;
import com.filip.versu.repository.DBHelper;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = DBHelper.TablesNames.POST)
@Where(clause = "is_deleted=0")
public class Post extends AbsBaseEntityWithOwner<Long> {

    /**
     * This enum specifies the access type to the shopping item.
     * The ONLY_OWNER type = only creator of the shopping item can see this shopping item.
     * The FOLLOWERS type = each follower can view the shopping item.
     * the PUBLICC type = each user can view the shopping item.
     * The SPECIFIC type = only the SPECIFIC persons (@viewers) can view this post
     */
    public static enum AccessType {
        ONLY_OWNER(1), FOLLOWERS(2), PUBLICC(3), SPECIFIC(4);

        private final int value;

        private AccessType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @Getter
    @Setter
    private String description;

    /**
     * The UTC time when the shopping item was published.
     */
    @Getter
    @Setter
    private long publishTime;

    /**
     * This is a location of this post.
     */
    @Getter
    @Setter
    @ManyToOne
    private GoogleLocation location;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "post", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @Getter
    @Setter
    private List<PostPhoto> photos;

    @Getter
    @Setter
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "post", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<PostFeedbackPossibility> postFeedbackPossibilities;

    @Getter
    @Setter
    @OneToOne
    private PostFeedbackPossibility chosenFeedbackPossibility;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    @Getter
    @Setter
    private List<Comment> comments = new ArrayList<>();


    @Transient
    @Getter
    @Setter
    private PostFeedbackVote myPostFeedbackVote;


    @Transient
    @Getter
    @Setter
    private List<Comment> mostRecentComment = new ArrayList<>();

    @Getter
    @Setter
    @Enumerated
    private AccessType accessType;

    @ManyToMany(fetch = FetchType.LAZY)
    @Getter
    @Setter
    private List<User> viewers = new ArrayList<>();

    /**
     * This is an url, which can be used to share this post outside the app.
     */
    @Getter
    @Setter
    private String secretUrl;

    @Getter
    @Setter
    private boolean isDeleted;


    public Post() {
        super();
    }

    public Post(PostDTO other) {
        super(other);
        this.description = other.description;
        this.publishTime = other.publishTime;
        this.accessType = other.accessType;
        this.secretUrl = other.secretUrl;

        if(other.location != null) {
            this.location = new GoogleLocation(other.location);
        }

        if(other.viewers != null) {
            viewers = new ArrayList<>();
            for(UserDTO userDTO: other.viewers) {
                viewers.add(new User(userDTO));
            }
        }
        if(other.photos != null) {
            photos = new ArrayList<>();
            for(PostPhotoDTO postPhotoDTO : other.photos) {
                PostPhoto photo = new PostPhoto(postPhotoDTO);
                photo.setPost(this);//setting reference to this post (to avoid stackoverflow in ShoppingItem -> PostPhoto -> ShoppingItem ...)
                photos.add(photo);
            }
        }

        this.postFeedbackPossibilities = new ArrayList<>();
        //adding possibilities for voting
        for(PostFeedbackPossibilityDTO postFeedbackPossibilityDTO: other.postFeedbackPossibilities) {
            PostFeedbackPossibility postFeedbackPossibility = new PostFeedbackPossibility(postFeedbackPossibilityDTO, true);
            postFeedbackPossibility.setPost(this);

            this.postFeedbackPossibilities.add(postFeedbackPossibility);
        }


        if(other.chosenFeedbackPossibility != null) {
            chosenFeedbackPossibility = new PostFeedbackPossibility(other.chosenFeedbackPossibility, true);
            chosenFeedbackPossibility.setPost(this);
        }


    }

    /**
     * The viewers & feedback actions are removed from equals because they are lazy loaded.
     * @param o
    * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Post that = (Post) o;

        if (publishTime != that.publishTime) return false;
        if (isDeleted != that.isDeleted) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if(photos != null) {
            if(that.photos != null) {
                for(PostPhoto photo: photos) {
                    boolean found = false;
                    for(PostPhoto thatPhoto : that.photos) {
                        if(photo.equals(thatPhoto)) {
                            found = true;
                        }
                    }
                    if(!found) return false;
                }
                for(PostPhoto thatPhoto: that.photos) {
                    boolean found = false;
                    for(PostPhoto photo: photos) {
                        if(thatPhoto.equals(photo)) {
                            found = true;
                        }
                    }
                    if(!found) return false;
                }
            }
            else {
                return false;
            }
        } else if (that.photos != null) {
            return false;
        }
        return accessType == that.accessType;

    }

}
