package com.meli.social.user.inter;

import com.meli.social.user.dto.UserDTO;
import com.meli.social.user.dto.UserSimpleDTO;
import com.meli.social.user.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    UserSimpleDTO createUser(String userName);

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

    List<UserSimpleDTO> getFollowers(Integer userId);

    List<User> getFollowersOrdered(Integer userId, String order);

    List<UserSimpleDTO> getFollowing(Integer userId);

    List<User> getFollowingOrdered(Integer userId, String order);

    Integer getFollowingCount(Integer userId);

    List<User> getMutualFollowers(Integer userId);

    List<User> getSuggestedUsers(Integer userId);

    List<UserDTO> getTopUsers(int limit);

    List<User> getUsersWithPosts();

    List<User> getUsersByIds(List<Integer> userIds);

    List<User> getUsersNotFollowingAnyone();

    List<User> getUsersWithoutFollowers();

    List<User> searchUsers(String partialName);

    void updateFollowersCount(Integer userId);
}