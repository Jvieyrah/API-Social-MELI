package com.meli.social.unit.service;

import com.meli.social.user.dto.UserSimpleDTO;
import com.meli.social.user.dto.UserWithFollowersDTO;
import com.meli.social.user.model.User;
import com.meli.social.user.impl.UserService;
import com.meli.social.user.inter.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Create User Tests")
class UserServiceTest {

@Mock
private UserJpaRepository userRepository;

@InjectMocks
private UserService userService;

@BeforeEach
void setUp() {
    reset(userRepository);
}


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

@Test
@DisplayName("Deve retornar usuário e lista de followers quando usuário existir")
void testGetFollowersSuccess() {
    // Arrange
    Integer userId = 1;

    User mainUser = new User();
    mainUser.setUserId(userId);
    mainUser.setUserName("main_user");

    User follower1 = new User();
    follower1.setUserId(2);
    follower1.setUserName("follower_1");

    User follower2 = new User();
    follower2.setUserId(3);
    follower2.setUserName("follower_2");

    when(userRepository.findById(userId)).thenReturn(Optional.of(mainUser));
    when(userRepository.findFollowersByUserId(userId)).thenReturn(List.of(follower1, follower2));

    UserWithFollowersDTO result = userService.getFollowers(userId);

    assertNotNull(result);
    assertNotNull(result.getUserName());
    assertEquals(userId, result.getUserId());
    assertEquals("main_user", result.getUserName());

    assertNotNull(result.getFollowers());
    assertEquals(2, result.getFollowers().size());
    assertEquals(2, result.getFollowers().get(0).getUserId());
    assertEquals("follower_1", result.getFollowers().get(0).getUserName());
    assertEquals(3, result.getFollowers().get(1).getUserId());
    assertEquals("follower_2", result.getFollowers().get(1).getUserName());

    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, times(1)).findFollowersByUserId(userId);
}

@Test
@DisplayName("Deve retornar usuário e lista de followers em ordem alfabética crescente")
void testGetFollowersSuccess_OrderByUserNameAscending() {
            Integer userId = 1;

            User mainUser = new User();
            mainUser.setUserId(userId);
            mainUser.setUserName("main_user");

            User follower1 = new User();
            follower1.setUserId(2);
            follower1.setUserName("alpha");

            User follower2 = new User();
            follower2.setUserId(3);
            follower2.setUserName("bravo");

            User follower3 = new User();
            follower3.setUserId(4);
            follower3.setUserName("charlie");

            when(userRepository.findById(userId)).thenReturn(Optional.of(mainUser));
            when(userRepository.findFollowersByUserIdOrderByNameAsc(userId))
                    .thenReturn(List.of(follower1, follower2, follower3));

            UserWithFollowersDTO result = userService.getFollowers(userId, "name_asc");

            assertNotNull(result.getFollowers());
            assertEquals(3, result.getFollowers().size());
            assertEquals(2, result.getFollowers().get(0).getUserId());
            assertEquals("alpha", result.getFollowers().get(0).getUserName());
            assertEquals(3, result.getFollowers().get(1).getUserId());
            assertEquals("bravo", result.getFollowers().get(1).getUserName());
            assertEquals(4, result.getFollowers().get(2).getUserId());
            assertEquals("charlie", result.getFollowers().get(2).getUserName());

            verify(userRepository, times(1)).findById(userId);
            verify(userRepository, times(1)).findFollowersByUserIdOrderByNameAsc(userId);
}

