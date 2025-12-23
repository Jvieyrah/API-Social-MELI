package com.meli.social.unit.repository;

import com.meli.social.user.dto.UserSimpleDTO;
import com.meli.social.user.model.User;
import com.meli.social.user.impl.UserService;
import com.meli.social.user.inter.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Create User Tests")
class UserServiceTest {

@Mock
private UserJpaRepository userRepository;

@InjectMocks
private UserService userService;

@Test
@DisplayName("Deve criar um novo usuário se o username for único e válido")
void testCreateUserSuccess() {
    // Arrange
    String userName = "test_user";

    User savedUser = new User();
    savedUser.setUserId(1);
    savedUser.setUserName(userName);
    savedUser.setFollowersCount(0);
    savedUser.setFollowers(new HashSet<>());
    savedUser.setFollowing(new HashSet<>());

    when(userRepository.existsByUserName(userName)).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    UserSimpleDTO result = userService.createUser(userName);

    assertNotNull(result, "O resultado não pode ser nulo");
    assertEquals("test_user", result.getUserName(), "Username deve ser 'test_user'");
    assertNotNull(result.getUserId(), "UserId não pode ser nulo");
    assertEquals(1, result.getUserId(), "UserId deve ser 1");

    verify(userRepository, times(1)).existsByUserName(userName);
    verify(userRepository, times(1)).save(any(User.class));
}

@Test
@DisplayName("Deve lançar exceção quando username já existe")
void testCreateUser_UserNameAlreadyExists() {

    String userName = "existing_user";
    when(userRepository.existsByUserName(userName)).thenReturn(true);

    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(userName)
    );

    assertEquals("Username já existe: " + userName, exception.getMessage());
    verify(userRepository, times(1)).existsByUserName(userName);
    verify(userRepository, never()).save(any(User.class));
}

@Test
@DisplayName("Deve lançar exceção quando username for vazio")
void testCreateUser_EmptyUserName() {
    String userName = "";

    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(userName)
    );

    assertEquals("Username não pode ser vazio", exception.getMessage());
    verify(userRepository, never()).existsByUserName(anyString());
    verify(userRepository, never()).save(any(User.class));
}

@Test
@DisplayName("Deve lançar exceção quando username for nulo")
void testCreateUser_NullUserName() {

    String userName = null;

    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(userName)
    );

    assertEquals("Username não pode ser vazio", exception.getMessage());
    verify(userRepository, never()).existsByUserName(anyString());
    verify(userRepository, never()).save(any(User.class));
}


//    @Test
//    @DisplayName("Deve retornar top users ordenados por número de seguidores")
//    void testGetTopUsers_Success() {
//        // Arrange
//        int limit = 3;
//        List<User> users = List.of(user3, user1, user2);
//        when(userRepository.findAllByOrderByFollowersCountDesc(PageRequest.of(0, limit)))
//                .thenReturn(users);
//
//        // Act
//        List<UserDTO> result = userService.getTopUsers(limit);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(3, result.size());
//        assertEquals("pedro_oliveira", result.get(0).getUserName());
//        assertEquals(150, result.get(0).getFollowersCount());
//        assertEquals("joao_silva", result.get(1).getUserName());
//        assertEquals("maria_santos", result.get(2).getUserName());
//
//        verify(userRepository, times(1))
//                .findAllByOrderByFollowersCountDesc(PageRequest.of(0, limit));
//    }
//
//    @Test
//    @DisplayName("Deve retornar lista vazia quando não houver usuários")
//    void testGetTopUsers_EmptyList() {
//        // Arrange
//        int limit = 5;
//        when(userRepository.findAllByOrderByFollowersCountDesc(PageRequest.of(0, limit)))
//                .thenReturn(List.of());
//
//        // Act
//        List<UserDTO> result = userService.getTopUsers(limit);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//        verify(userRepository, times(1))
//                .findAllByOrderByFollowersCountDesc(PageRequest.of(0, limit));
//    }

