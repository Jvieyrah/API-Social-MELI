package com.meli.social.user.impl;

import com.meli.social.user.inter.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Integer userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/follow/{followedId}")
    public ResponseEntity<User> followUser(
            @PathVariable Integer userId,
            @PathVariable Integer followedId) {
        User followed = userService.followUser(userId, followedId);
        return ResponseEntity.ok(followed);
    }

    @DeleteMapping("/{userId}/unfollow/{followedId}")
    public ResponseEntity<User> unfollowUser(
            @PathVariable Integer userId,
            @PathVariable Integer followedId) {
        User unfollowed = userService.unfollowUser(userId, followedId);
        return ResponseEntity.ok(unfollowed);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<User>> getFollowers(
            @PathVariable Integer userId,
            @RequestParam(required = false) String order) {
        List<User> followers = userService.getFollowersOrdered(userId, order);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<User>> getFollowing(
            @PathVariable Integer userId,
            @RequestParam(required = false) String order) {
        List<User> following = userService.getFollowingOrdered(userId, order);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<Integer> getFollowersCount(@PathVariable Integer userId) {
        Integer count = userService.getFollowersCount(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/top")
    public ResponseEntity<List<User>> getTopUsers(@RequestParam(defaultValue = "10") int limit) {
        List<User> topUsers = userService.getTopUsers(limit);
        return ResponseEntity.ok(topUsers);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        List<User> users = userService.searchUsers(query);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}/suggestions")
    public ResponseEntity<List<User>> getSuggestions(@PathVariable Integer userId) {
        List<User> suggestions = userService.getSuggestedUsers(userId);
        return ResponseEntity.ok(suggestions);
    }
}