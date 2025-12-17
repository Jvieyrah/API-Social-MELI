package com.meli.social.user.impl;

import com.meli.social.user.inter.IUserRepository;
import com.meli.social.user.inter.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository implements IUserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public Optional<User> findByUserName(String userName) {
        return jpaRepository.findByUserName(userName);
    }

    @Override
    public boolean existsByUserName(String userName) {
        return jpaRepository.existsByUserName(userName);
    }

    @Override
    public Optional<Integer> getFollowersCountByUserId(Integer userId) {
        return jpaRepository.getFollowersCountByUserId(userId);
    }

    @Override
    public List<User> findFollowersByUserId(Integer userId) {
        return jpaRepository.findFollowersByUserId(userId);
    }

    @Override
    public List<User> findFollowersByUserIdOrderByNameAsc(Integer userId) {
        return jpaRepository.findFollowersByUserIdOrderByNameAsc(userId);
    }

    @Override
    public List<User> findFollowersByUserIdOrderByNameDesc(Integer userId) {
        return jpaRepository.findFollowersByUserIdOrderByNameDesc(userId);
    }

    @Override
    public List<User> findFollowingByUserId(Integer userId) {
        return jpaRepository.findFollowingByUserId(userId);
    }

    @Override
    public List<User> findFollowingByUserIdOrderByNameAsc(Integer userId) {
        return jpaRepository.findFollowingByUserIdOrderByNameAsc(userId);
    }

    @Override
    public List<User> findFollowingByUserIdOrderByNameDesc(Integer userId) {
        return jpaRepository.findFollowingByUserIdOrderByNameDesc(userId);
    }

    @Override
    public List<User> findAllByOrderByUserNameAsc() {
        return jpaRepository.findAllByOrderByUserNameAsc();
    }

    @Override
    public List<User> findAllByOrderByUserNameDesc() {
        return jpaRepository.findAllByOrderByUserNameDesc();
    }

    @Override
    public boolean isFollowing(Integer followerId, Integer followedId) {
        return jpaRepository.isFollowing(followerId, followedId);
    }

    @Override
    public List<User> findTopUsersByFollowersCount() {
        return jpaRepository.findTopUsersByFollowersCount();
    }

    @Override
    public List<User> findAllByOrderByFollowersCountDesc(Pageable pageable) {
        return jpaRepository.findAllByOrderByFollowersCountDesc(pageable);
    }

    @Override
    public List<User> findUsersWithPosts() {
        return jpaRepository.findUsersWithPosts();
    }

    @Override
    public List<User> findByUserIdIn(List<Integer> userIds) {
        return jpaRepository.findByUserIdIn(userIds);
    }

    @Override
    public Integer countFollowingByUserId(Integer userId) {
        return jpaRepository.countFollowingByUserId(userId);
    }

    @Override
    public List<User> findUsersNotFollowingAnyone() {
        return jpaRepository.findUsersNotFollowingAnyone();
    }

    @Override
    public List<User> findUsersWithoutFollowers() {
        return jpaRepository.findUsersWithoutFollowers();
    }

    @Override
    public List<User> searchByUserName(String partialName) {
        return jpaRepository.searchByUserName(partialName);
    }

    @Override
    public List<User> findByUserNameContainingIgnoreCase(String partialName) {
        return jpaRepository.findByUserNameContainingIgnoreCase(partialName);
    }

    @Override
    @Transactional
    public void updateFollowersCount(Integer userId, Integer count) {
        jpaRepository.updateFollowersCount(userId, count);
    }

    @Override
    public List<User> findMutualFollowers(Integer userId) {
        return jpaRepository.findMutualFollowers(userId);
    }

    @Override
    public List<User> findSuggestedUsers(Integer userId) {
        return jpaRepository.findSuggestedUsers(userId);
    }

    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    @Transactional
    public void delete(User user) {
        jpaRepository.delete(user);
    }
}