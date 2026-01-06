package com.meli.social.user.impl;

import com.meli.social.user.dto.UserDTO;
import com.meli.social.user.dto.UserSimpleDTO;
import com.meli.social.user.dto.UserWithFollowersDTO;
import com.meli.social.user.inter.IUserService;
import com.meli.social.user.inter.UserJpaRepository;
import com.meli.social.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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


    public List<UserDTO> getTopUsersWithDetails(int limit) {
        List<User> users = userRepository.findAllByOrderByFollowersCountDesc(
                PageRequest.of(0, limit)
        );


        return users.stream()
                .map(user -> {
                    user.getFollowers().size();
                    user.getFollowing().size();
                    return UserDTO.fromEntityWithRelations(user);
                })
                .toList();
    }

    public UserDTO getUserWithDetails(Integer userId) {
        User user = userRepository.findByIdWithAllRelations(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        return UserDTO.fromEntityWithRelations(user);
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
    public UserWithFollowersDTO getFollowing(Integer userId,String order) {
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

        return UserWithFollowersDTO.withFollowed(mainUser, followingDTD);
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
