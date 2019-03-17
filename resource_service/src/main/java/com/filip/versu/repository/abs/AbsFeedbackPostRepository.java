package com.filip.versu.repository.abs;


import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.abs.AbsFeedbackPost;
import com.filip.versu.entity.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface AbsFeedbackPostRepository<K extends AbsFeedbackPost> extends JpaRepository<K, Long> {

    public K findOneByOwnerAndPost(User owner, Post post);

    public Page<K> findByPostOrderByTimestampDesc(Post post, Pageable pageable);

    @Query("select fa from #{#entityName} fa where fa.post = :post and fa.id < :lastId order by fa.timestamp desc")
    public Page<K> findByPostPaging(@Param("post") Post post, @Param("lastId") Long lastId, Pageable pageable);

    public Page<K> findByOwnerOrderByTimestampDesc(User owner, Pageable pageable);

    @Query("select fa from #{#entityName} fa where fa.owner = :owner and fa.id < :lastId order by fa.timestamp desc")
    public Page<K> findByOwnerPaging(@Param("owner") User owner, @Param("lastId") Long lastId, Pageable pageable);

    public Long countByPost(Post post);

    @Modifying
    @Query("update #{#entityName} fa set fa.isDeleted=true where fa.post = :post")
    public void markAsDeletedByPost(@Param("post") Post post);

    @Modifying
    @Query("update #{#entityName} fa set fa.isDeleted=true where fa.owner = :owner")
    public void markAsDeletedByCreator(@Param("owner") User owner);


}
