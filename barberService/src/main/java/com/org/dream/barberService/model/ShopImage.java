package com.org.dream.barberService.model;

import jakarta.persistence.*;

@Entity
@Table(name = "shop_images")
public class ShopImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl; // path or cloud URL

    private String imageType; // e.g. WORK_SAMPLE, SHOP_FRONT

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private BarberShop shop;

    // getters & setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public BarberShop getShop() {
        return shop;
    }

    public void setShop(BarberShop shop) {
        this.shop = shop;
    }
}
