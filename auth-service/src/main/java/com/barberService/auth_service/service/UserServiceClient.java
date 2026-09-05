package com.barberService.auth_service.service;

import com.barberService.auth_service.dto.UserServiceDtos;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


@Service
@AllArgsConstructor
public class UserServiceClient {

    private final RestClient restClient;


    public UserServiceDtos.UserProfileResponse createUser(UserServiceDtos.CreateUserProfileRequest request) {

        return restClient.post()
                .uri("http://localhost:8088/api/v1/internal/users")
                .body(request)
                .retrieve().body(UserServiceDtos.UserProfileResponse.class);
    }

}
