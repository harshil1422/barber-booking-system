package com.newlook.booking.catlog.provider.impl;

import com.newlook.booking.catlog.model.ServiceCatalogDetails;
import com.newlook.booking.catlog.provider.ServiceCatalogProvider;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ServiceCatalogProviderImpl implements ServiceCatalogProvider {
    private static final Map<UUID, ServiceCatalogDetails> SERVICES =
            Map.of(
                    UUID.fromString("11111111-1111-1111-1111-111111111111"),
                    new ServiceCatalogDetails(
                            UUID.fromString("11111111-1111-1111-1111-111111111111"),
                            "Haircut",
                            30,
                            new BigDecimal("300.00")
                    ),

                    UUID.fromString("22222222-2222-2222-2222-222222222222"),
                    new ServiceCatalogDetails(
                            UUID.fromString("22222222-2222-2222-2222-222222222222"),
                            "Shaving",
                            15,
                            new BigDecimal("150.00")
                    ),

                    UUID.fromString("33333333-3333-3333-3333-333333333333"),
                    new ServiceCatalogDetails(
                            UUID.fromString("33333333-3333-3333-3333-333333333333"),
                            "Head Massage",
                            20,
                            new BigDecimal("250.00")
                    )
            );

    @Override
    public List<ServiceCatalogDetails> getServices(
            UUID shopId,
            List<UUID> serviceCatalogIds
    ) {

        return serviceCatalogIds.stream()
                .map(serviceId -> {

                    ServiceCatalogDetails service =
                            SERVICES.get(serviceId);

                    if (service == null) {
                        throw new IllegalArgumentException(
                                "Service not found: " + serviceId
                        );
                    }

                    return service;
                })
                .toList();
    }
}
