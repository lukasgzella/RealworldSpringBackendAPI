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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.applyPermitDefaultValues();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4300"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token", "Access-Control-Allow-Origin"));
        configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                        .requestMatchers("/console/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/users/login").permitAll()
                        .requestMatchers(HttpMethod.POST,"/users").permitAll()
                        .requestMatchers(HttpMethod.GET,"/user").authenticated()
                        .requestMatchers(HttpMethod.PUT,"/user").permitAll()

                        .requestMatchers(HttpMethod.GET,"/profiles/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/profiles/*/follow").authenticated()
                        .requestMatchers(HttpMethod.DELETE,"/profiles/*/follow").authenticated()
                        .requestMatchers(HttpMethod.GET,"/articles").permitAll()
                        .requestMatchers(HttpMethod.GET,"/articles/*").permitAll()
                        .requestMatchers(HttpMethod.POST,"/articles").authenticated()
                        .requestMatchers(HttpMethod.GET, "/articles/feed").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/articles/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/articles/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/articles/*/comments").authenticated()
                        .requestMatchers(HttpMethod.GET, "/articles/*/comments").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/articles/*/comments/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/articles/*/favorite").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/articles/*/favorite").authenticated()
                        .requestMatchers(HttpMethod.GET, "/tags").permitAll()
                )

                .headers(h -> h.frameOptions(Customizer.withDefaults()).disable())

                .sessionManagement(Customizer.withDefaults())
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}