package com.hibernateRealworldRelations.realworldRelations.API.services;

import com.hibernateRealworldRelations.realworldRelations._config.JwtService;
import com.hibernateRealworldRelations.realworldRelations.auxiliary.AuthenticationFacade;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.LoginRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.RegistrationRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.UpdateRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.LoginResponse;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.ProfileResponse;
import com.hibernateRealworldRelations.realworldRelations.entity.Follower;
import com.hibernateRealworldRelations.realworldRelations.entity.Role;
import com.hibernateRealworldRelations.realworldRelations.entity.User;
import com.hibernateRealworldRelations.realworldRelations.repository.FollowerRepository;
import com.hibernateRealworldRelations.realworldRelations.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuthenticationFacade authenticationFacade;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return LoginResponse.builder()
                .email(user.getEmail())
                .token(jwtToken)
                .username(user.getUsernameDB())
                .bio(user.getBio())
                .image(user.getImage())
                .build();
    }
// todo check if username and email is unique
    public LoginResponse registerUser(RegistrationRequest request) {
        System.out.println(request.toString());
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return LoginResponse.builder()
                .email(user.getEmail())
                .token(jwtToken)
                .username(user.getUsernameDB())
                .bio(user.getBio())
                .image(user.getImage())
                .build();
    }

    public LoginResponse getCurrentUser() {
        var user = retrieveCurrentUserFromDb();
        var jwtToken = jwtService.generateToken(user);
        return LoginResponse.builder()
                .email(user.getEmail())
                .token(jwtToken)
                .username(user.getUsernameDB())
                .bio(user.getBio())
                .image(user.getImage())
                .build();
    }

    public LoginResponse updateUser(UpdateRequest request) {
        var user = retrieveCurrentUserFromDb();

        if (isEmailUnique(request.getEmail())) {
            throw new IllegalArgumentException("Provided email already exists");
        }
        if (isUsernameUnique(request.getUsername())) {
            throw new IllegalArgumentException("Provided username already exists");
        }

        User updatedUser = userRepository.save(updateRequestedFields(user, request));

        var jwtToken = jwtService.generateToken(user);
        return LoginResponse.builder()
                .email(updatedUser.getEmail())
                .token(jwtToken)
                .username(updatedUser.getUsernameDB())
                .bio(updatedUser.getBio())
                .image(updatedUser.getImage())
                .build();
    }

    private User updateRequestedFields(User user, UpdateRequest request) {
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getImage() != null) {
            user.setImage(request.getImage());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        return user;
    }

    private boolean isEmailUnique(String email) {
        return userRepository.existsByEmail(email);
    }

    private boolean isUsernameUnique(String username) {
        return userRepository.existsByUsername(username);
    }

    private User retrieveCurrentUserFromDb() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principal = authenticationFacade.getAuthentication().getName();
        return userRepository.findByEmail(principal).orElseThrow();
    }

    @Transactional
    public ProfileResponse getProfile(String username) {
        // todo exception no such user
        User userTo = userRepository.findByUsername(username).orElseThrow();
        String emailFrom = authenticationFacade.getAuthentication().getName();
        boolean isFollowing = false;
        System.out.println(emailFrom);
        if (!emailFrom.equals("anonymousUser")) {
            User userFrom = userRepository.findByEmail(emailFrom).orElseThrow();
            isFollowing = followerRepository.existsByFromTo(userFrom.getUsernameDB(), username);
            System.out.println(isFollowing);
        }
        return ProfileResponse.builder()
                .username(userTo.getUsernameDB())
                .bio(userTo.getBio())
                .image(userTo.getImage())
                .following(isFollowing)
                .build();
    }

    @Transactional
    public ProfileResponse followUser(String username) {
        User userTo = userRepository.findByUsername(username).orElseThrow();
        ProfileResponse res = ProfileResponse.builder()
                .username(userTo.getUsername())
                .bio(userTo.getBio())
                .image(userTo.getImage())
                .following(true)
                .build();
        String emailFrom = authenticationFacade.getAuthentication().getName();
        User userFrom = userRepository.findByEmail(emailFrom).orElseThrow();
        String usernameFrom = userFrom.getUsernameDB();
        boolean isFollowing = followerRepository.existsByFromTo(usernameFrom, username);
        if (isFollowing) {
            return res;
        }
        Follower follower = new Follower(userFrom, userTo);
        follower = followerRepository.save(follower);
        userTo.getFollowers().add(follower);
        userFrom.getFollowing().add(follower);
        userRepository.save(userTo);
        userRepository.save(userFrom);
        return res;
    }

    @Transactional
    public ProfileResponse unfollowUser(String username) {
        User userTo = userRepository.findByUsername(username).orElseThrow();
        ProfileResponse res = ProfileResponse.builder()
                .username(userTo.getUsername())
                .bio(userTo.getBio())
                .image(userTo.getImage())
                .following(false)
                .build();
        String emailFrom = authenticationFacade.getAuthentication().getName();
        User userFrom = userRepository.findByEmail(emailFrom).orElseThrow();
        String usernameFrom = userFrom.getUsernameDB();
        boolean isFollowing = followerRepository.existsByFromTo(usernameFrom, username);
        if (!isFollowing) {
            return res;
        }
        Follower follower = followerRepository.findByFromTo(usernameFrom, username).get();
        userFrom.getFollowing().remove(follower);
        userTo.getFollowers().remove(follower);
        followerRepository.delete(follower);
        userRepository.save(userTo);
        userRepository.save(userFrom);
        return res;
    }
}
