package com.barterAuctions.portal.services;

import com.barterAuctions.portal.models.auction.Category;
import com.barterAuctions.portal.repositories.CategoryRepository;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class CategoryServiceTest {


    private CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private CategoryService categoryService = new CategoryService(categoryRepository);
    private List<Category> listForTests;
    private Category dummyCategory1;
    private Category dummyCategory2;

    @BeforeEach
    public void init() {
        dummyCategory1 = new Category("dummyCategory1");
        dummyCategory2 = new Category("dummyCategory2");
        listForTests = new ArrayList<>();
        listForTests.addAll(List.of(dummyCategory1, dummyCategory2));
    }

    @Test
    void should_return_list_of_categories() {
        //given
        when(categoryRepository.findAll()).thenReturn(listForTests);
        //when
        List<Category> result = categoryService.findAll();
        //then
        assertEquals(2, result.size());
    }

    @Test
    void should_return_optional_from_category() {
        //given
        when(categoryRepository.findByCategoryName("dummyCategory1")).thenReturn(Optional.of(dummyCategory1));
        //when
        Optional<Category> result = categoryService.findByName("dummyCategory1");
        //then
        assertEquals(dummyCategory1.getCategoryName(),result.get().getCategoryName());
    }
    

}