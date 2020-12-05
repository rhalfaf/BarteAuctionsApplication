package com.barterAuctions.portal.models.auction;

import com.barterAuctions.portal.models.user.User;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "auction")
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String localization;
    private String title;
    @Column(length = 2050)
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "auction", orphanRemoval = true)
    private List<Image> images;
    private BigDecimal price;
    private Boolean active;
    private LocalDate startDate;
    private LocalDate expireDate;
    @OneToOne
    @JoinColumn(name = "CATEGORY_ID")
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private Category category;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;


    public Auction() {

    }

    public Auction(Long id, String localization, String title, String description,
                   List<Image> images, BigDecimal price, Boolean active, LocalDate startDate,
                   LocalDate expireDate, Category category, User user) {

        this.id = id;
        this.localization = localization;
        this.title = title;
        this.description = description;
        this.images = images;
        this.price = price;
        this.active = active;
        this.startDate = startDate;
        this.expireDate = expireDate;
        this.category = category;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getLocalization() {
        return localization;
    }

    public void setLocalization(String localization) {
        this.localization = localization;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public void addImage(Image image) {
        images.add(image);
        image.setAuction(this);
    }

    public void addImages(List<Image> imagesToAdd) {
        images = imagesToAdd;
        imagesToAdd.forEach(image -> image.setAuction(this));
    }


}
