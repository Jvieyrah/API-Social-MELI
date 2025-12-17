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

    @Query("SELECT u FROM User u JOIN u.following f WHERE f.userId = :userId")
    List<User> findFollowersByUserId(@Param("userId") Integer userId);

    @Query("SELECT u FROM User u JOIN u.following f WHERE f.userId = :userId ORDER BY u.userName ASC")
    List<User> findFollowersByUserIdOrderByNameAsc(@Param("userId") Integer userId);

    @Query("SELECT u FROM User u JOIN u.following f WHERE f.userId = :userId ORDER BY u.userName DESC")
    List<User> findFollowersByUserIdOrderByNameDesc(@Param("userId") Integer userId);

    @Query("SELECT f FROM User u JOIN u.following f WHERE u.userId = :userId")
    List<User> findFollowingByUserId(@Param("userId") Integer userId);

    @Query("SELECT f FROM User u JOIN u.following f WHERE u.userId = :userId ORDER BY f.userName ASC")
    List<User> findFollowingByUserIdOrderByNameAsc(@Param("userId") Integer userId);

    @Query("SELECT f FROM User u JOIN u.following f WHERE u.userId = :userId ORDER BY f.userName DESC")
    List<User> findFollowingByUserIdOrderByNameDesc(@Param("userId") Integer userId);

    List<User> findAllByOrderByUserNameAsc();

    List<User> findAllByOrderByUserNameDesc();

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM User u JOIN u.following f " +
            "WHERE u.userId = :followerId AND f.userId = :followedId")
    boolean isFollowing(@Param("followerId") Integer followerId, @Param("followedId") Integer followedId);

    @Query("SELECT u FROM User u ORDER BY u.followersCount DESC")
    List<User> findTopUsersByFollowersCount();

    List<User> findAllByOrderByFollowersCountDesc(Pageable pageable);

    @Query("SELECT DISTINCT u FROM User u WHERE SIZE(u.posts) > 0")
    List<User> findUsersWithPosts();

    List<User> findByUserIdIn(List<Integer> userIds);

    @Query("SELECT SIZE(u.following) FROM User u WHERE u.userId = :userId")
    Integer countFollowingByUserId(@Param("userId") Integer userId);

    @Query("SELECT u FROM User u WHERE SIZE(u.following) = 0")
    List<User> findUsersNotFollowingAnyone();

    @Query("SELECT u FROM User u WHERE u.followersCount = 0")
    List<User> findUsersWithoutFollowers();

    @Query("SELECT u FROM User u WHERE LOWER(u.userName) LIKE LOWER(CONCAT('%', :partialName, '%'))")
    List<User> searchByUserName(@Param("partialName") String partialName);

    List<User> findByUserNameContainingIgnoreCase(String partialName);

    @Modifying
    @Query("UPDATE User u SET u.followersCount = :count WHERE u.userId = :userId")
    void updateFollowersCount(@Param("userId") Integer userId, @Param("count") Integer count);

    @Query("SELECT f FROM User u JOIN u.following f JOIN f.following ff " +
            "WHERE u.userId = :userId AND ff.userId = :userId")
    List<User> findMutualFollowers(@Param("userId") Integer userId);

    @Query("SELECT DISTINCT f2 FROM User u " +
            "JOIN u.following f1 " +
            "JOIN f1.following f2 " +
            "WHERE u.userId = :userId " +
            "AND f2.userId <> :userId " +
            "AND f2 NOT IN (SELECT f3 FROM User u2 JOIN u2.following f3 WHERE u2.userId = :userId)")
    List<User> findSuggestedUsers(@Param("userId") Integer userId);
}