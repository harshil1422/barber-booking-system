package com.newlook.user.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Defense-in-depth: even if the gateway is misconfigured and a request to
 * /api/v1/internal/** slips through from outside, this filter rejects it
 * unless the caller presents the shared internal API key. In production,
 * prefer mTLS between services or a service-mesh authorization policy
 * over this static-key approach.
 */
@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {

//    @Value("${internal.api-key}")
    private String expectedApiKey;

    private static final String INTERNAL_PREFIX = "/api/v1/internal/";
    private static final String HEADER_NAME = "X-Internal-Api-Key";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().startsWith(INTERNAL_PREFIX)) {
            String providedKey = request.getHeader(HEADER_NAME);
            if (providedKey == null || !providedKey.equals(expectedApiKey)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Missing or invalid internal API key");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
