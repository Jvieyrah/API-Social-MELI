package com.meli.social.user.inter;

import com.meli.social.user.impl.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUserName(String userName);

    boolean existsByUserName(String userName);

    @Query("SELECT u.followersCount FROM User u WHERE u.userId = :userId")
    Optional<Integer> getFollowersCountByUserId(@Param("userId") Integer userId);

    @Query("""
        SELECT uf.follower FROM UserFollow uf 
        WHERE uf.followed.userId = :userId
        """)
    List<User> findFollowersByUserId(@Param("userId") Integer userId);

    @Query("""
        SELECT uf.follower FROM UserFollow uf 
        WHERE uf.followed.userId = :userId 
        ORDER BY uf.follower.userName ASC
        """)
    List<User> findFollowersByUserIdOrderByNameAsc(@Param("userId") Integer userId);

    @Query("""
        SELECT uf.follower FROM UserFollow uf 
        WHERE uf.followed.userId = :userId 
        ORDER BY uf.follower.userName DESC
        """)
    List<User> findFollowersByUserIdOrderByNameDesc(@Param("userId") Integer userId);

    @Query("""
        SELECT uf.followed FROM UserFollow uf 
        WHERE uf.follower.userId = :userId
        """)
    List<User> findFollowingByUserId(@Param("userId") Integer userId);

    @Query("""
        SELECT uf.followed FROM UserFollow uf 
        WHERE uf.follower.userId = :userId 
        ORDER BY uf.followed.userName ASC
        """)
    List<User> findFollowingByUserIdOrderByNameAsc(@Param("userId") Integer userId);

    @Query("""
        SELECT uf.followed FROM UserFollow uf 
        WHERE uf.follower.userId = :userId 
        ORDER BY uf.followed.userName DESC
        """)
    List<User> findFollowingByUserIdOrderByNameDesc(@Param("userId") Integer userId);

    List<User> findAllByOrderByUserNameAsc();

    List<User> findAllByOrderByUserNameDesc();

    @Query("SELECT u FROM User u ORDER BY u.followersCount DESC")
    List<User> findTopUsersByFollowersCount();

    List<User> findAllByOrderByFollowersCountDesc(Pageable pageable);

    @Query("""
        SELECT COUNT(uf) > 0 
        FROM UserFollow uf 
        WHERE uf.follower.userId = :followerId 
        AND uf.followed.userId = :followedId
        """)
    boolean isFollowing(@Param("followerId") Integer followerId,
                        @Param("followedId") Integer followedId);


    @Query("""
        SELECT COUNT(uf) > 0 
        FROM UserFollow uf 
        WHERE uf.follower.userId = :followerId 
        AND uf.followed.userId = :followedId
        """)
    boolean existsFollow(@Param("followerId") Integer followerId,
                         @Param("followedId") Integer followedId);

    @Query("""
        SELECT DISTINCT u FROM User u
        LEFT JOIN FETCH u.following uf
        LEFT JOIN FETCH uf.followed
        WHERE u.userId = :userId
        """)
    Optional<User> findByIdWithFollowing(@Param("userId") Integer userId);

    @Query("""
        SELECT DISTINCT u FROM User u
        LEFT JOIN FETCH u.followers uf
        LEFT JOIN FETCH uf.follower
        WHERE u.userId = :userId
        """)
    Optional<User> findByIdWithFollowers(@Param("userId") Integer userId);

    @Query("""
        SELECT DISTINCT u FROM User u
        LEFT JOIN FETCH u.following
        LEFT JOIN FETCH u.followers
        LEFT JOIN FETCH u.posts
        WHERE u.userId = :userId
        """)
    Optional<User> findByIdWithAllRelations(@Param("userId") Integer userId);

    @Query("SELECT DISTINCT u FROM User u WHERE SIZE(u.posts) > 0")
    List<User> findUsersWithPosts();

    List<User> findByUserIdIn(List<Integer> userIds);

    @Query("SELECT u FROM User u WHERE LOWER(u.userName) LIKE LOWER(CONCAT('%', :partialName, '%'))")
    List<User> searchByUserName(@Param("partialName") String partialName);

    List<User> findByUserNameContainingIgnoreCase(String partialName);


    @Query("""
        SELECT COUNT(uf) 
        FROM UserFollow uf 
        WHERE uf.follower.userId = :userId
        """)
    Integer countFollowingByUserId(@Param("userId") Integer userId);

    @Query("""
        SELECT COUNT(uf) 
        FROM UserFollow uf 
        WHERE uf.followed.userId = :userId
        """)
    Integer countFollowersByUserId(@Param("userId") Integer userId);

    @Query("""
        SELECT u FROM User u 
        WHERE NOT EXISTS (
            SELECT 1 FROM UserFollow uf 
            WHERE uf.follower.userId = u.userId
        )
        """)
    List<User> findUsersNotFollowingAnyone();

    @Query("SELECT u FROM User u WHERE u.followersCount = 0")
    List<User> findUsersWithoutFollowers();


    @Modifying
    @Query("UPDATE User u SET u.followersCount = :count WHERE u.userId = :userId")
    void updateFollowersCount(@Param("userId") Integer userId, @Param("count") Integer count);


    @Query("""
        SELECT DISTINCT uf1.followed 
        FROM UserFollow uf1 
        WHERE uf1.follower.userId = :userId 
        AND EXISTS (
            SELECT 1 FROM UserFollow uf2 
            WHERE uf2.follower.userId = uf1.followed.userId 
            AND uf2.followed.userId = :userId
        )
        """)
    List<User> findMutualFollowers(@Param("userId") Integer userId);


    @Query("""
        SELECT DISTINCT uf2.followed 
        FROM UserFollow uf1 
        JOIN UserFollow uf2 ON uf1.followed.userId = uf2.follower.userId
        WHERE uf1.follower.userId = :userId 
        AND uf2.followed.userId <> :userId 
        AND NOT EXISTS (
            SELECT 1 FROM UserFollow uf3 
            WHERE uf3.follower.userId = :userId 
            AND uf3.followed.userId = uf2.followed.userId
        )
        """)
    List<User> findSuggestedUsers(@Param("userId") Integer userId);

    @Query("""
        SELECT DISTINCT uf2.followed 
        FROM UserFollow uf1 
        JOIN UserFollow uf2 ON uf1.followed.userId = uf2.follower.userId
        WHERE uf1.follower.userId = :userId 
        AND uf2.followed.userId <> :userId 
        AND NOT EXISTS (
            SELECT 1 FROM UserFollow uf3 
            WHERE uf3.follower.userId = :userId 
            AND uf3.followed.userId = uf2.followed.userId
        )
        ORDER BY uf2.followed.followersCount DESC
        """)
    List<User> findSuggestedUsersOrderedByPopularity(@Param("userId") Integer userId, Pageable pageable);

    @Query("""
        SELECT u FROM User u 
        WHERE u.userId <> :userId 
        AND NOT EXISTS (
            SELECT 1 FROM UserFollow uf 
            WHERE uf.follower.userId = :userId 
            AND uf.followed.userId = u.userId
        )
        ORDER BY u.followersCount DESC
        """)
    List<User> findPopularUsersNotFollowing(@Param("userId") Integer userId, Pageable pageable);

    @Query("""
        SELECT uf.followed.userId 
        FROM UserFollow uf 
        WHERE uf.follower.userId = :userId
        """)
    List<Integer> findFollowingIdsByUserId(@Param("userId") Integer userId);

    @Query("""
        SELECT uf.follower.userId 
        FROM UserFollow uf 
        WHERE uf.followed.userId = :userId
        """)
    List<Integer> findFollowerIdsByUserId(@Param("userId") Integer userId);

    @Query("""
    SELECT DISTINCT u FROM User u
    LEFT JOIN FETCH u.followers uf1
    LEFT JOIN FETCH uf1.follower
    LEFT JOIN FETCH u.following uf2
    LEFT JOIN FETCH uf2.followed
    WHERE u.userId IN :userIds
    """)
    List<User> findByIdInWithRelations(@Param("userIds") List<Integer> userIds);

}