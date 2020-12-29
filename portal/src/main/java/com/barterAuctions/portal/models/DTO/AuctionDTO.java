package com.barterAuctions.portal.models.DTO;

import com.barterAuctions.portal.models.auction.Auction;
import com.barterAuctions.portal.models.auction.Category;
import com.barterAuctions.portal.models.auction.Image;
import com.barterAuctions.portal.models.user.User;

import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

public class AuctionDTO {
    private Long id;
    @NotBlank(message = "To pole nie może być puste.")
    private String title;
    @NotBlank(message = "To pole nie może być puste.")
    private String localization;
    @NotBlank(message = "To pole nie może być puste.")
    private String description;
    private List<Image> images;
    private BigDecimal price;
    private Category category;
    private Boolean active;
    private User user;

    public AuctionDTO() {
    }

    public AuctionDTO(Auction auction) {
        this.id = auction.getId();
        this.title = auction.getTitle();
        this.localization = auction.getLocalization();
        this.description = auction.getDescription();
        this.images = auction.getImages();
        this.price = auction.getPrice();
        this.category = auction.getCategory();
        this.active = auction.isActive();
        this.user = auction.getUser();
    }

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

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
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

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
