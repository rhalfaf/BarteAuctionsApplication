package com.barterAuctions.portal.services;

import com.barterAuctions.portal.models.auction.Image;
import com.barterAuctions.portal.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ImageService {

    final ImageRepository imageRepository;


    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image findById(Long id) {
      Image i = imageRepository.findById(id).orElseThrow();


        return i;
    }

    public Image save(Image image) {
        return imageRepository.saveAndFlush(image);
    }

}
