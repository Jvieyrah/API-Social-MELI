package com.meli.social.unit.service;

import com.meli.social.exception.UserNotFoundException;
import com.meli.social.user.dto.UserSimpleDTO;
import com.meli.social.user.impl.FollowService;
import com.meli.social.user.model.User;
import com.meli.social.user.inter.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FollowService - Create follow Tests")
public class FollowServiceTest {
    @Mock
    private UserJpaRepository userRepository;

    @InjectMocks
    private FollowService followService;

    @BeforeEach
    void setUp() {
        reset(userRepository);
    }

    private User createUser(Integer userId, String userName) {
        User user = new User(userName);
        user.setUserId(userId);
        user.setFollowersCount(0);
        user.setFollowers(new HashSet<>());
        user.setFollowing(new HashSet<>());
        return user;
    }


    @Test
    @DisplayName("Deve retornar 'true' se um usuário segue outro e 'false' caso não o siga ")
    void testIsFollowing() {

        User savedUserA = createUser(1, "test_userA");
        User savedUserB = createUser(2, "test_userB");

        when(userRepository.isFollowing(savedUserA.getUserId(),savedUserB.getUserId())).thenReturn(true);
        when(userRepository.isFollowing(savedUserB.getUserId(),savedUserA.getUserId())).thenReturn(false);

        Boolean resultExpectedTrue =  followService.isFollowing(savedUserA.getUserId(),savedUserB.getUserId());
        Boolean resultExpectedFalse =  followService.isFollowing(savedUserB.getUserId(),savedUserA.getUserId());

        assertEquals(true , resultExpectedTrue);
        assertEquals(false, resultExpectedFalse);
    }

    @Test
    @DisplayName("Deve retornar o usuário que segue outro")
    void testFollowUser() {
       
        User userA = createUser(1, "test_userA");
        User userB = createUser(2, "test_userB");

        when(userRepository.isFollowing(userA.getUserId(), userB.getUserId())).thenReturn(false);
        when(userRepository.findById(1)).thenReturn(Optional.of(userA));
        when(userRepository.findById(2)).thenReturn(Optional.of(userB));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

 
        User result = followService.followUser(userA.getUserId(), userB.getUserId());

 
        assertNotNull(result);
        assertEquals(userA, result);
        assertEquals(1, result.getFollowing().size());
        assertEquals(1, userB.getFollowersCount());

 
        verify(userRepository, times(1)).isFollowing(userA.getUserId(), userB.getUserId());
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).findById(2);
        verify(userRepository, times(1)).save(userA);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando usuário tenta seguir a si mesmo")
    void testFollowUser_ShouldThrowIllegalArgumentException_WhenUserTriesToFollowHimself() {
       
        Integer userId = 1;

 
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> followService.followUser(userId, userId),
                "Deveria lançar IllegalArgumentException quando usuário tenta seguir a si mesmo"
        );

        // Verificar mensagem
        assertEquals("Os IDs de usuário não podem ser iguais!", exception.getMessage());

        // Verificar que nenhuma operação foi executada
        verify(userRepository, never()).isFollowing(any(), any());
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando usuário já segue o outro")
    void testFollowUser_ShouldThrowIllegalArgumentException_WhenAlreadyFollowing() {
       
        Integer followerId = 1;
        Integer followedId = 2;

        when(userRepository.isFollowing(followerId, followedId)).thenReturn(true);

 
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> followService.followUser(followerId, followedId),
                "Deveria lançar IllegalArgumentException quando usuário já segue o outro"
        );

        // Verificar mensagem
        String expectedMessage = "Usuário %d já segue o usuário %d".formatted(followerId, followedId);
        assertEquals(expectedMessage, exception.getMessage());

        // Verificar que isFollowing foi chamado mas não tentou buscar os usuários
        verify(userRepository, times(1)).isFollowing(followerId, followedId);
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando follower não existe")
    void testFollowUser_ShouldThrowUserNotFoundException_WhenFollowerDoesNotExist() {
       
        Integer followerId = 999;
        Integer followedId = 2;

        when(userRepository.isFollowing(followerId, followedId)).thenReturn(false);
        when(userRepository.findById(followerId)).thenReturn(Optional.empty());

 
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> followService.followUser(followerId, followedId),
                "Deveria lançar UserNotFoundException quando follower não existe"
        );

