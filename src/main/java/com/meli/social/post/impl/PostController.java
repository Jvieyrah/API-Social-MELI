package com.meli.social.post.impl;

import com.meli.social.exception.ErrorDTO;
import com.meli.social.post.dto.*;
import com.meli.social.post.inter.IPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "Operações relacionadas a publicações, feed e likes")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    private final IPostService postService;

    @PostMapping("/publish")
    @Operation(summary = "Publicar post", description = "Cria uma nova publicação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post criado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<Void> publish(@Valid @RequestBody PostDTO postDTO) {
        logger.info("Request to publish post userId={}", postDTO.getUserId());
        postService.createPost(postDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/promo-pub")
    @Operation(summary = "Publicar post promocional", description = "Cria uma nova publicação promocional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post promocional criado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<Void> publishPromo(@Valid @RequestBody PostPromoDTO postPromoDTO) {
        logger.info("Request to publish promo post userId={}", postPromoDTO.getUserId());
        postService.createPost(postPromoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/followed/{userId}/list")
    @Operation(summary = "Buscar feed", description = "Retorna publicações dos usuários seguidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feed retornado", content = @Content(schema = @Schema(implementation = FollowedPostsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public ResponseEntity<FollowedPostsDTO> getFeed(
            @Parameter(description = "ID do usuário", example = "1")
            @PathVariable Integer userId,
            @Parameter(description = "Ordenação (ex.: date_asc / date_desc)")
            @RequestParam(required = false) String order,
            @Parameter(description = "Número da página (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Request to get feed userId={} order={} page={} size={}", userId, order, page, size);
        return ResponseEntity.ok(postService.getFollowedPosts(userId, order, page, size));
    }

    @GetMapping("/promo-pub/count")
    @Operation(summary = "Contar posts promocionais", description = "Retorna a contagem de publicações promocionais de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contagem retornada", content = @Content(schema = @Schema(implementation = PromoProductsCountDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public ResponseEntity<PromoProductsCountDTO> getPromoProductsCount(
            @Parameter(description = "ID do usuário", example = "1")
            @RequestParam Integer userId) {
        logger.info("Request to get promo products count userId={}", userId);
        return ResponseEntity.ok(postService.getPromoProductsCount(userId));
    }

    @GetMapping("/promo-pub/list")
    @Operation(summary = "Listar posts promocionais", description = "Retorna as publicações promocionais de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada", content = @Content(schema = @Schema(implementation = PromoProducsListDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public ResponseEntity<PromoProducsListDTO> getPromoProductsList(
            @Parameter(description = "ID do usuário", example = "1")
            @RequestParam Integer userId,
            @Parameter(description = "Número da página (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Request to get promo products list userId={} page={} size={}", userId, page, size);
        return ResponseEntity.ok(postService.getPromoProductsList(userId, page, size));
    }

    @PostMapping("/{postId}/like/{userId}")
    @Operation(summary = "Curtir post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Like realizado", content = @Content),
            @ApiResponse(responseCode = "422", description = "Requisição não processável", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Post ou usuário não encontrado", content = @Content)
    })
    public ResponseEntity<Void> likePost(
            @Parameter(description = "ID do post", example = "100")
            @PathVariable Integer postId,
            @Parameter(description = "ID do usuário", example = "1")
            @PathVariable Integer userId
    ) {
        logger.info("Request to like postId={} userId={}", postId, userId);
        postService.likePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/unlike/{userId}")
    @Operation(summary = "Descurtir post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unlike realizado", content = @Content),
            @ApiResponse(responseCode = "422", description = "Requisição não processável", content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Post ou usuário não encontrado", content = @Content)
    })
    public ResponseEntity<Void> unlikePost(
            @Parameter(description = "ID do post", example = "100")
            @PathVariable Integer postId,
            @Parameter(description = "ID do usuário", example = "1")
            @PathVariable Integer userId
    ) {
        logger.info("Request to unlike postId={} userId={}", postId, userId);
        postService.unlikePost(postId, userId);
        return ResponseEntity.ok().build();
    }

}
