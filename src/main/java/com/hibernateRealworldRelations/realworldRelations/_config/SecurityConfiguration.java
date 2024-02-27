package com.hibernateRealworldRelations.realworldRelations._config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/console/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/users").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/user").authenticated()
                        .requestMatchers(HttpMethod.PUT,"/api/user").authenticated()
                        .requestMatchers(HttpMethod.GET,"/api/profiles/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/profiles/*/follow").authenticated()
                        .requestMatchers(HttpMethod.DELETE,"/api/profiles/*/unfollow").authenticated()
                        .requestMatchers(HttpMethod.GET,"/api/articles").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/articles/*").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/articles").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/articles/feed").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/articles/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/articles/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/articles/*/comments").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/articles/*/comments").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/articles/*/comments/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/articles/*/favorite").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/articles/*/favorite").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/tags").permitAll()
//                        .anyRequest().permitAll()
                )

                .headers(h -> h.frameOptions(Customizer.withDefaults()).disable())

                .sessionManagement(Customizer.withDefaults())
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}