@Test
@DisplayName("Deve retornar usuário e lista de followers em ordem alfabética decescente")
void testGetFollowersSuccess_OrderByUserNameDescending() {

    Integer userId = 1;

    User mainUser = new User();
    mainUser.setUserId(userId);
    mainUser.setUserName("main_user");

    User follower1 = new User();
    follower1.setUserId(2);
    follower1.setUserName("charlie");

    User follower2 = new User();
    follower2.setUserId(3);
    follower2.setUserName("bravo");

    User follower3 = new User();
    follower3.setUserId(4);
    follower3.setUserName("alpha");

    when(userRepository.findById(userId)).thenReturn(Optional.of(mainUser));
    when(userRepository.findFollowersByUserIdOrderByNameDesc(userId))
            .thenReturn(List.of(follower1, follower2, follower3));

    UserWithFollowersDTO result = userService.getFollowers(userId, "name_desc");

    assertNotNull(result.getFollowers());
    assertEquals(3, result.getFollowers().size());
    assertEquals(2, result.getFollowers().get(0).getUserId());
    assertEquals("charlie", result.getFollowers().get(0).getUserName());
    assertEquals(3, result.getFollowers().get(1).getUserId());
    assertEquals("bravo", result.getFollowers().get(1).getUserName());
    assertEquals(4, result.getFollowers().get(2).getUserId());
    assertEquals("alpha", result.getFollowers().get(2).getUserName());

    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, times(1)).findFollowersByUserIdOrderByNameDesc(userId);
}

@Test
@DisplayName("Deve lançar exceção quando order de followers for inválido")
void testGetFollowers_ShouldThrowException_WhenOrderIsInvalid() {
    Integer userId = 1;
    String order = "invalid_order";

    User mainUser = new User();
    mainUser.setUserId(userId);
    mainUser.setUserName("main_user");

    when(userRepository.findById(userId)).thenReturn(Optional.of(mainUser));

    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.getFollowers(userId, order)
    );

    assertEquals("Order inválido: " + order, exception.getMessage());
    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, never()).findFollowersByUserId(anyInt());
    verify(userRepository, never()).findFollowersByUserIdOrderByNameAsc(anyInt());
    verify(userRepository, never()).findFollowersByUserIdOrderByNameDesc(anyInt());
}




@Test
@DisplayName("Deve lançar exceção quando tentar buscar followers de usuário inexistente")
void testGetFollowersUserNotFound() {
    // Arrange
    Integer userId = 999;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.getFollowers(userId)
    );

    assertEquals("Usuário não encontrado: " + userId, exception.getMessage());
    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, never()).findFollowersByUserId(anyInt());
}

