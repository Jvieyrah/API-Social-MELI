package com.meli.social.unit.service;

import com.meli.social.user.dto.UserSimpleDTO;
import com.meli.social.user.dto.UserWithFollowedDTO;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
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

    assertEquals("Usuário já existe: " + userName, exception.getMessage());
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

    assertEquals("Nome do usuário não pode ser vazio", exception.getMessage());
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

    assertEquals("Nome do usuário não pode ser vazio", exception.getMessage());
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
    UserWithFollowedDTO result = userService.getFollowing(userId, null);

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
@DisplayName("Deve paginar followers corretamente")
void testGetFollowersPagination() {
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

    User follower3 = new User();
    follower3.setUserId(4);
    follower3.setUserName("follower_3");

    when(userRepository.findById(userId)).thenReturn(Optional.of(mainUser));
    when(userRepository.findFollowersByUserId(eq(userId), any(PageRequest.class)))
            .thenReturn(new PageImpl<>(List.of(follower1, follower2), PageRequest.of(0, 2), 3));

    UserWithFollowersDTO page0 = userService.getFollowers(userId, null, 0, 2);
    assertNotNull(page0.getFollowers());
    assertEquals(2, page0.getFollowers().size());
    assertEquals(2, page0.getFollowers().get(0).getUserId());
    assertEquals(3, page0.getFollowers().get(1).getUserId());

    when(userRepository.findFollowersByUserId(eq(userId), any(PageRequest.class)))
            .thenReturn(new PageImpl<>(List.of(follower3), PageRequest.of(1, 2), 3));

    UserWithFollowersDTO page1 = userService.getFollowers(userId, null, 1, 2);
    assertNotNull(page1.getFollowers());
    assertEquals(1, page1.getFollowers().size());
    assertEquals(4, page1.getFollowers().get(0).getUserId());

    verify(userRepository, times(2)).findById(userId);
    verify(userRepository, times(2)).findFollowersByUserId(eq(userId), any(PageRequest.class));
}

@Test
@DisplayName("Deve paginar following corretamente")
void testGetFollowingPagination() {
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
    when(userRepository.findFollowingByUserId(eq(userId), any(PageRequest.class)))
            .thenReturn(new PageImpl<>(List.of(following1), PageRequest.of(0, 1), 2));

    UserWithFollowedDTO page0 = userService.getFollowing(userId, null, 0, 1);
    assertNotNull(page0.getFollowed());
    assertEquals(1, page0.getFollowed().size());
    assertEquals(2, page0.getFollowed().get(0).getUserId());

    when(userRepository.findFollowingByUserId(eq(userId), any(PageRequest.class)))
            .thenReturn(new PageImpl<>(List.of(following2), PageRequest.of(1, 1), 2));

    UserWithFollowedDTO page1 = userService.getFollowing(userId, null, 1, 1);
    assertNotNull(page1.getFollowed());
    assertEquals(1, page1.getFollowed().size());
    assertEquals(3, page1.getFollowed().get(0).getUserId());

    verify(userRepository, times(2)).findById(userId);
    verify(userRepository, times(2)).findFollowingByUserId(eq(userId), any(PageRequest.class));
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

        UserWithFollowedDTO result = userService.getFollowing(userId, "name_asc");

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

        UserWithFollowedDTO result = userService.getFollowing(userId, "name_desc");

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

}