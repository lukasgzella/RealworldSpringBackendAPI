package com.hibernateRealworldRelations.realworldRelations.API.controllers;

import com.hibernateRealworldRelations.realworldRelations.API.services.UserService;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.LoginRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.RegistrationRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.UpdateRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.LoginResponse;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    // Authentication
    @PostMapping("/api/users/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(service.login(request));
    }

    // Registration
    @PostMapping("/api/users")
    public ResponseEntity<LoginResponse> registerUser(
            @RequestBody RegistrationRequest request
    ) {
        return ResponseEntity.ok(service.registerUser(request));
    }

    // Get Current User
    @GetMapping("/api/user")
    public ResponseEntity<LoginResponse> currentUser() {
        return ResponseEntity.ok(service.getCurrentUser());
    }

    // Update User
    @PutMapping("/api/user")
    public ResponseEntity<LoginResponse> updateUser(
            @RequestBody UpdateRequest request
    ) {
        return ResponseEntity.ok(service.updateUser(request));
    }

    // Get Profile
    @GetMapping("/api/profiles/{username}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable("username") String username) {
        return ResponseEntity.ok(service.getProfile(username));
    }

    // Follow User
    @PostMapping("/api/profiles/{username}/follow")
    public ResponseEntity<ProfileResponse> followUser(@PathVariable("username") String username) {
        return ResponseEntity.ok(service.followUser(username));
    }

    // Unfollow User
    @DeleteMapping("/api/profiles/{username}/follow")
    public ResponseEntity<ProfileResponse> unfollowUser(@PathVariable("username") String username) {
        return ResponseEntity.ok(service.unfollowUser(username));
    }
}