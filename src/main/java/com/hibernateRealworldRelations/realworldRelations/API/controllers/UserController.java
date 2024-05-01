package com.hibernateRealworldRelations.realworldRelations.API.controllers;

import com.hibernateRealworldRelations.realworldRelations.API.services.ArticleServiceHTTP;
import com.hibernateRealworldRelations.realworldRelations.API.services.UserService;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.LoginRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.RegistrationRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.UpdateRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.LoginResponse;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.MultipleTagResponse;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final ArticleServiceHTTP articleServiceHTTP;

    // Authentication
    @PostMapping("/users/login")
    public ResponseEntity<Map<String, LoginResponse>> login(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(Map.of("user", service.login(request)));
    }

    // Registration
    @PostMapping("/users")
    public ResponseEntity<Map<String, LoginResponse>> registerUser(
            @RequestBody RegistrationRequest request
    ) {
        return ResponseEntity.ok(Map.of("user", service.registerUser(request)));
    }

    // Get Current User
    @GetMapping("/user")
    public ResponseEntity<Map<String, LoginResponse>> currentUser() {
        return ResponseEntity.ok(Map.of("user", service.getCurrentUser()));
    }

    // Update User
    @PutMapping("/user")
    public ResponseEntity<Map<String, LoginResponse>> updateUser(
            @RequestBody UpdateRequest request
    ) {
        return ResponseEntity.ok(Map.of("user", service.updateUser(request)));
    }

    // Get Profile
    @GetMapping("/profiles/{username}")
    public ResponseEntity<Map<String, ProfileResponse>> getProfile(
            @PathVariable("username") String username
    ) {
        return ResponseEntity.ok(Map.of("profile", service.getProfile(username)));
    }

    // Follow User
    @PostMapping("/profiles/{username}/follow")
    public ResponseEntity<Map<String, ProfileResponse>> followUser(
            @PathVariable("username") String username
    ) {
        return ResponseEntity.ok(Map.of("profile", service.followUser(username)));
    }

    // Unfollow User
    @DeleteMapping("/profiles/{username}/follow")
    public ResponseEntity<Map<String, ProfileResponse>> unfollowUser(
            @PathVariable("username") String username
    ) {
        return ResponseEntity.ok(Map.of("profile", service.unfollowUser(username)));
    }

    // Get Tags
    @GetMapping("/tags")
    public ResponseEntity<MultipleTagResponse> getTags() {
        return ResponseEntity.ok(articleServiceHTTP.getTags());
    }
}