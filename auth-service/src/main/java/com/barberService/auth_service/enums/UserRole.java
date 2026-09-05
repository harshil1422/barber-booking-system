package com.barberService.auth_service.enums;

public enum UserRole {
    /** Standard customer — can browse, book, review */
    USER,

    /** Shop owner — can manage shop, chairs, schedules, earnings */
    BARBER,

    /** Platform administrator — can verify shops, moderate content */
    ADMIN
}
