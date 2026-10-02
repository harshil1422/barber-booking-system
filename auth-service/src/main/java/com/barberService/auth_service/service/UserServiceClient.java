package com.barberService.auth_service.service;


import com.barberService.auth_service.dto.UserServiceDtos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class UserServiceClient {

    private final RestClient restClient;
    private final String userServiceBaseUrl;

    public UserServiceClient(
            RestClient restClient,
            @Value("${user-service.base-url}") String userServiceBaseUrl) {
        this.restClient = restClient;
        this.userServiceBaseUrl = userServiceBaseUrl;
    }

    public UserServiceDtos.UserProfileResponse createUser(
            UserServiceDtos.CreateUserProfileRequest request) {

        return restClient.post()
                .uri(userServiceBaseUrl + "/api/v1/internal/users")
                .body(request)
                .retrieve()
                .body(UserServiceDtos.UserProfileResponse.class);
    }
}