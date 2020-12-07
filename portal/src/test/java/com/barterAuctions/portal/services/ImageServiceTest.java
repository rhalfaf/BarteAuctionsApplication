package com.barterAuctions.portal.services;

import com.barterAuctions.portal.models.auction.Image;
import com.barterAuctions.portal.repositories.ImageRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class ImageServiceTest {

    private ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
    private ImageService imageService = new ImageService(imageRepository);
    private Image image = new Image("testImage", true, "jpeg", new byte[0]);


    @Test
    void should_return_image() {
        //given
        when(imageRepository.findById(anyLong())).thenReturn(Optional.of(image));
        //when
        Image result = imageService.findById(1L);
        //then
        assertEquals(image.getName(), result.getName());
    }

    @Test
    void should_throw_NoSuchElementException() {
        //then

        assertThrows(NoSuchElementException.class, ()->imageService.findById(1L));
    }

    @Test
    void should_return_Image_object() {
        //given
        when(imageRepository.save(any(Image.class))).thenReturn(image);
        //when
        Image result = imageRepository.save(image);
        //then
        assertEquals(result,image);
    }
}