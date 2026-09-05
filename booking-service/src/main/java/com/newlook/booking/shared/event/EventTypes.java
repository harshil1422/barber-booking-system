package com.newlook.booking.shared.event;

public class EventTypes {
    private EventTypes() {
        // Utility class
    }

    public static final String BOOKING_CREATED =
            "booking.created";
    public static final String BOOKING_CANCELLED =
            "booking.cancelled";

    public static final String BOOKING_RESCHEDULED =
            "booking.rescheduled";
}
