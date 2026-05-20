package com.org.dream.barberService.controller;

import com.org.dream.barberService.DTO.ShopRegistrationRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
public class BarberShopController {

   @PostMapping("/register")
    public ResponseEntity<?> registerShop(
            @Valid @RequestPart("shop") ShopRegistrationRequest request,
            @RequestPart(name="images", required = false) List<MultipartFile> images) {
       System.out.println("hello");
        return ResponseEntity.ok("success");

    }
}
