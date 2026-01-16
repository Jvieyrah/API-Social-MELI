package com.meli.social.unit.service;

import com.meli.social.exception.PostNotFoundException;
import com.meli.social.exception.PostUnprocessableException;
import com.meli.social.exception.UserNotFoundException;
import com.meli.social.post.dto.FollowedPostsDTO;
import com.meli.social.post.dto.PostDTO;
import com.meli.social.post.dto.PostPromoDTO;
import com.meli.social.post.dto.PromoProducsListDTO;
import com.meli.social.post.dto.PromoProductsCountDTO;
import com.meli.social.post.dto.ProductDTO;
import com.meli.social.post.impl.PostService;
import com.meli.social.post.inter.IProductRepository;
import com.meli.social.post.inter.IPostRepository;
import com.meli.social.post.inter.PostLikeJpaRepository;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

    @Mock
    private PostLikeJpaRepository postLikeRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        reset(postRepository, productRepository, userRepository, postLikeRepository);
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

        when(postRepository.findPostsByUserIdInAndDateBetween(eq(List.of(2, 3, 4)), any(), any(), any(Pageable.class)))
                .thenReturn(followingPosts);

        FollowedPostsDTO result = postService.getFollowedPosts(1, null);

        assertNotNull(result);
        assertEquals(1, result.getUserId());
        assertEquals(3, result.getPosts().size());
        assertEquals(followingPosts.get(0), result.getPosts().get(0));
        assertEquals(followingPosts.get(1), result.getPosts().get(1));
        assertEquals(followingPosts.get(2), result.getPosts().get(2));

        verify(userRepository, times(1)).existsById(1);
        verify(userRepository, times(1)).findFollowingIdsByUserId(1);
        verify(postRepository, times(1)).findPostsByUserIdInAndDateBetween(eq(List.of(2, 3, 4)), any(), any(), any(Pageable.class));

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
        verify(postRepository, never()).findPostsByUserIdInAndDateBetween(anyList(), any(), any(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar PromoProductsCountDTO com userId, userName e promoProductsCount")
    void testGetPromoProductsCount_Success() {
        Integer userId = 1;
        long promoCount = 7L;

        User user = new User();
        user.setUserId(userId);
        user.setUserName("testUser");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.countPromoPostsByUserId(userId)).thenReturn(promoCount);

        PromoProductsCountDTO result = postService.getPromoProductsCount(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("testUser", result.getUserName());
        assertEquals(promoCount, result.getPromoProductsCount());

        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, times(1)).countPromoPostsByUserId(userId);
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException ao buscar promo products count de usuário inexistente")
    void testGetPromoProductsCount_UserNotFound() {
        Integer userId = 999;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> postService.getPromoProductsCount(userId)
        );

        assertEquals("Usuário não encontrado: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, never()).countPromoPostsByUserId(any());
        verifyNoInteractions(productRepository);
    }

    @Test
    @DisplayName("Deve retornar PromoProducsListDTO com posts promocionais mapeados")
    void testGetPromoProductsList_Success() {
        Integer userId = 1;

        User user = new User();
        user.setUserId(userId);
        user.setUserName("testUser");

        Product product = new Product(10, "Product 10", "Type", "Brand", "Color", "Notes");

        Post p1 = new Post();
        p1.setPostId(100);
        p1.setUser(user);
        p1.setDate(LocalDate.of(2026, 1, 1));
        p1.setProduct(product);
        p1.setCategory(1);
        p1.setPrice(99.99);
        p1.setHasPromo(true);
        p1.setDiscount(0.10);

        List<Post> posts = List.of(p1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findPromoPostsByUserId(eq(userId), any(Pageable.class))).thenReturn(posts);

        PromoProducsListDTO result = postService.getPromoProductsList(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("testUser", result.getUserName());
        assertNotNull(result.getPosts());
        assertEquals(1, result.getPosts().size());

        PostPromoDTO dto = result.getPosts().get(0);
        assertEquals(userId, dto.getUserId());
        assertEquals("2026-01-01", dto.getDate());
        assertNotNull(dto.getProduct());
        assertEquals(10, dto.getProduct().getProductId());
        assertEquals(1, dto.getCategory());
        assertEquals(99.99, dto.getPrice());
        assertTrue(dto.getHasPromo());
        assertEquals(0.10, dto.getDiscount());

        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, times(1)).findPromoPostsByUserId(eq(userId), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException ao buscar promo products list de usuário inexistente")
    void testGetPromoProductsList_UserNotFound() {
        Integer userId = 999;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> postService.getPromoProductsList(userId)
        );

        assertEquals("Usuário não encontrado: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, never()).findPromoPostsByUserId(any());
        verifyNoInteractions(productRepository);
    }

    @Test
    @DisplayName("Deve curtir um post com sucesso e incrementar likesCount")
    void testLikePost_Success() {
        Integer userId = 1;
        Integer postId = 10;

        User user = new User();
        user.setUserId(userId);
        user.setUserName("testUser");

        Post post = new Post();
        post.setPostId(postId);
        post.setUser(user);
        post.setDate(LocalDate.of(2026, 1, 1));
        post.setCategory(1);
        post.setPrice(10.0);
        post.setLikesCount(0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postLikeRepository.existsByUser_UserIdAndPost_PostId(userId, postId)).thenReturn(false);
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> postService.likePost(postId, userId));

        var postCaptor = org.mockito.ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        Post saved = postCaptor.getValue();

        assertEquals(1, saved.getLikesCount());
        verify(postLikeRepository, times(1)).save(any());

        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, times(1)).findById(postId);
        verify(postLikeRepository, times(1)).existsByUser_UserIdAndPost_PostId(userId, postId);
    }

    @Test
    @DisplayName("Deve lançar PostUnprocessableException ao curtir um post já curtido")
    void testLikePost_AlreadyLiked_ShouldThrow() {
        Integer userId = 1;
        Integer postId = 10;

        User user = new User();
        user.setUserId(userId);
        user.setUserName("testUser");

        Post post = new Post();
        post.setPostId(postId);
        post.setUser(user);
        post.setDate(LocalDate.of(2026, 1, 1));
        post.setCategory(1);
        post.setPrice(10.0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postLikeRepository.existsByUser_UserIdAndPost_PostId(userId, postId)).thenReturn(true);

        PostUnprocessableException ex = assertThrows(
                PostUnprocessableException.class,
                () -> postService.likePost(postId, userId)
        );

        assertEquals("Usuário %d já curtiu o post %d".formatted(userId, postId), ex.getMessage());
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Deve remover curtida com sucesso e decrementar likesCount")
    void testUnlikePost_Success() {
        Integer userId = 1;
        Integer postId = 10;

        User user = new User();
        user.setUserId(userId);
        user.setUserName("testUser");

        Post post = new Post();
        post.setPostId(postId);
        post.setUser(user);
        post.setDate(LocalDate.of(2026, 1, 1));
        post.setCategory(1);
        post.setPrice(10.0);
        post.setLikesCount(1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postLikeRepository.deleteByUser_UserIdAndPost_PostId(userId, postId)).thenReturn(1L);
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> postService.unlikePost(postId, userId));

        var postCaptor = org.mockito.ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        Post saved = postCaptor.getValue();
        assertEquals(0, saved.getLikesCount());

        verify(postLikeRepository, times(1)).deleteByUser_UserIdAndPost_PostId(userId, postId);
    }

    @Test
    @DisplayName("Deve lançar PostUnprocessableException ao remover curtida inexistente")
    void testUnlikePost_NotLiked_ShouldThrow() {
        Integer userId = 1;
        Integer postId = 10;

        User user = new User();
        user.setUserId(userId);
        user.setUserName("testUser");

        Post post = new Post();
        post.setPostId(postId);
        post.setUser(user);
        post.setDate(LocalDate.of(2026, 1, 1));
        post.setCategory(1);
        post.setPrice(10.0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postLikeRepository.deleteByUser_UserIdAndPost_PostId(userId, postId)).thenReturn(0L);

        PostUnprocessableException ex = assertThrows(
                PostUnprocessableException.class,
                () -> postService.unlikePost(postId, userId)
        );

        assertEquals("Usuário %d não curtiu o post %d".formatted(userId, postId), ex.getMessage());
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Deve lançar PostNotFoundException ao curtir post inexistente")
    void testLikePost_PostNotFound_ShouldThrow() {
        Integer userId = 1;
        Integer postId = 999;

        User user = new User();
        user.setUserId(userId);
        user.setUserName("testUser");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        PostNotFoundException ex = assertThrows(
                PostNotFoundException.class,
                () -> postService.likePost(postId, userId)
        );

        assertEquals("Post não encontrado: " + postId, ex.getMessage());
        verify(postRepository, never()).save(any(Post.class));
    }

}