@Test
@DisplayName("Deve retornar usuário e lista de following quando usuário existir")
void testGetFollowingSuccess() {
    // Arrange
    Integer userId = 1;

    User mainUser = new User();
    mainUser.setUserId(userId);
    mainUser.setUserName("main_user");

    User following1 = new User();
    following1.setUserId(2);
    following1.setUserName("following_1");

    User following2 = new User();
    following2.setUserId(3);
    following2.setUserName("following_2");

    when(userRepository.findById(userId)).thenReturn(Optional.of(mainUser));
    when(userRepository.findFollowingByUserId(userId)).thenReturn(List.of(following1, following2));

    // Act
    UserWithFollowersDTO result = userService.getFollowing(userId, null);

    // Assert
    assertNotNull(result);
    assertNotNull(result.getUserName());
    assertEquals(userId, result.getUserId());
    assertEquals("main_user", result.getUserName());

    assertNotNull(result.getFollowed());
    assertEquals(2, result.getFollowed().size());
    assertEquals(2, result.getFollowed().get(0).getUserId());
    assertEquals("following_1", result.getFollowed().get(0).getUserName());
    assertEquals(3, result.getFollowed().get(1).getUserId());
    assertEquals("following_2", result.getFollowed().get(1).getUserName());

    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, times(1)).findFollowingByUserId(userId);
}

    @Test
    @DisplayName("Deve retornar usuário e lista de following em ordem alfabética crescente")
    void testGetFollowingSuccess_OrderByUserNameAscending() {
        Integer userId = 1;

        User mainUser = new User();
        mainUser.setUserId(userId);
        mainUser.setUserName("main_user");

        User following1 = new User();
        following1.setUserId(2);
        following1.setUserName("alpha");

        User following2 = new User();
        following2.setUserId(3);
        following2.setUserName("bravo");

        User following3 = new User();
        following3.setUserId(4);
        following3.setUserName("charlie");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mainUser));
        when(userRepository.findFollowingByUserIdOrderByNameAsc(userId))
                .thenReturn(List.of(following1, following2, following3));

        UserWithFollowersDTO result = userService.getFollowing(userId, "name_asc");

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("main_user", result.getUserName());

        assertNotNull(result.getFollowed());
        assertEquals(3, result.getFollowed().size());
        assertEquals(2, result.getFollowed().get(0).getUserId());
        assertEquals("alpha", result.getFollowed().get(0).getUserName());
        assertEquals(3, result.getFollowed().get(1).getUserId());
        assertEquals("bravo", result.getFollowed().get(1).getUserName());
        assertEquals(4, result.getFollowed().get(2).getUserId());
        assertEquals("charlie", result.getFollowed().get(2).getUserName());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findFollowingByUserIdOrderByNameAsc(userId);
}

    @Test
    @DisplayName("Deve retornar usuário e lista de following em ordem alfabética decrescente")
    void testGetFollowingSuccess_OrderByUserNameDescending() {
        Integer userId = 1;

        User mainUser = new User();
        mainUser.setUserId(userId);
        mainUser.setUserName("main_user");

        User following1 = new User();
        following1.setUserId(2);
        following1.setUserName("charlie");

        User following2 = new User();
        following2.setUserId(3);
        following2.setUserName("bravo");

        User following3 = new User();
        following3.setUserId(4);
        following3.setUserName("alpha");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mainUser));
        when(userRepository.findFollowingByUserIdOrderByNameDesc(userId))
                .thenReturn(List.of(following1, following2, following3));

        UserWithFollowersDTO result = userService.getFollowing(userId, "name_desc");

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("main_user", result.getUserName());

        assertNotNull(result.getFollowed());
        assertEquals(3, result.getFollowed().size());
        assertEquals(2, result.getFollowed().get(0).getUserId());
        assertEquals("charlie", result.getFollowed().get(0).getUserName());
        assertEquals(3, result.getFollowed().get(1).getUserId());
        assertEquals("bravo", result.getFollowed().get(1).getUserName());
        assertEquals(4, result.getFollowed().get(2).getUserId());
        assertEquals("alpha", result.getFollowed().get(2).getUserName());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findFollowingByUserIdOrderByNameDesc(userId);
    }

@Test
@DisplayName("Deve lançar exceção quando order de following for inválido")
void testGetFollowing_ShouldThrowException_WhenOrderIsInvalid() {
    Integer userId = 1;
    String order = "invalid_order";

    User mainUser = new User();
    mainUser.setUserId(userId);
    mainUser.setUserName("main_user");

    when(userRepository.findById(userId)).thenReturn(Optional.of(mainUser));

    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.getFollowing(userId, order)
    );

    assertEquals("Order inválido: " + order, exception.getMessage());
    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, never()).findFollowingByUserId(anyInt());
    verify(userRepository, never()).findFollowingByUserIdOrderByNameAsc(anyInt());
    verify(userRepository, never()).findFollowingByUserIdOrderByNameDesc(anyInt());
}

@Test
@DisplayName("Deve lançar exceção quando tentar buscar following de usuário inexistente")
void testGetFollowingUserNotFound() {
    // Arrange
    Integer userId = 999;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.getFollowing(userId, null)
    );

    assertEquals("Usuário não encontrado: " + userId, exception.getMessage());
    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, never()).findFollowingByUserId(anyInt());
}


//    @Test
//    @DisplayName("Deve retornar top users ordenados por número de seguidores")
//    void testGetTopUsers_Success() {
// 
//        int limit = 3;
//        List<User> users = List.of(user3, user1, user2);
//        when(userRepository.findAllByOrderByFollowersCountDesc(PageRequest.of(0, limit)))
//                .thenReturn(users);
//
// 
//        List<UserDTO> result = userService.getTopUsers(limit);
//
// 
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
// 
//        int limit = 5;
//        when(userRepository.findAllByOrderByFollowersCountDesc(PageRequest.of(0, limit)))
//                .thenReturn(List.of());
//
// 
//        List<UserDTO> result = userService.getTopUsers(limit);
//
// 
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//        verify(userRepository, times(1))
//                .findAllByOrderByFollowersCountDesc(PageRequest.of(0, limit));
//    }

