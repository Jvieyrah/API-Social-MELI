package com.meli.social.user.inter;

import com.meli.social.user.model.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFollowJpaRepository extends JpaRepository<UserFollow, Long> {

    @Query("""
        SELECT COUNT(uf) > 0 
        FROM UserFollow uf 
        WHERE uf.follower.userId = :followerId 
        AND uf.followed.userId = :followedId
        """)
    boolean existsFollow(@Param("followerId") Integer followerId, @Param("followedId") Integer followedId);

    @Modifying
    @Query("""
        DELETE FROM UserFollow uf 
        WHERE uf.follower.userId = :followerId 
        AND uf.followed.userId = :followedId
        """)
    int deleteFollow(@Param("followerId") Integer followerId, @Param("followedId") Integer followedId);
}
