package com.meli.social.user.impl;

import com.meli.social.user.dto.UserDTO;
import com.meli.social.user.dto.UserSimpleDTO;
import com.meli.social.user.dto.UserWithFollowedDTO;
import com.meli.social.user.dto.UserWithFollowersDTO;
import com.meli.social.user.inter.IFollowService;
import com.meli.social.user.inter.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operações relacionadas a usuários e relacionamentos de follow")
public class UserController {

    private final IUserService userService;
    private final IFollowService followService;

    @PostMapping
    @Operation(summary = "Criar um novo usuário", description = "Cria um usuário a partir do campo 'userName' no corpo da requisição")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado", content = @Content(schema = @Schema(implementation = UserSimpleDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content)
    })
    public ResponseEntity<UserSimpleDTO> createNewUser(@RequestBody Map<String, String> request) {
        String userName = request.get("userName");

        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("Username é obrigatório");
        }

        UserSimpleDTO createdUser = userService.createUser(userName);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/top")
    @Operation(summary = "Listar top usuários", description = "Retorna os usuários ordenados por relevância (ex.: número de seguidores)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso", content = @Content(schema = @Schema(implementation = UserDTO.class)))
    })
    public ResponseEntity<List<UserDTO>> getTopUsers(
            @Parameter(description = "Quantidade máxima de usuários retornados", example = "10")
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(userService.getTopUsers(limit));
    }

    @PostMapping("/{userId}/follow/{userIdToFollow}")
    @Operation(summary = "Seguir um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Follow realizado" , content = @Content),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public ResponseEntity<Void> followUser(
            @Parameter(description = "ID do usuário que irá seguir", example = "1")
            @PathVariable Integer userId,
            @Parameter(description = "ID do usuário a ser seguido", example = "2")
            @PathVariable Integer userIdToFollow
    ) {
        followService.followUser(userId, userIdToFollow);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/unfollow/{userIdToUnfollow}")
    @Operation(summary = "Deixar de seguir um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unfollow realizado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public ResponseEntity<Void> unfollowUser(
            @Parameter(description = "ID do usuário que irá deixar de seguir", example = "1")
            @PathVariable Integer userId,
            @Parameter(description = "ID do usuário a deixar de seguir", example = "2")
            @PathVariable Integer userIdToUnfollow
    ) {
        followService.unfollowUser(userId, userIdToUnfollow);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/followers/count")
    @Operation(summary = "Contagem de seguidores", description = "Retorna o usuário com a contagem de seguidores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contagem retornada", content = @Content(schema = @Schema(implementation = UserSimpleDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public ResponseEntity<UserSimpleDTO> getUserWithFollowersCount(
            @Parameter(description = "ID do usuário", example = "1")
            @PathVariable Integer userId
    ) {
        return ResponseEntity.ok(followService.returnUserWithFollowerCounter(userId));
    }

    @GetMapping("/{userId}/followed/list")
    @Operation(summary = "Listar seguidos", description = "Retorna a lista de usuários seguidos por um usuário. Parâmetro 'order' pode definir ordenação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada", content = @Content(schema = @Schema(implementation = UserWithFollowedDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public ResponseEntity<UserWithFollowedDTO> getFollowing(
            @Parameter(description = "ID do usuário", example = "1")
            @PathVariable Integer userId,
            @Parameter(description = "Ordenação (ex.: name_asc / name_desc)")
            @RequestParam(required = false) String order) {
        return ResponseEntity.ok(userService.getFollowing(userId, order));
    }

    @GetMapping("/{userId}/followers/list")
    @Operation(summary = "Listar seguidores", description = "Retorna a lista de seguidores de um usuário. Parâmetro 'order' pode definir ordenação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada", content = @Content(schema = @Schema(implementation = UserWithFollowersDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    public ResponseEntity<UserWithFollowersDTO> getFollowers(
            @Parameter(description = "ID do usuário", example = "1")
            @PathVariable Integer userId,
            @Parameter(description = "Ordenação (ex.: name_asc / name_desc)")
            @RequestParam(required = false) String order
    ) {
        return ResponseEntity.ok(userService.getFollowers(userId, order));
    }

}