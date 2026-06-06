package com.barberService.auth_service;


import com.barberService.auth_service.dto.AuthDtos;
import com.barberService.auth_service.exception.InvalidTokenException;
import com.barberService.auth_service.model.User;
import com.barberService.auth_service.repository.RefreshTokenRepository;
import com.barberService.auth_service.repository.UserRepository;
import com.barberService.auth_service.security.JwtTokenProvider;
import com.barberService.auth_service.service.AuditService;
import com.barberService.auth_service.service.AuthService;
import com.barberService.auth_service.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    RefreshTokenRepository refreshTokenRepository;
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    RefreshTokenService refreshTokenService;
    @Mock PasswordEncoder passwordEncoder;
    @Mock
    AuditService auditService;
    @Mock HttpServletRequest httpRequest;

    @InjectMocks
    AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .password("$2a$12$hashedpassword")
                .enabled(true)
                .locked(false)
                .roles(Set.of())
                .build();
    }

    @Test
    @DisplayName("login() returns tokens for valid credentials")
    void login_validCredentials_returnsTokens() {
        when(userRepository.findByUsernameOrEmail("testuser", "testuser"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(any(), any())).thenReturn("access.token.here");
        when(refreshTokenService.createRefreshToken(any(), any(), any())).thenReturn("raw-refresh-token");

        AuthDtos.TokenResponse response = authService.login(new AuthDtos.LoginRequest("testuser", "password123"), httpRequest);

        assertThat(response.accessToken()).isEqualTo("access.token.here");
        assertThat(response.refreshToken()).isEqualTo("raw-refresh-token");
        verify(auditService).log(eq(testUser.getId()), eq("LOGIN"), any());
    }

    @Test
    @DisplayName("login() throws BadCredentialsException for wrong password")
    void login_wrongPassword_throwsBadCredentials() {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() ->
                authService.login(new AuthDtos.LoginRequest("testuser", "wrongpass"), httpRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("login() throws BadCredentialsException for unknown user")
    void login_unknownUser_throwsBadCredentials() {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authService.login(new AuthDtos.LoginRequest("nobody", "pass"), httpRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("refresh() rotates token and returns new access token")
    void refresh_validToken_returnsNewTokens() {
        UUID userId = testUser.getId();
        when(refreshTokenService.rotate(any(), any(), any()))
                .thenReturn(new RefreshTokenService.RotationResult(userId, "new-raw-refresh"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateAccessToken(any(), any())).thenReturn("new.access.token");

        AuthDtos.TokenResponse response = authService.refresh("old-refresh-token", httpRequest);

        assertThat(response.accessToken()).isEqualTo("new.access.token");
        assertThat(response.refreshToken()).isEqualTo("new-raw-refresh");
    }

    @Test
    @DisplayName("refresh() throws when token is reused (theft detection)")
    void refresh_revokedToken_throwsInvalidTokenException() {
        when(refreshTokenService.rotate(any(), any(), any()))
                .thenThrow(new InvalidTokenException("Token reuse detected — all sessions have been revoked"));

        assertThatThrownBy(() -> authService.refresh("stolen-token", httpRequest))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Token reuse detected");
    }
}