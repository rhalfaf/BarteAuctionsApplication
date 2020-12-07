package com.barterAuctions.portal.repositories;

import com.barterAuctions.portal.models.auction.Category;
import com.barterAuctions.portal.models.auction.Image;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
class ImageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ImageRepository repository;

    private Image dummyImage;

    @BeforeEach
    void setUp() {
        dummyImage = new Image("dummy image", true, "jpg", new byte[0]);
        dummyImage = entityManager.persistAndFlush(dummyImage);
    }

    @Test
    void findById() {
        //when
        Optional<Image> result = repository.findById(dummyImage.getId());
        //then
        assertEquals(dummyImage.getName(),result.get().getName());
    }
}