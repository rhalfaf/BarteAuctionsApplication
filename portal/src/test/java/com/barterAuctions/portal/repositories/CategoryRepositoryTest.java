package com.barterAuctions.portal.repositories;

import com.barterAuctions.portal.models.auction.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private CategoryRepository repository;

    private Category dummyCategory;
    private Category dummyCategory2;

    @BeforeEach
    void setUp() {
        dummyCategory = new Category("test category");
        dummyCategory2 = new Category("test category2");
        dummyCategory = entityManager.persistAndFlush(dummyCategory);
        dummyCategory2 = entityManager.persistAndFlush(dummyCategory2);
    }

    @Test
    void should_return_list_of_two_categories() {
        //when
        List<Category> result = repository.findAll();
        //then
        assertEquals(2, result.size());
        assertEquals(result.get(0).getCategoryName(),dummyCategory.getCategoryName());
        assertEquals(result.get(1).getCategoryName(),dummyCategory2.getCategoryName());
    }

    @Test
    void should_return_optional_of_category_searched_by_name() {
        //when
        Optional<Category> result =repository.findByCategoryName("test category");
        //then
        assertEquals(result.get().getCategoryName(),dummyCategory.getCategoryName());
    }
}