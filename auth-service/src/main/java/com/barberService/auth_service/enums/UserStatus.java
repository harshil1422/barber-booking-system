package com.barberService.auth_service.enums;

public enum UserStatus {
    /** Normal active account */
    ACTIVE,

    /** Temporarily suspended by admin (abuse, fraud) */
    SUSPENDED,

    /** Soft-deleted — account closed by user or admin */
    DELETED
}
