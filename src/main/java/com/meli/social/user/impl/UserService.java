package com.meli.social.user.impl;

import com.meli.social.user.inter.IUserRepository;
import com.meli.social.user.inter.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    @Override
    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByUserName(user.getUserName())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUserName());
        }
        user.setFollowersCount(0);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> getUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getAllUsersOrdered(String order) {
        if ("name_asc".equalsIgnoreCase(order)) {
            return userRepository.findAllByOrderByUserNameAsc();
        } else if ("name_desc".equalsIgnoreCase(order)) {
            return userRepository.findAllByOrderByUserNameDesc();
        }
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public boolean existsUser(Integer userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public boolean existsUserByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }

    @Override
    @Transactional
    public User followUser(Integer followerId, Integer followedId) {
        if (followerId.equals(followedId)) {
            throw new IllegalArgumentException("User cannot follow themselves");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("Follower not found: " + followerId));

        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new IllegalArgumentException("Followed user not found: " + followedId));

        if (userRepository.isFollowing(followerId, followedId)) {
            throw new IllegalArgumentException("User " + followerId + " already follows " + followedId);
        }

        follower.getFollowing().add(followed);
        followed.setFollowersCount(followed.getFollowersCount() + 1);

        userRepository.save(follower);
        userRepository.save(followed);

        return followed;
    }

    @Override
    @Transactional
    public User unfollowUser(Integer followerId, Integer followedId) {
        if (followerId.equals(followedId)) {
            throw new IllegalArgumentException("User cannot unfollow themselves");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("Follower not found: " + followerId));

        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new IllegalArgumentException("Followed user not found: " + followedId));

        if (!userRepository.isFollowing(followerId, followedId)) {
            throw new IllegalArgumentException("User " + followerId + " does not follow " + followedId);
        }

        follower.getFollowing().remove(followed);
        followed.setFollowersCount(Math.max(0, followed.getFollowersCount() - 1));

        userRepository.save(follower);
        userRepository.save(followed);

        return followed;
    }

    @Override
    public boolean isFollowing(Integer followerId, Integer followedId) {
        return userRepository.isFollowing(followerId, followedId);
    }

    @Override
    public Integer getFollowersCount(Integer userId) {
        return userRepository.getFollowersCountByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    @Override
    public List<User> getFollowers(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        return userRepository.findFollowersByUserId(userId);
    }

    @Override
    public List<User> getFollowersOrdered(Integer userId, String order) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        if ("name_asc".equalsIgnoreCase(order)) {
            return userRepository.findFollowersByUserIdOrderByNameAsc(userId);
        } else if ("name_desc".equalsIgnoreCase(order)) {
            return userRepository.findFollowersByUserIdOrderByNameDesc(userId);
        }
        return userRepository.findFollowersByUserId(userId);
    }

    @Override
    public List<User> getFollowing(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        return userRepository.findFollowingByUserId(userId);
    }

    @Override
    public List<User> getFollowingOrdered(Integer userId, String order) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        if ("name_asc".equalsIgnoreCase(order)) {
            return userRepository.findFollowingByUserIdOrderByNameAsc(userId);
        } else if ("name_desc".equalsIgnoreCase(order)) {
            return userRepository.findFollowingByUserIdOrderByNameDesc(userId);
        }
        return userRepository.findFollowingByUserId(userId);
    }

    @Override
    public Integer getFollowingCount(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        return userRepository.countFollowingByUserId(userId);
    }

    @Override
    public List<User> getMutualFollowers(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        return userRepository.findMutualFollowers(userId);
    }

    @Override
    public List<User> getSuggestedUsers(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        return userRepository.findSuggestedUsers(userId);
    }

    @Override
    public List<User> getTopUsers(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        Pageable pageable = PageRequest.of(0, limit);
        return userRepository.findAllByOrderByFollowersCountDesc(pageable);
    }

    @Override
    public List<User> getUsersWithPosts() {
        return userRepository.findUsersWithPosts();
    }

    @Override
    public List<User> getUsersByIds(List<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("User IDs list cannot be empty");
        }
        return userRepository.findByUserIdIn(userIds);
    }

    @Override
    public List<User> getUsersNotFollowingAnyone() {
        return userRepository.findUsersNotFollowingAnyone();
    }

    @Override
    public List<User> getUsersWithoutFollowers() {
        return userRepository.findUsersWithoutFollowers();
    }

    @Override
    public List<User> searchUsers(String partialName) {
        if (partialName == null || partialName.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be empty");
        }
        return userRepository.searchByUserName(partialName);
    }

    @Override
    @Transactional
    public void updateFollowersCount(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        List<User> followers = userRepository.findFollowersByUserId(userId);
        int actualCount = followers.size();

        if (user.getFollowersCount() != actualCount) {
            userRepository.updateFollowersCount(userId, actualCount);
        }
    }
}