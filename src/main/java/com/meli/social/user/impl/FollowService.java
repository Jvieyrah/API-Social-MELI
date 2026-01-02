package com.meli.social.user.impl;

import com.meli.social.exception.UserNotFoundException;
import com.meli.social.user.dto.UserSimpleDTO;
import com.meli.social.user.inter.IFollowService;
import com.meli.social.user.inter.UserJpaRepository;
import com.meli.social.user.model.User;
import com.meli.social.user.model.UserFollow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class FollowService implements IFollowService {

    private final UserJpaRepository userRepository;

    @Override
    @Transactional
    public User followUser(Integer followerId, Integer followedId) {

        if (isFollowing(followerId, followedId)) {
            throw new IllegalArgumentException(
                    "Usuário %d já segue o usuário %d".formatted(followerId, followedId)
            );
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserNotFoundException("Seguidor não encontrado"));

        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new UserNotFoundException("Usuário a ser seguido não encontrado"));

        follower.follow(followed);
        userRepository.save(follower);

        return follower;
    }

    @Override
    @Transactional
    public User unfollowUser(Integer followerId, Integer followedId) {

        if (!isFollowing(followerId, followedId)) {
            throw new IllegalArgumentException(
                    "Usuário %d não segue o usuário %d".formatted(followerId, followedId)
            );
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserNotFoundException("Seguidor não encontrado"));

        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new UserNotFoundException("Usuário a ser deixado de seguir não encontrado"));

        
        follower.unfollow(followed);
        userRepository.save(follower);

        return follower;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Integer followerId, Integer followedId) {
        validateNullsOrEcuals(followerId, followedId);

        return userRepository.isFollowing(followerId, followedId);
    }

    private static void validateNullsOrEcuals(Integer followerId, Integer followedId) {
        if (followerId == null || followedId == null) {
            throw new IllegalArgumentException("IDs não podem ser nulos");
        }

        if (followerId.equals(followedId)) {
            throw new IllegalArgumentException("Os IDs de usuário não podem ser iguais!");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserSimpleDTO returnUserWithFollowerCounter (Integer userId){
        User follower = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        return UserSimpleDTO.fromUserWithFollowers(follower);
    }
}
