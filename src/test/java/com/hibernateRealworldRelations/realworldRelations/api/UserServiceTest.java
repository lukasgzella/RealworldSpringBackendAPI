package com.hibernateRealworldRelations.realworldRelations.api;

import com.hibernateRealworldRelations.realworldRelations.API.services.UserService;
import com.hibernateRealworldRelations.realworldRelations._config.JwtService;
import com.hibernateRealworldRelations.realworldRelations.auxiliary.AuthenticationFacade;
import com.hibernateRealworldRelations.realworldRelations.dto.requests.RegistrationRequest;
import com.hibernateRealworldRelations.realworldRelations.dto.responses.LoginResponse;
import com.hibernateRealworldRelations.realworldRelations.entity.Role;
import com.hibernateRealworldRelations.realworldRelations.entity.User;
import com.hibernateRealworldRelations.realworldRelations.repository.FollowerRepository;
import com.hibernateRealworldRelations.realworldRelations.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private UserService userService;
    private Authentication auth;

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
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(request),
                "Provided email or username already exists");
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
}
