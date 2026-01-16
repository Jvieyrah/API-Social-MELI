package com.meli.social.post.impl;

import com.meli.social.exception.PostNotFoundException;
import com.meli.social.exception.PostUnprocessableException;
import com.meli.social.exception.UserNotFoundException;
import com.meli.social.post.dto.FollowedPostsDTO;
import com.meli.social.post.dto.PostDTO;
import com.meli.social.post.dto.PostPromoDTO;
import com.meli.social.post.dto.PromoProducsListDTO;
import com.meli.social.post.dto.PromoProductsCountDTO;
import com.meli.social.post.inter.IProductRepository;
import com.meli.social.post.inter.PostLikeJpaRepository;
import com.meli.social.post.inter.IPostRepository;
import com.meli.social.post.inter.IPostService;
import com.meli.social.post.model.Post;
import com.meli.social.post.model.PostLike;
import com.meli.social.post.model.Product;
import com.meli.social.user.inter.UserJpaRepository;
import com.meli.social.user.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService implements IPostService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    private final IPostRepository postRepository;
    private final IProductRepository productRepository;
    private final UserJpaRepository userRepository;
    private final PostLikeJpaRepository postLikeRepository;

    @Override
    @Transactional
    public Boolean createPost(PostDTO newPost) {
        if (newPost == null) {
            logger.warn("Invalid post creation request (null body)");
            throw new IllegalArgumentException("Post não pode ser nulo");
        }
        if (newPost.getUserId() == null) {
            logger.warn("Invalid post creation request (null userId)");
            throw new IllegalArgumentException("UserId não pode ser nulo");
        }

        logger.info("Creating post userId={} hasPromo={}", newPost.getUserId(), (newPost instanceof PostPromoDTO));

        User user = getUserOrThrow(newPost.getUserId());

        Post post = newPost.toEntity(user, resolveDate(newPost.getDate()));

        post.setProduct(resolveProduct(post.getProduct()));

        postRepository.save(post);
        logger.info("Post persisted userId={} postDate={}", user.getUserId(), post.getDate());
        return true;
    }

    @Override
    public FollowedPostsDTO getFollowedPosts(Integer userId, String sort) {
        return getFollowedPosts(userId, sort, 0, 100);
    }

    @Override
    public FollowedPostsDTO getFollowedPosts(Integer userId, String sort, int page, int size) {
        logger.info("Fetching feed userId={} sort={} page={} size={}", userId, sort, page, size);
        if (!userRepository.existsById(userId)){
            logger.warn("Feed requested for non-existing userId={}", userId);
            throw new UserNotFoundException("Usuário não encontrado: " + userId);
        }

        PageSizeValidation(page, size);

        FollowedPostsDTO followedPosts =  new FollowedPostsDTO();
        followedPosts.setUserId(userId);

        List<Integer> userFollows = userRepository.findFollowingIdsByUserId(userId);
        if (userFollows.isEmpty()) {
            logger.info("Feed empty (user follows nobody) userId={}", userId);
            return new FollowedPostsDTO(userId, null);
        }
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(14);
        Sort sortSpec = (sort == null || sort.isBlank() || sort.equalsIgnoreCase("date_desc"))
                ? Sort.by("date").descending()
                : sort.equalsIgnoreCase("date_asc") ? Sort.by("date").ascending() : Sort.by("date").descending();

        Pageable pageable = PageRequest.of(page, size, sortSpec);
        List<Post> posts = postRepository.findPostsByUserIdInAndDateBetween(userFollows, startDate, endDate, pageable);
        logger.info("Feed fetched userId={} postsCount={}", userId, posts == null ? 0 : posts.size());
        return new FollowedPostsDTO(userId, posts);
    }

    private static void PageSizeValidation(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page inválida: " + page);
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Size inválido: " + size);
        }
    }

    @Override
    public PromoProductsCountDTO getPromoProductsCount(Integer userId) {
        logger.info("Counting promo posts userId={}", userId);
        User user = getUserOrThrow(userId);

        long count = postRepository.countPromoPostsByUserId(userId);
        return new PromoProductsCountDTO(user.getUserId(), user.getUserName(), count);
    }

    @Override
    public PromoProducsListDTO getPromoProductsList(Integer userId) {
        return getPromoProductsList(userId, 0, 100);
    }

    @Override
    public PromoProducsListDTO getPromoProductsList(Integer userId, int page, int size) {
        logger.info("Listing promo posts userId={} page={} size={}", userId, page, size);
        PageSizeValidation(page, size);
        User user = getUserOrThrow(userId);

        Pageable pageable = PageRequest.of(page, size);
        List<Post> posts = postRepository.findPromoPostsByUserId(userId, pageable);
        List<PostPromoDTO> promoPostTreated = posts.stream()
                .map(PostPromoDTO::fromEntity)
                .collect(Collectors.toList());
        return new PromoProducsListDTO(user.getUserId(), user.getUserName(), promoPostTreated);
    }

    @Override
    @Transactional
    public void likePost(Integer postId, Integer userId) {
        logger.info("Liking post postId={} userId={}", postId, userId);
        validateNotNullOrThrow(postId, userId);

        User user = getUserOrThrow(userId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post não encontrado: " + postId));

        if (postLikeRepository.existsByUser_UserIdAndPost_PostId(userId, postId)) {
            logger.warn("Like rejected (already liked) postId={} userId={}", postId, userId);
            throw new PostUnprocessableException(
                    "Usuário %d já curtiu o post %d".formatted(userId, postId)
            );
        }

        PostLike like = new PostLike();
        like.setUser(user);
        like.setPost(post);

        postLikeRepository.save(like);

        post.setLikesCount((post.getLikesCount() == null ? 0 : post.getLikesCount()) + 1);
        postRepository.save(post);
        logger.info("Post liked postId={} userId={} likesCount={}", postId, userId, post.getLikesCount());
    }

    @Override
    @Transactional
    public void unlikePost(Integer postId, Integer userId) {
        logger.info("Unliking post postId={} userId={}", postId, userId);
        validateNotNullOrThrow(postId, userId);

        getUserOrThrow(userId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post não encontrado: " + postId));

        long deleted = postLikeRepository.deleteByUser_UserIdAndPost_PostId(userId, postId);
        if (deleted == 0) {
            logger.warn("Unlike rejected (not liked) postId={} userId={}", postId, userId);
            throw new PostUnprocessableException(
                    "Usuário %d não curtiu o post %d".formatted(userId, postId)
            );
        }

        post.setLikesCount(Math.max(0, (post.getLikesCount() == null ? 0 : post.getLikesCount()) - 1));
        postRepository.save(post);
        logger.info("Post unliked postId={} userId={} likesCount={}", postId, userId, post.getLikesCount());
    }

    private static void validateNotNullOrThrow(Integer postId, Integer userId) {
        if (postId == null || userId == null) {
            throw new IllegalArgumentException("IDs não podem ser nulos");
        }
    }

    private User getUserOrThrow(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado: " + userId));
    }

    private Product resolveProduct(Product product) {
        if (product == null) {
            return null;
        }
        if (product.getProductId() == null) {
            throw new IllegalArgumentException("ProductId não pode ser nulo");
        }

        return productRepository.findById(product.getProductId())
                .orElseGet(() -> productRepository.save(product));
    }

    private LocalDate resolveDate(String date) {
        ZoneId zoneId = ZoneId.of("America/Sao_Paulo");
        if (date == null || date.trim().isEmpty()) {
            return LocalDate.now(zoneId);
        }

        String trimmed = date.trim();
        try {
            return LocalDate.parse(trimmed);
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalDate.parse(trimmed, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Data inválida: " + date);
        }
    }

}
