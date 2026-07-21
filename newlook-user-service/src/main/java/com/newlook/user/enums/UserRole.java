package com.newlook.user.enums;

/**
 * User roles for New Look.
 *
 * Architecture rule: a single user can hold multiple roles simultaneously.
 * e.g. roles = [USER, BARBER] — they booked an appointment AND own a shop.
 *
 * Stored in user_roles table (one row per role per user).
 * JWT claim "roles" is a String[] reflecting the current list.
 * API Gateway injects X-User-Role as a comma-separated string.
 */
public enum UserRole {

    /** Standard customer — can browse, book, review */
    USER,

    /** Shop owner — can manage shop, chairs, schedules, earnings */
    BARBER,

    /** Platform administrator — can verify shops, moderate content */
    ADMIN
}