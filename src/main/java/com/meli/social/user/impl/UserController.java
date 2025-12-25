package com.meli.social.user.impl;

import com.meli.social.user.dto.UserDTO;
import com.meli.social.user.dto.UserSimpleDTO;
import com.meli.social.user.inter.IFollowService;
import com.meli.social.user.inter.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final IFollowService followService;

    @PostMapping
    public ResponseEntity<UserSimpleDTO> createNewUser(@RequestBody Map<String, String> request) {
        String userName = request.get("userName");

        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("Username é obrigatório");
        }

        UserSimpleDTO createdUser = userService.createUser(userName);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/top")
    public ResponseEntity<List<UserDTO>> getTopUsers(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(userService.getTopUsers(limit));
    }

//    @GetMapping("/top/details")
//    public ResponseEntity<List<UserDTO>> getTopUsersWithDetails(
//            @RequestParam(defaultValue = "10") int limit
//    ) {
//        return ResponseEntity.ok(userService.getTopUsersWithDetails(limit));
//    }

//    @GetMapping("/{userId}")
//    public ResponseEntity<UserDTO> getUserDetails(@PathVariable Integer userId) {
//        return ResponseEntity.ok(userService.getUserWithDetails(userId));
//    }

    @PostMapping("/{userId}/follow/{userIdToFollow}")
    public ResponseEntity<Void> followUser(
            @PathVariable Integer userId,
            @PathVariable Integer userIdToFollow
    ) {
        followService.followUser(userId, userIdToFollow);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/unfollow/{userIdToUnfollow}")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable Integer userId,
            @PathVariable Integer userIdToUnfollow
    ) {
        followService.unfollowUser(userId, userIdToUnfollow);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserSimpleDTO>> getFollowing(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getFollowing(userId));
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserSimpleDTO>> getFollowers(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getFollowers(userId));
    }

}