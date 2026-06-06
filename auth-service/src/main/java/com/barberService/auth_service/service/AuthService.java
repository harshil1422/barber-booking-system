package com.barberService.auth_service.service;

import com.barberService.auth_service.dto.AuthDtos;
import com.barberService.auth_service.dto.AuthDtos.TokenResponse;
import com.barberService.auth_service.model.User;
import com.barberService.auth_service.repository.RefreshTokenRepository;
import com.barberService.auth_service.repository.UserRepository;
import com.barberService.auth_service.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public TokenResponse login(AuthDtos.LoginRequest req, HttpServletRequest httpReq) {
        User user = userRepository
                .findByUsernameOrEmail(req.username(), req.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        if (user.isLocked())   throw new LockedException("Account is locked");
        if (!user.isEnabled()) throw new BadCredentialsException("Account is disabled");

        String accessToken = jwtTokenProvider.generateAccessToken(
                user, Map.of("userId", user.getId().toString()));

        String rawRefresh = refreshTokenService.createRefreshToken(
                user.getId(),
                httpReq.getHeader("User-Agent"),
                httpReq.getRemoteAddr());

        auditService.log(user.getId(), "LOGIN", httpReq);
        return new TokenResponse(accessToken, rawRefresh);
    }

    @Transactional
    public TokenResponse refresh(String rawRefreshToken, HttpServletRequest httpReq) {
        RefreshTokenService.RotationResult rotation = refreshTokenService.rotate(
                rawRefreshToken,
                httpReq.getHeader("User-Agent"),
                httpReq.getRemoteAddr());

        User user = userRepository.findById(rotation.userId())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        String accessToken = jwtTokenProvider.generateAccessToken(
                user, Map.of("userId", user.getId().toString()));

        auditService.log(user.getId(), "REFRESH", httpReq);
        return new TokenResponse(accessToken, rotation.newRawToken());
    }

    @Transactional
    public void logout(String rawRefreshToken, HttpServletRequest httpReq) {
        String hash = refreshTokenService.sha256Hex(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(token -> {
            refreshTokenService.revokeAll(token.getUserId());
            auditService.log(token.getUserId(), "LOGOUT", httpReq);
        });
    }

    @Transactional
    public void register(AuthDtos.RegisterRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .username(req.username())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .build();

        userRepository.save(user);
    }
}