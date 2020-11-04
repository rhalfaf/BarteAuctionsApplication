package com.barterAuctions.portal.models.DTO;

import com.barterAuctions.portal.models.auction.Category;
import com.barterAuctions.portal.models.auction.ImageURLs;

import java.math.BigDecimal;
import java.util.List;

public class AuctionDTO {
    private Long id;
    private String title;
    private String localization;
    private String description;
    private List<ImageURLs> imagesURL;
    private BigDecimal price;
    private Category category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocalization() {
        return localization;
    }

    public void setLocalization(String localization) {
        this.localization = localization;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ImageURLs> getImagesURL() {
        return imagesURL;
    }

    public void setImagesURL(List<ImageURLs> imagesURL) {
        this.imagesURL = imagesURL;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }



}
