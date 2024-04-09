package com.hibernateRealworldRelations.realworldRelations.api;

import com.hibernateRealworldRelations.realworldRelations.API.services.UserService;
import com.hibernateRealworldRelations.realworldRelations._config.JwtService;
import com.hibernateRealworldRelations.realworldRelations.auxiliary.AuthenticationFacade;
import com.hibernateRealworldRelations.realworldRelations.entity.Role;
import com.hibernateRealworldRelations.realworldRelations.entity.User;
import com.hibernateRealworldRelations.realworldRelations.repository.FollowerRepository;
import com.hibernateRealworldRelations.realworldRelations.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository userRepository;
    private FollowerRepository followerRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private AuthenticationFacade authenticationFacade;
    private UserService userService;

    @BeforeEach
    public void mockFields() {
        userRepository = mock(UserRepository.class);
        followerRepository = mock(FollowerRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        authenticationManager = mock(AuthenticationManager.class);
        authenticationFacade = mock(AuthenticationFacade.class);
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

    public void registerUser_allParamsOk_userRegistered() {

        //given
        //RegistrationRequest
        User actual = User.builder()
                .username("JohnDoe")
                .email("johndoe@johndoe.pl")
                .password("password")
                .role(Role.USER)
                .build();
        User expected = initUser();
        when(userRepository.existsByUsername(actual.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(actual.getPassword())).thenReturn("encodedPassword");
        //when
        userService.registerUser(actual);
        //then
        assertEquals(expected, actual);
        verify(userRepository).save(expected);
    }
}
