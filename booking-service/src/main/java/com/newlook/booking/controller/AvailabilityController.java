package com.newlook.booking.controller;

import com.newlook.booking.dto.request.AvailabilityRequest;
import com.newlook.booking.dto.response.AvailabilityResponse;
import com.newlook.booking.service.AvailabilityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/availability")
public class AvailabilityController {
//
//    private final AvailabilityService availabilityService;
//
//    public AvailabilityController(AvailabilityService availabilityService) {
//        this.availabilityService = availabilityService;
//    }
//
//    @PostMapping("/search")
//    public ResponseEntity<AvailabilityResponse> searchAvailability(
//            @Valid @RequestBody AvailabilityRequest request
//    ) {
//
//        AvailabilityResponse response =
//                availabilityService.searchAvailability(request);
//
//        return ResponseEntity.ok(response);
//    }

}
