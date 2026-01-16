package com.meli.social.user.impl;

import com.meli.social.user.dto.UserDTO;
import com.meli.social.user.dto.UserSimpleDTO;
import com.meli.social.user.dto.UserWithFollowedDTO;
import com.meli.social.user.dto.UserWithFollowersDTO;
import com.meli.social.user.inter.IUserService;
import com.meli.social.user.inter.UserJpaRepository;
import com.meli.social.user.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class        UserService implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserJpaRepository userRepository;

    @Override
    @Transactional
    public UserSimpleDTO createUser(String userName) {
        logger.info("Creating user userName={}", userName);
        validateUserName(userName);
        User user = new User(userName);
        User savedUser = userRepository.save(user);
        logger.info("User persisted userId={} userName={}", savedUser.getUserId(), savedUser.getUserName());
        return UserSimpleDTO.fromUser(savedUser);
    }

    private void validateUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            logger.warn("Invalid userName (blank)");
            throw new IllegalArgumentException("Nome do usuário não pode ser vazio");
        }
        if (userRepository.existsByUserName(userName)) {
            logger.warn("User already exists userName={}", userName);
            throw new IllegalArgumentException("Usuário já existe: " + userName);
        }
    }

    // Buscar top users (SEM relacionamentos lazy)
    public List<UserDTO> getTopUsers(int limit) {
        logger.info("Fetching top users limit={}", limit);
        List<User> users = userRepository.findAllByOrderByFollowersCountDesc(
                PageRequest.of(0, limit)
        );

        return users.stream()
                .map(UserDTO::fromEntity)
                .toList();
    }

    @Override
    public UserWithFollowersDTO getFollowers(Integer userId) {
        return getFollowers(userId, null);
    }

    @Override
    public UserWithFollowersDTO getFollowers(Integer userId, String order) {
        User mainUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + userId));

        List<User> followers;
        if (order == null || order.trim().isEmpty()) {
            followers = userRepository.findFollowersByUserId(userId);
        } else if ("name_asc".equalsIgnoreCase(order)) {
            followers = userRepository.findFollowersByUserIdOrderByNameAsc(userId);
        } else if ("name_desc".equalsIgnoreCase(order)) {
            followers = userRepository.findFollowersByUserIdOrderByNameDesc(userId);
        } else {
            throw new IllegalArgumentException("Order inválido: " + order);
        }

        List<UserSimpleDTO> followersDTO = followers.stream()
                .map(user -> new UserSimpleDTO(user.getUserId(), user.getUserName()))
                .toList();

        return UserWithFollowersDTO.withFollowers(mainUser, followersDTO);
    }

    @Override
    public UserWithFollowersDTO getFollowers(Integer userId, String order, int page, int size) {
        logger.info("Fetching followers userId={} order={} page={} size={}", userId, order, page, size);
        validatePageRequest(page, size);
        User mainUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + userId));

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> followersPage;
        if (order == null || order.trim().isEmpty()) {
            followersPage = userRepository.findFollowersByUserId(userId, pageRequest);
        } else if ("name_asc".equalsIgnoreCase(order)) {
            followersPage = userRepository.findFollowersByUserIdOrderByNameAsc(userId, pageRequest);
        } else if ("name_desc".equalsIgnoreCase(order)) {
            followersPage = userRepository.findFollowersByUserIdOrderByNameDesc(userId, pageRequest);
        } else {
            throw new IllegalArgumentException("Order inválido: " + order);
        }
        List<UserSimpleDTO> followersDTO = followersPage.getContent().stream()
                .map(user -> new UserSimpleDTO(user.getUserId(), user.getUserName()))
                .toList();

        return UserWithFollowersDTO.withFollowers(mainUser, followersDTO);
    }


    @Override
    public UserWithFollowedDTO getFollowing(Integer userId,String order) {
        User mainUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + userId));

        List<User> following;
        if (order == null || order.trim().isEmpty()) {
            following = userRepository.findFollowingByUserId(userId);
        } else if ("name_asc".equalsIgnoreCase(order)) {
            following = userRepository.findFollowingByUserIdOrderByNameAsc(userId);
        } else if ("name_desc".equalsIgnoreCase(order)) {
            following = userRepository.findFollowingByUserIdOrderByNameDesc(userId);
        } else {
            throw new IllegalArgumentException("Order inválido: " + order);
        }

        List<UserSimpleDTO> followingDTD = following.stream()
                .map(user -> new UserSimpleDTO(user.getUserId(), user.getUserName())) // <- estudar a implementacao de uma classe mapper aqui.
                .toList();

        return UserWithFollowedDTO.withFollowed(mainUser, followingDTD);
    }

    @Override
    public UserWithFollowedDTO getFollowing(Integer userId, String order, int page, int size) {
        logger.info("Fetching following userId={} order={} page={} size={}", userId, order, page, size);
        validatePageRequest(page, size);
        User mainUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + userId));

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> followingPage;
        if (order == null || order.trim().isEmpty()) {
            followingPage = userRepository.findFollowingByUserId(userId, pageRequest);
        } else if ("name_asc".equalsIgnoreCase(order)) {
            followingPage = userRepository.findFollowingByUserIdOrderByNameAsc(userId, pageRequest);
        } else if ("name_desc".equalsIgnoreCase(order)) {
            followingPage = userRepository.findFollowingByUserIdOrderByNameDesc(userId, pageRequest);
        } else {
            throw new IllegalArgumentException("Order inválido: " + order);
        }
        List<UserSimpleDTO> followingDTO = followingPage.getContent().stream()
                .map(user -> new UserSimpleDTO(user.getUserId(), user.getUserName()))
                .toList();

        return UserWithFollowedDTO.withFollowed(mainUser, followingDTO);
    }

    private static void validatePageRequest(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page inválida: " + page);
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Size inválido: " + size);
        }
    }

}
