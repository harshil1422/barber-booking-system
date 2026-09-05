package com.newlook.booking.catlog.provider;

import com.newlook.booking.catlog.model.ServiceCatalogDetails;

import java.util.List;
import java.util.UUID;


public interface ServiceCatalogProvider {
    List<ServiceCatalogDetails> getServices(
            UUID shopId,
            List<UUID> serviceCatalogIds
    );
}