//    @Test
//    @DisplayName("Deve retornar top users com detalhes completos")
//    void testGetTopUsersWithDetails_Success() {
//        // Arrange
//        int limit = 2;
//
//        // Configurar relacionamentos
//        user1.getFollowers().add(user2);
//        user1.getFollowing().add(user3);
//        user2.getFollowers().add(user1);
//
//        List<User> users = List.of(user1, user2);
//        when(userRepository.findAllByOrderByFollowersCountDesc(PageRequest.of(0, limit)))
//                .thenReturn(users);
//
//        // Act
//        List<UserDTO> result = userService.getTopUsersWithDetails(limit);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertEquals("joao_silva", result.get(0).getUserName());
//
//        verify(userRepository, times(1))
//                .findAllByOrderByFollowersCountDesc(PageRequest.of(0, limit));
//    }
//
//    @Test
//    @DisplayName("Deve retornar usuário com todos os detalhes quando encontrado")
//    void testGetUserWithDetails_Success() {
//        // Arrange
//        Integer userId = 1;
//        user1.getFollowers().add(user2);
//        user1.getFollowing().add(user3);
//
//        when(userRepository.findByIdWithAllRelations(userId))
//                .thenReturn(Optional.of(user1));
//
//        // Act
//        UserDTO result = userService.getUserWithDetails(userId);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(userId, result.getUserId());
//        assertEquals("joao_silva", result.getUserName());
//        assertEquals(100, result.getFollowersCount());
//
//        verify(userRepository, times(1)).findByIdWithAllRelations(userId);
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção quando usuário não for encontrado")
//    void testGetUserWithDetails_UserNotFound() {
//        // Arrange
//        Integer userId = 999;
//        when(userRepository.findByIdWithAllRelations(userId))
//                .thenReturn(Optional.empty());
//
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class,
//                () -> userService.getUserWithDetails(userId)
//        );
//
//        assertEquals("User not found: " + userId, exception.getMessage());
//        verify(userRepository, times(1)).findByIdWithAllRelations(userId);
//    }