//    @Test
//    @DisplayName("Deve retornar top users com detalhes completos")
//    void testGetTopUsersWithDetails_Success() {
// 
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
// 
//        List<UserDTO> result = userService.getTopUsersWithDetails(limit);
//
// 
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
// 
//        Integer userId = 1;
//        user1.getFollowers().add(user2);
//        user1.getFollowing().add(user3);
//
//        when(userRepository.findByIdWithAllRelations(userId))
//                .thenReturn(Optional.of(user1));
//
// 
//        UserDTO result = userService.getUserWithDetails(userId);
//
// 
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
// 
//        Integer userId = 999;
//        when(userRepository.findByIdWithAllRelations(userId))
//                .thenReturn(Optional.empty());
//
//  & Assert
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
// 
//        Integer userId = 1;
//        List<User> followers = List.of(user2, user3);
//        when(userRepository.findFollowersByUserId(userId))
//                .thenReturn(followers);
//
// 
//        List<UserSimpleDTO> result = userService.getFollowers(userId);
//
// 
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
// 
//        Integer userId = 1;
//        when(userRepository.findFollowersByUserId(userId))
//                .thenReturn(List.of());
//
// 
//        List<UserSimpleDTO> result = userService.getFollowers(userId);
//
// 
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//        verify(userRepository, times(1)).findFollowersByUserId(userId);
//    }
//
//    @Test
//    @DisplayName("Deve retornar lista de usuários que o usuário segue")
//    void testGetFollowing_Success() {
// 
//        Integer userId = 1;
//        List<User> following = List.of(user2, user3);
//        when(userRepository.findFollowingByUserId(userId))
//                .thenReturn(following);
//
// 
//        List<UserSimpleDTO> result = userService.getFollowing(userId);
//
// 
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
// 
//        Integer userId = 1;
//        when(userRepository.findFollowingByUserId(userId))
//                .thenReturn(List.of());
//
// 
//        List<UserSimpleDTO> result = userService.getFollowing(userId);
//
// 
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//        verify(userRepository, times(1)).findFollowingByUserId(userId);
//    }
//
//    @Test
//    @DisplayName("Deve lidar com limite de 1 usuário corretamente")
//    void testGetTopUsers_LimitOne() {
// 
//        int limit = 1;
//        List<User> users = List.of(user3);
//        when(userRepository.findAllByOrderByFollowersCountDesc(PageRequest.of(0, limit)))
//                .thenReturn(users);
//
// 
//        List<UserDTO> result = userService.getTopUsers(limit);
//
// 
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals("pedro_oliveira", result.get(0).getUserName());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getUsersWithPosts vazio (não implementado)")
//    void testGetUsersWithPosts_NotImplemented() {
// 
//        List<User> result = userService.getUsersWithPosts();
//
// 
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getUsersByIds vazio (não implementado)")
//    void testGetUsersByIds_NotImplemented() {
// 
//        List<User> result = userService.getUsersByIds(List.of(1, 2, 3));
//
// 
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getUsersNotFollowingAnyone vazio (não implementado)")
//    void testGetUsersNotFollowingAnyone_NotImplemented() {
// 
//        List<User> result = userService.getUsersNotFollowingAnyone();
//
// 
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getUsersWithoutFollowers vazio (não implementado)")
//    void testGetUsersWithoutFollowers_NotImplemented() {
// 
//        List<User> result = userService.getUsersWithoutFollowers();
//
// 
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar searchUsers vazio (não implementado)")
//    void testSearchUsers_NotImplemented() {
// 
//        List<User> result = userService.searchUsers("joao");
//
// 
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve executar updateFollowersCount sem erro (não implementado)")
//    void testUpdateFollowersCount_NotImplemented() {
//  & Assert
//        assertDoesNotThrow(() -> userService.updateFollowersCount(1));
//    }
//
//    @Test
//    @DisplayName("Deve retornar createUser nulo (não implementado)")
//    void testCreateUser_NotImplemented() {
// 
//        User result = userService.createUser(user1);
//
// 
//        assertNull(result);
//    }
//
//    @Test
//    @DisplayName("Deve retornar getUserById vazio (não implementado)")
//    void testGetUserById_NotImplemented() {
// 
//        Optional<User> result = userService.getUserById(1);
//
// 
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getUserByUserName vazio (não implementado)")
//    void testGetUserByUserName_NotImplemented() {
// 
//        Optional<User> result = userService.getUserByUserName("joao_silva");
//
// 
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getAllUsers vazio (não implementado)")
//    void testGetAllUsers_NotImplemented() {
// 
//        List<User> result = userService.getAllUsers();
//
// 
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getAllUsersOrdered vazio (não implementado)")
//    void testGetAllUsersOrdered_NotImplemented() {
// 
//        List<User> result = userService.getAllUsersOrdered("name_asc");
//
// 
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve executar deleteUser sem erro (não implementado)")
//    void testDeleteUser_NotImplemented() {
//  & Assert
//        assertDoesNotThrow(() -> userService.deleteUser(1));
//    }
//
//    @Test
//    @DisplayName("Deve retornar existsUser falso (não implementado)")
//    void testExistsUser_NotImplemented() {
// 
//        boolean result = userService.existsUser(1);
//
// 
//        assertFalse(result);
//    }
//
//    @Test
//    @DisplayName("Deve retornar existsUserByUserName falso (não implementado)")
//    void testExistsUserByUserName_NotImplemented() {
// 
//        boolean result = userService.existsUserByUserName("joao_silva");
//
// 
//        assertFalse(result);
//    }
//
//    @Test
//    @DisplayName("Deve retornar countUsers zero (não implementado)")
//    void testCountUsers_NotImplemented() {
// 
//        long result = userService.countUsers();
//
// 
//        assertEquals(0, result);
//    }
//
//    @Test
//    @DisplayName("Deve retornar followUser nulo (não implementado)")
//    void testFollowUser_NotImplemented() {
// 
//        User result = userService.followUser(1, 2);
//
// 
//        assertNull(result);
//    }
//
//    @Test
//    @DisplayName("Deve retornar unfollowUser nulo (não implementado)")
//    void testUnfollowUser_NotImplemented() {
// 
//        User result = userService.unfollowUser(1, 2);
//
// 
//        assertNull(result);
//    }
//
//    @Test
//    @DisplayName("Deve retornar isFollowing falso (não implementado)")
//    void testIsFollowing_NotImplemented() {
// 
//        boolean result = userService.isFollowing(1, 2);
//
// 
//        assertFalse(result);
//    }
//
//    @Test
//    @DisplayName("Deve retornar getFollowersCount nulo (não implementado)")
//    void testGetFollowersCount_NotImplemented() {
// 
//        Integer result = userService.getFollowersCount(1);
//
// 
//        assertNull(result);
//    }
//
//    @Test
//    @DisplayName("Deve retornar getFollowersOrdered vazio (não implementado)")
//    void testGetFollowersOrdered_NotImplemented() {
// 
//        List<User> result = userService.getFollowersOrdered(1, "name_asc");
//
// 
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getFollowingOrdered vazio (não implementado)")
//    void testGetFollowingOrdered_NotImplemented() {
// 
//        List<User> result = userService.getFollowingOrdered(1, "name_asc");
//
// 
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getFollowingCount nulo (não implementado)")
//    void testGetFollowingCount_NotImplemented() {
// 
//        Integer result = userService.getFollowingCount(1);
//
// 
//        assertNull(result);
//    }
//
//    @Test
//    @DisplayName("Deve retornar getMutualFollowers vazio (não implementado)")
//    void testGetMutualFollowers_NotImplemented() {
// 
//        List<User> result = userService.getMutualFollowers(1);
//
// 
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deve retornar getSuggestedUsers vazio (não implementado)")
//    void testGetSuggestedUsers_NotImplemented() {
// 
//        List<User> result = userService.getSuggestedUsers(1);
//
// 
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
}