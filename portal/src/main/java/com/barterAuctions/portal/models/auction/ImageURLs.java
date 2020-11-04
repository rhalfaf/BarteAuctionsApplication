package com.barterAuctions.portal.models.auction;

import javax.persistence.*;

@Entity
@Table
public class ImageURLs {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String url;
    private boolean isMainPhoto;

    public ImageURLs() {
    }

    public ImageURLs(String url) {
        this(url,false);
    }

    public ImageURLs(String url, boolean isMainPhoto) {
        this.url = url;
        this.isMainPhoto = isMainPhoto;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isMainPhoto() {
        return isMainPhoto;
    }

    public void setMainPhoto(boolean mainPhoto) {
        isMainPhoto = mainPhoto;
    }
}
