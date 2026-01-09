package com.meli.social.unit.service;

import com.meli.social.exception.UserNotFoundException;
import com.meli.social.post.dto.FollowedPostsDTO;
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
import java.util.ArrayList;
import java.util.List;
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

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
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

    @Test
    @DisplayName("Deve retornar uma lista de publicações dos followings do user (quem o user segue) nas duas ultimas semanas")
    void testFollowingsPostListingSuccess_2WeeksAgoWithNoSortParameters() {
        User user = new User();
        user.setUserId(1);
        user.setUserName("test_user");

        User user2 = new User();
        user2.setUserId(2);
        user2.setUserName("test_user2");

        User user3 = new User();
        user3.setUserId(3);
        user3.setUserName("test_user3");

        User user4 = new User();
        user4.setUserId(4);
        user4.setUserName("test_user4");


        when(userRepository.existsById(1)).thenReturn(true);
        when(userRepository.findFollowingIdsByUserId(1)).thenReturn(List.of(2, 3, 4));

        Post p1 = new Post();
        p1.setPostId(null);
        p1.setDate(LocalDate.now().minusWeeks(2));
        p1.setProduct(new Product(1, "Product 1", "Type 1", "Brand 1", "Color 1", "Notes 1"));
        p1.setUser(user2);
        p1.setCategory(1);
        p1.setPrice(1200.50);
        p1.setHasPromo(false);
        p1.setDiscount(null);
        p1.setLikesCount(0);

        Post p2 = new Post();
        p2.setPostId(null);
        p2.setDate(LocalDate.now().minusDays(3));
        p2.setProduct(new Product(2, "Product 2", "Type 2", "Brand 2", "Color 2", "Notes 2"));
        p2.setUser(user3);
        p2.setCategory(2);
        p2.setPrice(599.99);
        p2.setHasPromo(false);
        p2.setDiscount(null);
        p2.setLikesCount(0);

        Post p3 = new Post();
        p3.setPostId(null);
        p3.setDate(LocalDate.now().minusWeeks(1));
        p3.setProduct(new Product(3, "Product 3", "Type 3", "Brand 3", "Color 3", "Notes 3"));
        p3.setUser(user2);
        p3.setCategory(3);
        p3.setPrice(399.99);
        p3.setHasPromo(false);
        p3.setDiscount(null);
        p3.setLikesCount(0);

        List<Post> followingPosts = List.of(p1, p2, p3);

        when(postRepository.findPostsByUserIdInAndDateBetween(eq(List.of(2, 3, 4)), any(), any(), any())).thenReturn(followingPosts);

        FollowedPostsDTO result = postService.getFollowedPosts(1, null);

        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals(3, result.getPosts().size());
        assertEquals(followingPosts.get(0), result.getPosts().get(0));
        assertEquals(followingPosts.get(1), result.getPosts().get(1));
        assertEquals(followingPosts.get(2), result.getPosts().get(2));

        verify(userRepository, times(1)).existsById(1);
        verify(userRepository, times(1)).findFollowingIdsByUserId(1);


        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> postService.getFollowedPosts(999, null)
        );

        assertEquals("Usuário não encontrado: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar um FollowedPostsDTO vazio quando não houver followings")
    void testFollowingsPostListing_whenUserHasNoFollowings () {

        when(userRepository.existsById(999)).thenReturn(true);
        when(userRepository.findFollowingIdsByUserId(999)).thenReturn(new ArrayList<>());

        FollowedPostsDTO result = postService.getFollowedPosts(999, null);

        assertNotNull(result);
        assertEquals(999, result.getUserId());
        assertNull(result.getPosts());

        verify(userRepository, times(1)).existsById(999);
        verify(userRepository, times(1)).findFollowingIdsByUserId(999);
        verify(postRepository, never()).findPostsByUserIdInAndDateBetween(anyList(), any(), any(), any());
    }

}
