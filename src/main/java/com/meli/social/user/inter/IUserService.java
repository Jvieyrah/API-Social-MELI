package com.meli.social.user.inter;

import com.meli.social.user.impl.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    User createUser(User user);

    Optional<User> getUserById(Integer userId);

    Optional<User> getUserByUserName(String userName);

    List<User> getAllUsers();

    List<User> getAllUsersOrdered(String order);

    void deleteUser(Integer userId);

    boolean existsUser(Integer userId);

    boolean existsUserByUserName(String userName);

    long countUsers();

    User followUser(Integer followerId, Integer followedId);

    User unfollowUser(Integer followerId, Integer followedId);

    boolean isFollowing(Integer followerId, Integer followedId);

    Integer getFollowersCount(Integer userId);

    List<User> getFollowers(Integer userId);

    List<User> getFollowersOrdered(Integer userId, String order);

    List<User> getFollowing(Integer userId);

    List<User> getFollowingOrdered(Integer userId, String order);

    Integer getFollowingCount(Integer userId);

    List<User> getMutualFollowers(Integer userId);

    List<User> getSuggestedUsers(Integer userId);

    List<User> getTopUsers(int limit);

    List<User> getUsersWithPosts();

    List<User> getUsersByIds(List<Integer> userIds);

    List<User> getUsersNotFollowingAnyone();

    List<User> getUsersWithoutFollowers();

    List<User> searchUsers(String partialName);

    void updateFollowersCount(Integer userId);
}