        // Verificar mensagem
        assertEquals("Seguidor não encontrado", exception.getMessage());

        // Verificar chamadas
        verify(userRepository, times(1)).isFollowing(followerId, followedId);
        verify(userRepository, times(1)).findById(followerId);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando followed não existe")
    void testFollowUser_ShouldThrowUserNotFoundException_WhenFollowedDoesNotExist() {
       
        Integer followerId = 1;
        Integer followedId = 999;

        User followerUser = createUser(followerId, "test_follower");

        when(userRepository.isFollowing(followerId, followedId)).thenReturn(false);
        when(userRepository.findById(followerId)).thenReturn(Optional.of(followerUser));
        when(userRepository.findById(followedId)).thenReturn(Optional.empty());

 
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> followService.followUser(followerId, followedId),
                "Deveria lançar UserNotFoundException quando followed não existe"
        );

        // Verificar mensagem
        assertEquals("Usuário a ser seguido não encontrado", exception.getMessage());

        // Verificar chamadas
        verify(userRepository, times(1)).isFollowing(followerId, followedId);
        verify(userRepository, times(1)).findById(followerId);
        verify(userRepository, times(1)).findById(followedId);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando ambos os usuários não existem")
    void testFollowUser_ShouldThrowUserNotFoundException_WhenBothUsersDoNotExist() {
       
        Integer followerId = 888;
        Integer followedId = 999;

        when(userRepository.isFollowing(followerId, followedId)).thenReturn(false);
        when(userRepository.findById(followerId)).thenReturn(Optional.empty());

 
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> followService.followUser(followerId, followedId),
                "Deveria lançar UserNotFoundException quando follower não existe (primeiro a ser verificado)"
        );

        // Verificar que lança exceção do follower primeiro
        assertEquals("Seguidor não encontrado", exception.getMessage());

        // Verificar que não chegou a verificar o followed
        verify(userRepository, times(1)).findById(followerId);
        verify(userRepository, never()).findById(followedId);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando followerId é null no isFollowing")
    void testIsFollowing_ShouldThrowIllegalArgumentException_WhenFollowerIdIsNull() {
       
        Integer followerId = null;
        Integer followedId = 2;

 
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> followService.isFollowing(followerId, followedId),
                "Deveria lançar IllegalArgumentException quando followerId é null"
        );

        // Verificar mensagem
        assertEquals("IDs não podem ser nulos", exception.getMessage());

        // Verificar que repository não foi chamado
        verify(userRepository, never()).isFollowing(any(), any());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando followedId é null no isFollowing")
    void testIsFollowing_ShouldThrowIllegalArgumentException_WhenFollowedIdIsNull() {
       
        Integer followerId = 1;
        Integer followedId = null;

 
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> followService.isFollowing(followerId, followedId),
                "Deveria lançar IllegalArgumentException quando followedId é null"
        );

        // Verificar mensagem
        assertEquals("IDs não podem ser nulos", exception.getMessage());

        // Verificar que repository não foi chamado
        verify(userRepository, never()).isFollowing(any(), any());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando ambos os IDs são null no isFollowing")
    void testIsFollowing_ShouldThrowIllegalArgumentException_WhenBothIdsAreNull() {
       
        Integer followerId = null;
        Integer followedId = null;

 
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> followService.isFollowing(followerId, followedId),
                "Deveria lançar IllegalArgumentException quando ambos os IDs são null"
        );

        // Verificar mensagem
        assertEquals("IDs não podem ser nulos", exception.getMessage());

