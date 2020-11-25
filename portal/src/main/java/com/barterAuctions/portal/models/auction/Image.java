package com.barterAuctions.portal.models.auction;

import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.persistence.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.NoSuchFileException;
import java.util.NoSuchElementException;
import java.util.Optional;

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
    @ManyToOne(fetch = FetchType.LAZY)
    Auction auction;

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

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }


    @ResponseBody
    public ResponseEntity<StreamingResponseBody> handleRequest() {
        StreamingResponseBody responseBody = out -> {
            out.write(imageByte);
            out.flush();
        };
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(responseBody);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Image)) return false;
        return id != null && id.equals(((Image) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
