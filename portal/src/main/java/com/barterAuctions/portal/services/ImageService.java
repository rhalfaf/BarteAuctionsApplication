package com.barterAuctions.portal.services;

import com.barterAuctions.portal.models.auction.Image;
import com.barterAuctions.portal.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

    final ImageRepository imageRepository;


    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image findById(Long id){
        return imageRepository.findById(id).orElseThrow();
    }

    public Image save(Image image){
        return imageRepository.saveAndFlush(image);
    }

}
