package com.hibernateRealworldRelations.realworldRelations.api;

import com.hibernateRealworldRelations.realworldRelations.API.services.UserService;
import com.hibernateRealworldRelations.realworldRelations._config.JwtService;
import com.hibernateRealworldRelations.realworldRelations.auxiliary.AuthenticationFacade;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.RegistrationRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.UpdateRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.LoginResponse;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.ProfileResponse;
import com.hibernateRealworldRelations.realworldRelations.entity.Follower;
import com.hibernateRealworldRelations.realworldRelations.entity.Role;
import com.hibernateRealworldRelations.realworldRelations.entity.User;
import com.hibernateRealworldRelations.realworldRelations.exceptions.NoSuchUserException;
import com.hibernateRealworldRelations.realworldRelations.repository.FollowerRepository;
import com.hibernateRealworldRelations.realworldRelations.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository userRepository;
    private FollowerRepository followerRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private AuthenticationFacade authenticationFacade;
    private Authentication auth;
    private UserService userService;

    @BeforeEach
    public void mockFields() {
        userRepository = mock(UserRepository.class);
        followerRepository = mock(FollowerRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        authenticationManager = mock(AuthenticationManager.class);
        authenticationFacade = mock(AuthenticationFacade.class);
        auth = mock(Authentication.class);
        userService = new UserService(
                userRepository,
                followerRepository,
                passwordEncoder,
                jwtService,
                authenticationManager,
                authenticationFacade
        );
    }

    private User initUser() {
        return User.builder()
                .username("JohnDoe")
                .email("johndoe@johndoe.pl")
                .password("encodedPassword")
                .role(Role.USER)
                .build();
    }

    @Test
    public void registerUser_allParamsOk_userRegistered() {

        //given
        RegistrationRequest request = RegistrationRequest.builder()
                .username("JohnDoe")
                .email("johndoe@johndoe.pl")
                .password("password")
                .build();
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        LoginResponse expected = LoginResponse.builder()
                .email("johndoe@johndoe.pl")
                .token("generatedToken")
                .username("JohnDoe")
                .bio(null)
                .image(null)
                .build();
        when(userRepository.save(user)).thenReturn(user);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(jwtService.generateToken(user)).thenReturn("generatedToken");
        //when
        LoginResponse actual = userService.registerUser(request);
        //then
        assertEquals(expected, actual);
        verify(userRepository).save(user);
    }

    @Test
    public void registerUser_usernameAlreadyExists_throwsIllegalArgumentException() {
        //given
        RegistrationRequest request = RegistrationRequest.builder()
                .username("JohnDoe")
                .email("johndoe@johndoe.pl")
                .password("password")
                .build();
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);
        //when/then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(request));
        assertEquals("Provided email or username already exists", exception.getMessage());
    }

    @Test
    public void registerUser_emailAlreadyExists_throwsIllegalArgumentException() {
        //given
        RegistrationRequest request = RegistrationRequest.builder()
                .username("JohnDoe")
                .email("johndoe@johndoe.pl")
                .password("password")
                .build();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
        //when/then
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(request),
                "Provided email or username already exists");
    }

    @Test
    public void getCurrentUser_allParamsOk_returnsLoginResponse() {

        //given
        var user = User.builder()
                .username("JohnDoe")
                .email("johndoe@johndoe.pl")
                .password("password")
                .role(Role.USER)
                .build();
        LoginResponse expected = LoginResponse.builder()
                .email("johndoe@johndoe.pl")
                .token("generatedToken")
                .username("JohnDoe")
                .bio(null)
                .image(null)
                .build();
        when(jwtService.generateToken(user)).thenReturn("generatedToken");
        when(authenticationFacade.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        //when
        LoginResponse actual = userService.getCurrentUser();
        //then
        assertEquals(expected, actual);
    }

    @Test
    public void updateUser_newEmail_userUpdated() {
        //given
        UpdateRequest request = UpdateRequest.builder()
                .email("newEmail")
                .build();
        var user = User.builder()
                .username("JohnDoe")
                .email("johndoe@johndoe.pl")
                .password("password")
                .role(Role.USER)
                .build();
        LoginResponse expected = LoginResponse.builder()
                .email("newEmail")
                .token("generatedToken")
                .username("JohnDoe")
                .bio(null)
                .image(null)
                .build();
        when(jwtService.generateToken(user)).thenReturn("generatedToken");
        when(authenticationFacade.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        //when
        LoginResponse actual = userService.updateUser(request);
        //then
        assertEquals(expected, actual);
    }

    @Test
    public void updateUser_newBioNewImage_userUpdated() {
        //given
        UpdateRequest request = UpdateRequest.builder()
                .bio("newBio")
                .image("newImage")
                .build();
        var user = User.builder()
                .username("JohnDoe")
                .email("johndoe@johndoe.pl")
                .password("password")
                .role(Role.USER)
                .build();
        LoginResponse expected = LoginResponse.builder()
                .email("johndoe@johndoe.pl")
                .token("generatedToken")
                .username("JohnDoe")
                .bio("newBio")
                .image("newImage")
                .build();
        when(jwtService.generateToken(user)).thenReturn("generatedToken");
        when(authenticationFacade.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        //when
        LoginResponse actual = userService.updateUser(request);
        //then
        assertEquals(expected, actual);
    }

    @Test
    public void updateUser_sameEmail_throwsIllegalArgumentException() {
        //given
        UpdateRequest request = UpdateRequest.builder()
                .email("johndoe@johndoe.pl")
                .build();
        var user = User.builder()
                .username("JohnDoe")
                .email("johndoe@johndoe.pl")
                .password("password")
                .role(Role.USER)
                .build();
        when(authenticationFacade.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
        //when/then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(request));
        assertEquals("Provided email already exists", exception.getMessage());
    }

    @Test
    public void updateUser_sameUsername_throwsIllegalArgumentException() {
        //given
        UpdateRequest request = UpdateRequest.builder()
                .username("JohnDoe")
                .build();
        var user = User.builder()
                .username("JohnDoe")
                .email("johndoe@johndoe.pl")
                .password("password")
                .role(Role.USER)
                .build();
        when(authenticationFacade.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);
        //when/then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(request));
        assertEquals("Provided username already exists", exception.getMessage());
    }

    @Test
    public void getProfile_sameUsername_returnsProfileResponse() {
        //given
        String username = "JohnDoe";
        var userTo = User.builder()
                .username("JohnDoe")
                .email("johndoe@johndoe.pl")
                .password("password")
                .bio("newBio")
                .image("newImage")
                .role(Role.USER)
                .build();
        ProfileResponse expected = ProfileResponse.builder()
                .username("JohnDoe")
                .bio("newBio")
                .image("newImage")
                .following(false)
                .build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userTo));
        when(authenticationFacade.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("anonymousUser");
        //when
        ProfileResponse actual = userService.getProfile(username);
        //then
        assertEquals(expected, actual);
    }

    @Test
    public void getProfile_followingTrue_returnsProfileResponse() {
        //given
        String username = "JohnDoe";
        var authenticatedUser = User.builder()
                .username("FollowingJohnDoe")
                .email("following@johndoe.pl")
                .password("password")
                .bio("followingBio")
                .image("followingImage")
                .role(Role.USER)
                .build();
        var userTo = User.builder()
                .username("JohnDoe")
                .email("johndoe@johndoe.pl")
                .password("password")
                .bio("newBio")
                .image("newImage")
                .role(Role.USER)
                .build();
        ProfileResponse expected = ProfileResponse.builder()
                .username("JohnDoe")
                .bio("newBio")
                .image("newImage")
                .following(true)
                .build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userTo));
        when(authenticationFacade.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn(authenticatedUser.getEmail());
        when(userRepository.findByEmail(authenticatedUser.getEmail())).thenReturn(Optional.of(authenticatedUser));
        when(followerRepository.existsByFromTo(authenticatedUser.getUsernameDB(), username)).thenReturn(true);
        //when
        ProfileResponse actual = userService.getProfile(username);
        //then
        assertEquals(expected, actual);
    }
    @Test
    public void getProfile_followingFalse_returnsProfileResponse() {
        //given
        String username = "JohnDoe";
        var authenticatedUser = User.builder()
                .username("NotFollowingJohnDoe")
                .email("notFollowing@johndoe.pl")
                .password("password")
                .bio("notFollowingBio")
                .image("notFollowingImage")
                .role(Role.USER)
                .build();
        var userTo = User.builder()
                .username("JohnDoe")
                .email("johndoe@johndoe.pl")
                .password("password")
                .bio("newBio")
                .image("newImage")
                .role(Role.USER)
                .build();
        ProfileResponse expected = ProfileResponse.builder()
                .username("JohnDoe")
                .bio("newBio")
                .image("newImage")
                .following(false)
                .build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userTo));
        when(authenticationFacade.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn(authenticatedUser.getEmail());
        when(userRepository.findByEmail(authenticatedUser.getEmail())).thenReturn(Optional.of(authenticatedUser));
        when(followerRepository.existsByFromTo(authenticatedUser.getUsernameDB(), username)).thenReturn(false);
        //when
        ProfileResponse actual = userService.getProfile(username);
        //then
        assertEquals(expected, actual);
    }
    @Test
    public void getProfile_notExistingUsername_throwsNoSuchUserException() {
        //given
        String username = "notExistingUsername";
        when(userRepository.findByUsername(username)).thenThrow(NoSuchUserException.class);
        //when/then
        NoSuchUserException exception = assertThrows(NoSuchUserException.class,
                () -> userService.getProfile(username));
    }
    @Test
    public void followUser_followingFalse_returnsProfileResponse() {
        //given
        String username = "JohnDoe";
        var authenticatedUser = User.builder()
                .username("NotFollowingJohnDoe")
                .email("notFollowing@johndoe.pl")
                .password("password")
                .bio("notFollowingBio")
                .image("notFollowingImage")
                .following(new HashSet<>())
                .role(Role.USER)
                .build();
        var userTo = User.builder()
                .username("JohnDoe")
                .email("johndoe@johndoe.pl")
                .password("password")
                .bio("newBio")
                .image("newImage")
                .followers(new HashSet<>())
                .role(Role.USER)
                .build();
        ProfileResponse expected = ProfileResponse.builder()
                .username("JohnDoe")
                .bio("newBio")
                .image("newImage")
                .following(true)
                .build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userTo));
        when(authenticationFacade.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn(authenticatedUser.getEmail());
        when(userRepository.findByEmail(authenticatedUser.getEmail())).thenReturn(Optional.of(authenticatedUser));
        when(followerRepository.existsByFromTo(authenticatedUser.getUsernameDB(), username)).thenReturn(false);
        //when
        ProfileResponse actual = userService.followUser(username);
        //then
        assertEquals(expected, actual);
    }
    @Test
    public void unfollowUser_returnsProfileResponse() {
        //given
        String username = "JohnDoe";
        var authenticatedUser = User.builder()
                .username("followingJohnDoe")
                .email("following@johndoe.pl")
                .password("password")
                .bio("followingBio")
                .image("followingImage")
                .following(new HashSet<>())
                .role(Role.USER)
                .build();
        var userTo = User.builder()
                .username("JohnDoe")
                .email("johndoe@johndoe.pl")
                .password("password")
                .bio("newBio")
                .image("newImage")
                .followers(new HashSet<>())
                .role(Role.USER)
                .build();
        ProfileResponse expected = ProfileResponse.builder()
                .username("JohnDoe")
                .bio("newBio")
                .image("newImage")
                .following(false)
                .build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userTo));
        when(authenticationFacade.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn(authenticatedUser.getEmail());
        when(userRepository.findByEmail(authenticatedUser.getEmail())).thenReturn(Optional.of(authenticatedUser));
        when(followerRepository.existsByFromTo(authenticatedUser.getUsernameDB(), username)).thenReturn(true);
        when(followerRepository.findByFromTo(authenticatedUser.getUsernameDB(), username))
                .thenReturn(Optional.of(new Follower(authenticatedUser, userTo)));
        //when
        ProfileResponse actual = userService.unfollowUser(username);
        //then
        assertEquals(expected, actual);
    }
}