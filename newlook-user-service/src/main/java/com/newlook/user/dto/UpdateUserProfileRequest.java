package com.newlook.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(

        @Size(min = 1, max = 100)
        String name,

        @Email
        String email,

        String avatarUrl,

        @Size(min = 2, max = 10)
        String preferredLanguage
) {}
