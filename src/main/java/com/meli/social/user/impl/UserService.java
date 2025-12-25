package com.meli.social.user.impl;

import com.meli.social.user.dto.UserDTO;
import com.meli.social.user.dto.UserSimpleDTO;
import com.meli.social.user.inter.IUserService;
import com.meli.social.user.inter.UserJpaRepository;
import com.meli.social.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements IUserService {

    private final UserJpaRepository userRepository;

    @Override
    @Transactional
    public UserSimpleDTO createUser(String userName) {
        validateUserName(userName);
        User user = new User(userName);
        User savedUser = userRepository.save(user);
        return UserSimpleDTO.fromUser(savedUser);
    }

    private void validateUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("Username não pode ser vazio");
        }
        if (userRepository.existsByUserName(userName)) {
            throw new IllegalArgumentException("Username já existe: " + userName);
        }
    }

    // Buscar top users (SEM relacionamentos lazy)
    public List<UserDTO> getTopUsers(int limit) {
        List<User> users = userRepository.findAllByOrderByFollowersCountDesc(
                PageRequest.of(0, limit)
        );

        return users.stream()
                .map(UserDTO::fromEntity)
                .toList();
    }

    @Override
    public List<User> getUsersWithPosts() {
        return List.of();
    }

    @Override
    public List<User> getUsersByIds(List<Integer> userIds) {
        return List.of();
    }

    @Override
    public List<User> getUsersNotFollowingAnyone() {
        return List.of();
    }

    @Override
    public List<User> getUsersWithoutFollowers() {
        return List.of();
    }

    @Override
    public List<User> searchUsers(String partialName) {
        return List.of();
    }

    @Override
    public void updateFollowersCount(Integer userId) {

    }

    // Buscar top users COM relacionamentos (usando FETCH JOIN)
    public List<UserDTO> getTopUsersWithDetails(int limit) {
        List<User> users = userRepository.findAllByOrderByFollowersCountDesc(
                PageRequest.of(0, limit)
        );

        // Carrega relacionamentos de forma eficiente
        return users.stream()
                .map(user -> {
                    // Força o carregamento dos relacionamentos dentro da transação
                    user.getFollowers().size();
                    user.getFollowing().size();
                    return UserDTO.fromEntityWithRelations(user);
                })
                .toList();
    }

    // Buscar usuário específico COM relacionamentos
    public UserDTO getUserWithDetails(Integer userId) {
        User user = userRepository.findByIdWithAllRelations(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        return UserDTO.fromEntityWithRelations(user);
    }


    @Override
    public Optional<User> getUserById(Integer userId) {
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserByUserName(String userName) {
        return Optional.empty();
    }

    @Override
    public List<User> getAllUsers() {
        return List.of();
    }

    @Override
    public List<User> getAllUsersOrdered(String order) {
        return List.of();
    }

    @Override
    public void deleteUser(Integer userId) {

    }

    @Override
    public boolean existsUser(Integer userId) {
        return false;
    }

    @Override
    public boolean existsUserByUserName(String userName) {
        return false;
    }

    @Override
    public long countUsers() {
        return 0;
    }

    @Override
    public Integer getFollowersCount(Integer userId) {
        return 0;
    }

    // Buscar followers de um usuário
    public List<UserSimpleDTO> getFollowers(Integer userId) {
        List<User> followers = userRepository.findFollowersByUserId(userId);

        return followers.stream()
                .map(user -> new UserSimpleDTO(user.getUserId(), user.getUserName()))
                .toList();
    }

    @Override
    public List<User> getFollowersOrdered(Integer userId, String order) {
        return List.of();
    }

    // Buscar following de um usuário
    public List<UserSimpleDTO> getFollowing(Integer userId) {
        List<User> following = userRepository.findFollowingByUserId(userId);

        return following.stream()
                .map(user -> new UserSimpleDTO(user.getUserId(), user.getUserName()))
                .toList();
    }

    @Override
    public List<User> getFollowingOrdered(Integer userId, String order) {
        return List.of();
    }

    @Override
    public Integer getFollowingCount(Integer userId) {
        return 0;
    }

    @Override
    public List<User> getMutualFollowers(Integer userId) {
        return List.of();
    }

    @Override
    public List<User> getSuggestedUsers(Integer userId) {
        return List.of();
    }
}
