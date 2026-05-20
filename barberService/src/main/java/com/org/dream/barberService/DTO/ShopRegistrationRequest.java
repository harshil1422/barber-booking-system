package com.org.dream.barberService.DTO;

import jakarta.validation.constraints.*;

public record ShopRegistrationRequest(

        @NotBlank String shopName,
        @NotBlank String ownerName,
        @Email String email,
        @NotBlank String phoneNumber,
        @NotBlank String address,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String pinCode,
        String description

) {}