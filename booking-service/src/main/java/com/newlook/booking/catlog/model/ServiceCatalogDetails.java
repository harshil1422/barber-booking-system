package com.newlook.booking.catlog.model;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceCatalogDetails(
        UUID serviceCatalogId,

        String serviceName,

        int durationMinutes,

        BigDecimal price
) {
}
