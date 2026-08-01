package com.newlook.user.exception;

import java.util.UUID;

public class UserProfileNotFoundException extends RuntimeException {
    public UserProfileNotFoundException(UUID userId) {
        super("No user profile found for userId=" + userId);
    }
}
