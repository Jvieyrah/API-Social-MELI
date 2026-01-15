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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "Operações relacionadas a publicações, feed e likes")
public class PostController {

    private final IPostService postService;

    @PostMapping("/publish")
    @Operation(summary = "Publicar post", description = "Cria uma nova publicação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post criado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<Void> publish(@Valid @RequestBody PostDTO postDTO) {
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
            @RequestParam(required = false) String order) {
        return ResponseEntity.ok(postService.getFollowedPosts(userId, order));
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
            @RequestParam Integer userId) {
        return ResponseEntity.ok(postService.getPromoProductsList(userId));
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
        postService.unlikePost(postId, userId);
        return ResponseEntity.ok().build();
    }

}
