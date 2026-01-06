package com.meli.social.unit.service;

import com.meli.social.post.dto.PostDTO;
import com.meli.social.post.dto.ProductDTO;
import com.meli.social.post.impl.PostService;
import com.meli.social.post.inter.IProductRepository;
import com.meli.social.post.inter.IPostRepository;
import com.meli.social.post.model.Post;
import com.meli.social.post.model.Product;
import com.meli.social.user.inter.UserJpaRepository;
import com.meli.social.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService - Create Post Tests")
class PostServiceTest {

    @Mock
    private IPostRepository postRepository;

    @Mock
    private IProductRepository productRepository;

    @Mock
    private UserJpaRepository userRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        reset(postRepository, productRepository, userRepository);
    }

    @Test
    @DisplayName("Deve criar um novo post e gerar a data automaticamente quando date for nula (fuso São Paulo)")
    void testCreatePostSuccess_NullDate_ShouldGenerateDateInSaoPauloTimezone() {
        PostDTO dto = new PostDTO(
                1,
                null,
                new ProductDTO(1, "Cadeira Gamer", "Gamer", "Racer", "Red & Black", "Special Edition"),
                100,
                1500.50
        );

        User user = new User();
        user.setUserId(1);
        user.setUserName("test_user");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        Boolean result = postService.createPost(dto);

        assertTrue(result);
        verify(postRepository).save(any(Post.class));

        var postCaptor = org.mockito.ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();

        assertNotNull(savedPost.getDate());
        assertEquals(LocalDate.now(ZoneId.of("America/Sao_Paulo")), savedPost.getDate());
        assertNotNull(savedPost.getProduct());
        assertEquals(1, savedPost.getProduct().getProductId());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve criar um novo post e usar a data informada quando date for válida")
    void testCreatePostSuccess_ValidDate_ShouldUseReceivedDate() {
        PostDTO dto = new PostDTO(
                1,
                "29-04-2021",
                new ProductDTO(1, "Cadeira Gamer", "Gamer", "Racer", "Red & Black", "Special Edition"),
                100,
                1500.50
        );

        User user = new User();
        user.setUserId(1);
        user.setUserName("test_user");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        Boolean result = postService.createPost(dto);

        assertTrue(result);

        var postCaptor = org.mockito.ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();

        assertEquals(LocalDate.of(2021, 4, 29), savedPost.getDate());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção quando data informada for inválida")
    void testCreatePost_InvalidDate_ShouldThrowException() {
        PostDTO dto = new PostDTO(
                1,
                "not-a-date",
                new ProductDTO(1, "Cadeira Gamer", "Gamer", "Racer", "Red & Black", "Special Edition"),
                100,
                1500.50
        );

        User user = new User();
        user.setUserId(1);
        user.setUserName("test_user");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> postService.createPost(dto)
        );

        assertEquals("Data inválida: not-a-date", exception.getMessage());
        verify(userRepository, times(1)).findById(1);
        verify(postRepository, never()).save(any(Post.class));
        verifyNoInteractions(productRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando PostDTO for nulo")
    void testCreatePost_NullPostDTO() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> postService.createPost(null)
        );

        assertEquals("Post não pode ser nulo", exception.getMessage());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(postRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando userId for nulo")
    void testCreatePost_NullUserId() {
        PostDTO dto = new PostDTO(
                null,
                null,
                new ProductDTO(1, "Cadeira Gamer", "Gamer", "Racer", "Red & Black", "Special Edition"),
                100,
                1500.50
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> postService.createPost(dto)
        );

        assertEquals("UserId não pode ser nulo", exception.getMessage());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(postRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não for encontrado")
    void testCreatePost_UserNotFound() {
        PostDTO dto = new PostDTO(
                999,
                null,
                new ProductDTO(1, "Cadeira Gamer", "Gamer", "Racer", "Red & Black", "Special Edition"),
                100,
                1500.50
        );

        when(userRepository.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> postService.createPost(dto)
        );

        assertEquals("Usuário não encontrado: 999", exception.getMessage());
        verify(userRepository, times(1)).findById(999);
        verify(postRepository, never()).save(any(Post.class));
        verifyNoInteractions(productRepository);
    }

    @Test
    @DisplayName("Deve reutilizar o produto existente quando productId já existe na tabela products")
    void testCreatePost_ProductAlreadyExists_ShouldReuseExistingProduct() {
        PostDTO dto = new PostDTO(
                1,
                null,
                new ProductDTO(1, "Cadeira Gamer", "Gamer", "Racer", "Red & Black", "Special Edition"),
                100,
                1500.50
        );

        User user = new User();
        user.setUserId(1);
        user.setUserName("test_user");

        Product existing = new Product(1, "Existing Name", "Existing Type", "Existing Brand", "Existing Color", "Existing Notes");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(existing));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        Boolean result = postService.createPost(dto);

        assertTrue(result);

        var postCaptor = org.mockito.ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();

        assertNotNull(savedPost.getProduct());
        assertEquals(existing, savedPost.getProduct());
        verify(productRepository, never()).save(any(Product.class));
    }
}
