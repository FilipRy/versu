package com.filip.versu.repository.abs;


import com.filip.versu.entity.model.Post;
import com.filip.versu.entity.model.User;
import com.filip.versu.entity.model.abs.AbsBaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface AbsFeedbackPostRepository<K extends AbsBaseEntity<Long>> extends JpaRepository<K, Long> {

    public K findOneByOwnerAndPost(User owner, Post post);

    public Page<K> findByPost(Post post, Pageable pageable);

    public Page<K> findByOwner(User owner, Pageable pageable);

    public Long countByPost(Post post);

    @Modifying
    @Query("update #{#entityName} fa set fa.isDeleted=true where fa.post = :post")
    public void markAsDeletedByPost(@Param("post") Post post);

    @Modifying
    @Query("update #{#entityName} fa set fa.isDeleted=true where fa.owner = :owner")
    public void markAsDeletedByCreator(@Param("owner") User owner);


}
