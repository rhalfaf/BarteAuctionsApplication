package com.barterAuctions.portal.models.auction;

import javax.persistence.*;

@Entity
@Table
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private boolean isMainPhoto = false;
    private String type;
    @Column
    private byte[] imageByte;

    public Image(String name, boolean isMainPhoto, String type, byte[] imageByte) {
        this.name = name;
        this.isMainPhoto = isMainPhoto;
        this.type = type;
        this.imageByte = imageByte;
    }

    public Image() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getImageByte() {
        return imageByte;
    }

    public void setImageByte(byte[] imageByte) {

        this.imageByte = imageByte;
    }

    public boolean isMainPhoto() {
        return isMainPhoto;
    }

    public void setMainPhoto(boolean mainPhoto) {
        isMainPhoto = mainPhoto;
    }
}
