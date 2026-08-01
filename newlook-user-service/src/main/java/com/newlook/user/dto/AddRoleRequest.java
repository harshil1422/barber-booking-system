package com.newlook.user.dto;

import com.newlook.user.enums.UserRole;
import jakarta.validation.constraints.NotNull;

public record AddRoleRequest(
        @NotNull UserRole role
) {}
