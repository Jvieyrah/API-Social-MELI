package com.meli.social.user.impl;

import com.meli.social.exception.PostUnprocessableException;
import com.meli.social.exception.UserNotFoundException;
import com.meli.social.user.dto.UserSimpleDTO;
import com.meli.social.user.inter.IFollowService;
import com.meli.social.user.inter.UserFollowJpaRepository;
import com.meli.social.user.inter.UserJpaRepository;
import com.meli.social.user.model.User;
import com.meli.social.user.model.UserFollow;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class FollowService implements IFollowService {

    private static final Logger logger = LoggerFactory.getLogger(FollowService.class);

    private final UserJpaRepository userRepository;
    private final UserFollowJpaRepository userFollowRepository;

    @Override
    @Transactional
    public User followUser(Integer followerId, Integer followedId) {

        logger.info("Request to follow followerId={} followedId={}", followerId, followedId);

        if (isFollowing(followerId, followedId)) {
            logger.warn("Follow rejected (already following) followerId={} followedId={}", followerId, followedId);
            throw new PostUnprocessableException(
                    "Usuário %d já segue o usuário %d".formatted(followerId, followedId)
            );
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserNotFoundException("Seguidor não encontrado"));

        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new UserNotFoundException("Usuário a ser seguido não encontrado"));

        UserFollow userFollow = new UserFollow(follower, followed);
        userFollowRepository.save(userFollow);

        followed.incrementFollowersCount();
        userRepository.save(followed);

        logger.info("Follow created followerId={} followedId={} followedFollowersCount={}", followerId, followedId, followed.getFollowersCount());

        return follower;
    }

    @Override
    @Transactional
    public User unfollowUser(Integer followerId, Integer followedId) {

        logger.info("Request to unfollow followerId={} followedId={}", followerId, followedId);

        if (!isFollowing(followerId, followedId)) {
            logger.warn("Unfollow rejected (not following) followerId={} followedId={}", followerId, followedId);
            throw new PostUnprocessableException(
                    "Usuário %d não segue o usuário %d".formatted(followerId, followedId)
            );
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserNotFoundException("Seguidor não encontrado"));

        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new UserNotFoundException("Usuário a ser deixado de seguir não encontrado"));


        int deleted = userFollowRepository.deleteFollow(followerId, followedId);
        if (deleted > 0) {
            followed.decrementFollowersCount();
            userRepository.save(followed);
            logger.info("Unfollow completed followerId={} followedId={} followedFollowersCount={}", followerId, followedId, followed.getFollowersCount());
        } else {
            logger.warn("Unfollow did not delete relationship followerId={} followedId={}", followerId, followedId);
        }

        return follower;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Integer followerId, Integer followedId) {
        validateNullsOrEcuals(followerId, followedId);

        return userFollowRepository.existsFollow(followerId, followedId);
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
        logger.info("Request to get follower counter userId={}", userId);
        User follower = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        return UserSimpleDTO.fromUserWithFollowers(follower);
    }
}
