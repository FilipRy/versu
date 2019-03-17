package com.filip.versu.repository;


import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    public static final String POSSIBLITIES_SEPARATOR = "VS";


    public static final String POST_VISIBILITY_QUERY = "((p.accessType = Post$AccessType.specific and :viewer member of p.viewers)" +
            " or (p.accessType = Post$AccessType.followers and exists(select f from Following f where f.creator = :viewer and f.target = p.owner))" +
            " or (p.accessType = Post$AccessType.onlyOwner and p.owner = :viewer)" +
            " or (p.accessType = Post$AccessType.publicc)" +
            " or p.owner = :viewer)";

    public static final String POST_VISIBILITY_TIMELINE_QUERY = "((p.accessType = Post$AccessType.specific and :viewer member of p.viewers)" +
            " or ((p.accessType = Post$AccessType.followers or p.accessType = Post$AccessType.publicc) and exists(select f from Following f where f.creator = :viewer and f.target = p.owner))" +
            " or (p.accessType = Post$AccessType.onlyOwner and p.owner = :viewer)" +
            " or p.owner = :viewer)";


    /**
     * This method is used to fill timeline feed.
     *
     * @param viewer
     * @param pageable
     * @return
     */
    @Query("select p from Post p where " + POST_VISIBILITY_TIMELINE_QUERY + " and p.chosenFeedbackPossibility=null order by p.publishTime desc")
    public Page<Post> findActiveForUserByTime(@Param("viewer") User viewer, Pageable pageable);


    @Query("select p from Post p where " + POST_VISIBILITY_TIMELINE_QUERY + " and p.chosenFeedbackPossibility=null and p.id < :lastId order by p.publishTime desc")
    public Page<Post> findActiveForUserByTimePaging(@Param("viewer") User viewer, @Param("lastId") Long lastLoadedId, Pageable pageable);

    /**
     * This method is used to fill nearby feed.
     * <p>
     * SELECT id, ( 6371 * acos( cos( radians(37) ) * cos( radians( lat ) ) * cos( radians( lng ) - radians(-122) ) + sin( radians(37) ) * sin( radians( lat ) ) ) ) AS distance FROM markers HAVING distance < 25 ORDER BY distance LIMIT 0 , 20;
     *
     * @param viewer
     * @param pageable
     * @return
     */
    @Query("select p from Post p where " +
            "( 6371 * acos( cos( radians(:lat) ) * cos( radians( p.location.latitude ) ) * cos( radians( p.location.longitude ) - radians(:lng) ) + sin( radians(:lat) ) * sin( radians( p.location.latitude ) ) ) ) < :dis and " +
            "p.location.latitude <> -300 and p.location.longitude <> -300 and " + POST_VISIBILITY_QUERY + " and " +
            "p.chosenFeedbackPossibility = null order by " +
            "( 6371 * acos( cos( radians(:lat) ) * cos( radians( p.location.latitude ) ) * cos( radians( p.location.longitude ) - radians(:lng) ) + sin( radians(:lat) ) * sin( radians( p.location.latitude ) ) ) ) asc")
    public Page<Post> findActiveForUserByLocation(@Param("viewer") User viewer, @Param("lat") double lat, @Param("lng") double lng,
                                                  @Param("dis") double distance, Pageable pageable);


    /**
     * This query returns the posts which are visible for @viewer and owned by @owner. (e.g. if @viewer is viewing profile of @owner)
     *
     * @param viewer
     * @param owner
     * @param pageable
     * @return
     */
    @Query("select p from Post p where p.owner = :owner and " + POST_VISIBILITY_QUERY + " order by p.publishTime desc")
    public Page<Post> findPostsVisibleForViewer(@Param("viewer") User viewer,
                                                @Param("owner") User owner,
                                                Pageable pageable);


    /**
     * This query returns the posts which are visible for @viewer and owned by @owner. (e.g. if @viewer is viewing profile of @owner)
     *
     * @param viewer
     * @param owner
     * @param pageable
     * @return
     */
    @Query("select p from Post p where p.owner = :owner and p.id < :lastId and " + POST_VISIBILITY_QUERY + " order by p.publishTime desc")
    public Page<Post> findPostsVisibleForViewerPaging(@Param("viewer") User viewer,
                                                      @Param("owner") User owner,
                                                      @Param("lastId") Long lastLoadedId,
                                                        Pageable pageable);



    @Query("select case when count(p) > 0 then true else false end from Post p where p.id = :id and " + POST_VISIBILITY_QUERY)
    public boolean isUserTheViewerOfPost(@Param("id") Long postID, @Param("viewer") User viewer);


    public Page<Post> findByOwnerOrderByPublishTimeDesc(User owner, Pageable pageable);


    /**
     * This method return possibilities in lowercase, which have already been used at posts.
     *
     * @param pattern - pattern for possibilities, smtng in form of: likeVS, or poss1 or likeVSlove ...
     * @return
     */
    @Query(value = "SELECT distinct " +
            "possibility " +
            "FROM (" +
            "       SELECT" +
            "         GROUP_CONCAT(DISTINCT lower(name) ORDER BY name ASC SEPARATOR" +
            "                      '" + POSSIBLITIES_SEPARATOR + "') AS possibility," +
            "         GROUP_CONCAT(DISTINCT lower(name) ORDER BY name DESC SEPARATOR" +
            "                      '" + POSSIBLITIES_SEPARATOR + "') AS possibilityB" +
            "       FROM post_feedback_possibility WHERE is_deleted=0" +
            "       GROUP BY post_id" +
            "     ) AS innertable WHERE possibility LIKE lower( :pattern ) or possibilityB LIKE lower( :pattern ) limit 20", nativeQuery = true)
    public List<String> searchVSPossibilitiesLike(@Param("pattern") String pattern);


    @Query("select distinct p from PostFeedbackPossibility po INNER JOIN po.post p " +
            "where (upper(po.name) = upper(:possA) or exists (select poB from PostFeedbackPossibility poB where upper(poB.name) = upper(:possB) and poB.post = po.post)) " +
            "and "+ POST_VISIBILITY_QUERY +" " +
            "order by (CASE WHEN upper(po.name) = upper(:possA) and exists (select poB from PostFeedbackPossibility poB where upper(poB.name) = upper(:possB) and poB.post = po.post) THEN 2 " +
            "WHEN upper(po.name) = upper(:possA) or exists (select poB from PostFeedbackPossibility poB where upper(poB.name) = upper(:possB) and poB.post = po.post) THEN 1 ELSE 0 END) desc, p.publishTime desc")
    public Page<Post> findPostsByFeedbackPossibilitesName(@Param("possA") String possA,
                                                          @Param("possB") String possB,
                                                          @Param("viewer") User viewer,
                                                          Pageable pageable);


    @Query("select p from Post p where (p.location.googleID = :googleID or p.location.cityGoogleId = :googleID) and " + POST_VISIBILITY_QUERY + " order by p.publishTime desc")
    public Page<Post> findByLocationGoogleId(@Param("googleID") String googleID, @Param("viewer") User viewer, Pageable pageable);

    @Query("select p from Post p where (p.location.googleID = :googleID or p.location.cityGoogleId = :googleID) and p.id < :lastId and " + POST_VISIBILITY_QUERY + " order by p.publishTime desc")
    public Page<Post> findByLocationGoogleIdPaging(@Param("googleID") String googleID, @Param("lastId") Long lastLoadedId, @Param("viewer") User viewer, Pageable pageable);

    public Post findOneBySecretUrl(String secretUrl);

}
