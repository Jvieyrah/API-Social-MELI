package com.meli.social.user.impl;

import com.meli.social.user.inter.IFollowService;
import com.meli.social.user.inter.UserJpaRepository;
import com.meli.social.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService implements IFollowService {

    private final UserJpaRepository userRepository;

    @Override
    public User followUser(Integer followerId, Integer followedId) {
        return null;
    }

    @Override
    public User unfollowUser(Integer followerId, Integer followedId) {
        return null;
    }

    @Override
    public boolean isFollowing(Integer followerId, Integer followedId) {

        if (followerId == null || followedId == null) {
            throw new IllegalArgumentException("Username não pode ser vazio");
        }
        return userRepository.isFollowing(followerId, followedId);
    }
}
