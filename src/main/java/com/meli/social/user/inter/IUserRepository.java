package com.meli.social.user.inter;

import com.meli.social.user.impl.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;


public interface IUserRepository {

    Optional<User> findByUserName(String userName);

    boolean existsByUserName(String userName);

    Optional<Integer> getFollowersCountByUserId(Integer userId);

    List<User> findFollowersByUserId(Integer userId);

    List<User> findFollowersByUserIdOrderByNameAsc(Integer userId);

    List<User> findFollowersByUserIdOrderByNameDesc(Integer userId);

    List<User> findFollowingByUserId(Integer userId);

    List<User> findFollowingByUserIdOrderByNameAsc(Integer userId);

    List<User> findFollowingByUserIdOrderByNameDesc(Integer userId);

    List<User> findAllByOrderByUserNameAsc();

    List<User> findAllByOrderByUserNameDesc();

    boolean isFollowing(Integer followerId, Integer followedId);

    List<User> findTopUsersByFollowersCount();

    List<User> findAllByOrderByFollowersCountDesc(Pageable pageable);

    List<User> findUsersWithPosts();

    List<User> findByUserIdIn(List<Integer> userIds);

    Integer countFollowingByUserId(Integer userId);

    List<User> findUsersNotFollowingAnyone();

    List<User> findUsersWithoutFollowers();

    List<User> searchByUserName(String partialName);

    List<User> findByUserNameContainingIgnoreCase(String partialName);

    void updateFollowersCount(Integer userId, Integer count);

    List<User> findMutualFollowers(Integer userId);

    List<User> findSuggestedUsers(Integer userId);

    User save(User user);

    Optional<User> findById(Integer id);

    List<User> findAll();

    void deleteById(Integer id);

    boolean existsById(Integer id);

    long count();

    void delete(User user);
}