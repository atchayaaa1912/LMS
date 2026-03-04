package com.project.lms.config;

import com.project.lms.security.JwtAuthenticationEntryPoint;
import com.project.lms.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final JwtAuthenticationEntryPoint entryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)
                )
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/export").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/users/import").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/courses").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/courses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/courses/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/courses/*/modules").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/courses/*/modules").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/courses/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/courses/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/courses/**")
                        .authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}