        // Verificar que repository não foi chamado
        verify(userRepository, never()).isFollowing(any(), any());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando followerId é null no followUser")
    void testFollowUser_ShouldThrowIllegalArgumentException_WhenFollowerIdIsNull() {
       
        Integer followerId = null;
        Integer followedId = 2;

 
        assertThrows(
                IllegalArgumentException.class,
                () -> followService.followUser(followerId, followedId),
                "Deveria lançar NullPointerException quando followerId é null no followUser"
        );

        // Verificar que nenhuma operação foi executada
        verify(userRepository, never()).isFollowing(any(), any());
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando followedId é null no followUser")
    void testFollowUser_ShouldThrowIllegalArgumentException_WhenFollowedIdIsNull() {
        Integer followerId = 1;
        Integer followedId = null;

        assertThrows(
                IllegalArgumentException.class,
                () -> followService.followUser(followerId, followedId),
                "Deveria lançar NullPointerException quando followedId é null no followUser"
        );

        verify(userRepository, never()).isFollowing(any(), any());
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar o usuário que deixa de seguir outro")
    void testUnfollowUser() {
       
        User userA = createUser(1, "test_userA");
        User userB = createUser(2, "test_userB");

        // Simular que userA já segue userB
        userA.follow(userB);

        when(userRepository.isFollowing(userA.getUserId(), userB.getUserId())).thenReturn(true);
        when(userRepository.findById(1)).thenReturn(Optional.of(userA));
        when(userRepository.findById(2)).thenReturn(Optional.of(userB));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

 
        User result = followService.unfollowUser(userA.getUserId(), userB.getUserId());

 
        assertNotNull(result);
        assertEquals(userA, result);
        assertEquals(0, result.getFollowing().size());
        assertEquals(0, userB.getFollowersCount());

 
        verify(userRepository, times(1)).isFollowing(userA.getUserId(), userB.getUserId());
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).findById(2);
        verify(userRepository, times(1)).save(userA);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando usuário tenta deixar de seguir a si mesmo")
    void testUnfollowUser_ShouldThrowIllegalArgumentException_WhenUserTriesToUnfollowHimself() {
       
        Integer userId = 1;

 
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> followService.unfollowUser(userId, userId),
                "Deveria lançar IllegalArgumentException quando usuário tenta deixar de seguir a si mesmo"
        );

        // Verificar mensagem
        assertEquals("Os IDs de usuário não podem ser iguais!", exception.getMessage());

        // Verificar que nenhuma operação foi executada
        verify(userRepository, never()).isFollowing(any(), any());
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando usuário não segue o outro")
    void testUnfollowUser_ShouldThrowIllegalArgumentException_WhenNotFollowing() {
       
        Integer followerId = 1;
        Integer followedId = 2;

        when(userRepository.isFollowing(followerId, followedId)).thenReturn(false);

 
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> followService.unfollowUser(followerId, followedId),
                "Deveria lançar IllegalArgumentException quando usuário não segue o outro"
        );

        // Verificar mensagem
        String expectedMessage = "Usuário %d não segue o usuário %d".formatted(followerId, followedId);
        assertEquals(expectedMessage, exception.getMessage());

        // Verificar que isFollowing foi chamado mas não tentou buscar os usuários
        verify(userRepository, times(1)).isFollowing(followerId, followedId);
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando follower não existe no unfollow")
    void testUnfollowUser_ShouldThrowUserNotFoundException_WhenFollowerDoesNotExist() {
       
        Integer followerId = 999;
        Integer followedId = 2;

        when(userRepository.isFollowing(followerId, followedId)).thenReturn(true);
        when(userRepository.findById(followerId)).thenReturn(Optional.empty());

 
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> followService.unfollowUser(followerId, followedId),
                "Deveria lançar UserNotFoundException quando follower não existe"
        );

        // Verificar mensagem
        assertEquals("Seguidor não encontrado", exception.getMessage());

        // Verificar chamadas
        verify(userRepository, times(1)).isFollowing(followerId, followedId);
        verify(userRepository, times(1)).findById(followerId);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando followed não existe no unfollow")
    void testUnfollowUser_ShouldThrowUserNotFoundException_WhenFollowedDoesNotExist() {
       
        Integer followerId = 1;
        Integer followedId = 999;

        User followerUser = createUser(followerId, "test_follower");

        when(userRepository.isFollowing(followerId, followedId)).thenReturn(true);
        when(userRepository.findById(followerId)).thenReturn(Optional.of(followerUser));
        when(userRepository.findById(followedId)).thenReturn(Optional.empty());

 
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> followService.unfollowUser(followerId, followedId),
                "Deveria lançar UserNotFoundException quando followed não existe"
        );

        // Verificar mensagem
        assertEquals("Usuário a ser deixado de seguir não encontrado", exception.getMessage());

        // Verificar chamadas
        verify(userRepository, times(1)).isFollowing(followerId, followedId);
        verify(userRepository, times(1)).findById(followerId);
        verify(userRepository, times(1)).findById(followedId);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando ambos os usuários não existem no unfollow")
    void testUnfollowUser_ShouldThrowUserNotFoundException_WhenBothUsersDoNotExist() {
       
        Integer followerId = 888;
        Integer followedId = 999;

        when(userRepository.isFollowing(followerId, followedId)).thenReturn(true);
        when(userRepository.findById(followerId)).thenReturn(Optional.empty());

 
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> followService.unfollowUser(followerId, followedId),
                "Deveria lançar UserNotFoundException quando follower não existe (primeiro a ser verificado)"
        );

        // Verificar que lança exceção do follower primeiro
        assertEquals("Seguidor não encontrado", exception.getMessage());

        // Verificar que não chegou a verificar o followed
        verify(userRepository, times(1)).findById(followerId);
        verify(userRepository, never()).findById(followedId);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando followerId é null no unfollowUser")
    void testUnfollowUser_ShouldThrowIllegalArgumentException_WhenFollowerIdIsNull() {
       
        Integer followerId = null;
        Integer followedId = 2;

 
        assertThrows(
                IllegalArgumentException.class,
                () -> followService.unfollowUser(followerId, followedId),
                "Deveria lançar IllegalArgumentException quando followerId é null no unfollowUser"
        );

        // Verificar que nenhuma operação foi executada
        verify(userRepository, never()).isFollowing(any(), any());
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando followedId é null no unfollowUser")
    void testUnfollowUser_ShouldThrowIllegalArgumentException_WhenFollowedIdIsNull() {
       
        Integer followerId = 1;
        Integer followedId = null;

 
        assertThrows(
                IllegalArgumentException.class,
                () -> followService.unfollowUser(followerId, followedId),
                "Deveria lançar IllegalArgumentException quando followedId é null no unfollowUser"
        );

        // Verificar que nenhuma operação foi executada
        verify(userRepository, never()).isFollowing(any(), any());
        verify(userRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve Retornar SimpleUserDTO quando usuário não está sendo seguindo")
    void testReturnUserWithFollowerCounter_WhenUserHaveZeroFolowers(){
        User userA = createUser(1, "test_userA");
        when(userRepository.findById(1)).thenReturn(Optional.of(userA));

        UserSimpleDTO userSimpleDTO = UserSimpleDTO.fromUserWithFollowers(userA);

        UserSimpleDTO result = followService.returnUserWithFollowerCounter(1);
        assertEquals(userSimpleDTO, result);
        assertEquals(userSimpleDTO.getFollowersCount(), result.getFollowersCount());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve Retornar SimpleUserDTO quando usuário está sendo seguindo")
    void testReturnUserWithFollowerCounter_WhenUserHaveFolowers(){
        User userA = createUser(1, "test_userA");
        User userB = createUser(2, "test_userB");
        userB.follow(userA);
        when(userRepository.findById(1)).thenReturn(Optional.of(userA));

        UserSimpleDTO userSimpleDTO = UserSimpleDTO.fromUserWithFollowers(userA);

        UserSimpleDTO result = followService.returnUserWithFollowerCounter(1);
        assertEquals(userSimpleDTO, result);
        assertEquals(1, result.getFollowersCount());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando usuários não existem")
    void testReturnUserNotFoundException_WhenUserDoesNotExist(){
        assertThrows(
                UserNotFoundException.class,
                () -> followService.returnUserWithFollowerCounter(99),
                "Usuário não encontrado"
        );
        verify(userRepository, times(1)).findById(99);
    }
}