//    @Test
//    @DisplayName("Deve retornar lista de seguidores de um usuário")
//    void testGetFollowers_Success() {
//        // Arrange
//        Integer userId = 1;
//        List<User> followers = List.of(user2, user3);
//        when(userRepository.findFollowersByUserId(userId))
//                .thenReturn(followers);
//
//        // Act
//        List<UserSimpleDTO> result = userService.getFollowers(userId);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertEquals(2, result.get(0).getUserId());
//        assertEquals("maria_santos", result.get(0).getUserName());
//        assertEquals(3, result.get(1).getUserId());
//        assertEquals("pedro_oliveira", result.get(1).getUserName());
//
//        verify(userRepository, times(1)).findFollowersByUserId(userId);
//    }
//
//    @Test
//    @DisplayName("Deve retornar lista vazia quando usuário não tem seguidores")
//    void testGetFollowers_EmptyList() {
//        // Arrange
//        Integer userId = 1;
//        when(userRepository.findFollowersByUserId(userId))
//                .thenReturn(List.of());
//
//        // Act
//        List<UserSimpleDTO> result = userService.getFollowers(userId);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//        verify(userRepository, times(1)).findFollowersByUserId(userId);
//    }
//
//    @Test
//    @DisplayName("Deve retornar lista de usuários que o usuário segue")
//    void testGetFollowing_Success() {
//        // Arrange
//        Integer userId = 1;
//        List<User> following = List.of(user2, user3);
//        when(userRepository.findFollowingByUserId(userId))
//                .thenReturn(following);
//
//        // Act
//        List<UserSimpleDTO> result = userService.getFollowing(userId);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        assertEquals(2, result.get(0).getUserId());
//        assertEquals("maria_santos", result.get(0).getUserName());
//        assertEquals(3, result.get(1).getUserId());
//        assertEquals("pedro_oliveira", result.get(1).getUserName());
//
//        verify(userRepository, times(1)).findFollowingByUserId(userId);
//    }
//
//    @Test
//    @DisplayName("Deve retornar lista vazia quando usuário não segue ninguém")
//    void testGetFollowing_EmptyList() {
//        // Arrange
//        Integer userId = 1;
//        when(userRepository.findFollowingByUserId(userId))
//                .thenReturn(List.of());
//
//        // Act
//        List<UserSimpleDTO> result = userService.getFollowing(userId);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//        verify(userRepository, times(1)).findFollowingByUserId(userId);
//    }
//
//    @Test
//    @DisplayName("Deve lidar com limite de 1 usuário corretamente")
//    void testGetTopUsers_LimitOne() {
//        // Arrange
//        int limit = 1;
//        List<User> users = List.of(user3);
//        when(userRepository.findAllByOrderByFollowersCountDesc(PageRequest.of(0, limit)))
//                .thenReturn(users);
//
//        // Act
//        List<UserDTO> result = userService.getTopUsers(limit);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals("pedro_oliveira", result.get(0).getUserName());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getUsersWithPosts vazio (não implementado)")
//    void testGetUsersWithPosts_NotImplemented() {
//        // Act
//        List<User> result = userService.getUsersWithPosts();
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getUsersByIds vazio (não implementado)")
//    void testGetUsersByIds_NotImplemented() {
//        // Act
//        List<User> result = userService.getUsersByIds(List.of(1, 2, 3));
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getUsersNotFollowingAnyone vazio (não implementado)")
//    void testGetUsersNotFollowingAnyone_NotImplemented() {
//        // Act
//        List<User> result = userService.getUsersNotFollowingAnyone();
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getUsersWithoutFollowers vazio (não implementado)")
//    void testGetUsersWithoutFollowers_NotImplemented() {
//        // Act
//        List<User> result = userService.getUsersWithoutFollowers();
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar searchUsers vazio (não implementado)")
//    void testSearchUsers_NotImplemented() {
//        // Act
//        List<User> result = userService.searchUsers("joao");
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve executar updateFollowersCount sem erro (não implementado)")
//    void testUpdateFollowersCount_NotImplemented() {
//        // Act & Assert
//        assertDoesNotThrow(() -> userService.updateFollowersCount(1));
//    }
//
//    @Test
//    @DisplayName("Deve retornar createUser nulo (não implementado)")
//    void testCreateUser_NotImplemented() {
//        // Act
//        User result = userService.createUser(user1);
//
//        // Assert
//        assertNull(result);
//    }
//
//    @Test
//    @DisplayName("Deve retornar getUserById vazio (não implementado)")
//    void testGetUserById_NotImplemented() {
//        // Act
//        Optional<User> result = userService.getUserById(1);
//
//        // Assert
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getUserByUserName vazio (não implementado)")
//    void testGetUserByUserName_NotImplemented() {
//        // Act
//        Optional<User> result = userService.getUserByUserName("joao_silva");
//
//        // Assert
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getAllUsers vazio (não implementado)")
//    void testGetAllUsers_NotImplemented() {
//        // Act
//        List<User> result = userService.getAllUsers();
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getAllUsersOrdered vazio (não implementado)")
//    void testGetAllUsersOrdered_NotImplemented() {
//        // Act
//        List<User> result = userService.getAllUsersOrdered("name_asc");
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve executar deleteUser sem erro (não implementado)")
//    void testDeleteUser_NotImplemented() {
//        // Act & Assert
//        assertDoesNotThrow(() -> userService.deleteUser(1));
//    }
//
//    @Test
//    @DisplayName("Deve retornar existsUser falso (não implementado)")
//    void testExistsUser_NotImplemented() {
//        // Act
//        boolean result = userService.existsUser(1);
//
//        // Assert
//        assertFalse(result);
//    }
//
//    @Test
//    @DisplayName("Deve retornar existsUserByUserName falso (não implementado)")
//    void testExistsUserByUserName_NotImplemented() {
//        // Act
//        boolean result = userService.existsUserByUserName("joao_silva");
//
//        // Assert
//        assertFalse(result);
//    }
//
//    @Test
//    @DisplayName("Deve retornar countUsers zero (não implementado)")
//    void testCountUsers_NotImplemented() {
//        // Act
//        long result = userService.countUsers();
//
//        // Assert
//        assertEquals(0, result);
//    }
//
//    @Test
//    @DisplayName("Deve retornar followUser nulo (não implementado)")
//    void testFollowUser_NotImplemented() {
//        // Act
//        User result = userService.followUser(1, 2);
//
//        // Assert
//        assertNull(result);
//    }
//
//    @Test
//    @DisplayName("Deve retornar unfollowUser nulo (não implementado)")
//    void testUnfollowUser_NotImplemented() {
//        // Act
//        User result = userService.unfollowUser(1, 2);
//
//        // Assert
//        assertNull(result);
//    }
//
//    @Test
//    @DisplayName("Deve retornar isFollowing falso (não implementado)")
//    void testIsFollowing_NotImplemented() {
//        // Act
//        boolean result = userService.isFollowing(1, 2);
//
//        // Assert
//        assertFalse(result);
//    }
//
//    @Test
//    @DisplayName("Deve retornar getFollowersCount nulo (não implementado)")
//    void testGetFollowersCount_NotImplemented() {
//        // Act
//        Integer result = userService.getFollowersCount(1);
//
//        // Assert
//        assertNull(result);
//    }
//
//    @Test
//    @DisplayName("Deve retornar getFollowersOrdered vazio (não implementado)")
//    void testGetFollowersOrdered_NotImplemented() {
//        // Act
//        List<User> result = userService.getFollowersOrdered(1, "name_asc");
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getFollowingOrdered vazio (não implementado)")
//    void testGetFollowingOrdered_NotImplemented() {
//        // Act
//        List<User> result = userService.getFollowingOrdered(1, "name_asc");
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getFollowingCount nulo (não implementado)")
//    void testGetFollowingCount_NotImplemented() {
//        // Act
//        Integer result = userService.getFollowingCount(1);
//
//        // Assert
//        assertNull(result);
//    }
//
//    @Test
//    @DisplayName("Deve retornar getMutualFollowers vazio (não implementado)")
//    void testGetMutualFollowers_NotImplemented() {
//        // Act
//        List<User> result = userService.getMutualFollowers(1);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getSuggestedUsers vazio (não implementado)")
//    void testGetSuggestedUsers_NotImplemented() {
//        // Act
//        List<User> result = userService.getSuggestedUsers(1);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
}