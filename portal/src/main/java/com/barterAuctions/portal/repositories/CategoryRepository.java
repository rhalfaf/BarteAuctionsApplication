package com.barterAuctions.portal.repositories;

import com.barterAuctions.portal.models.auction.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAll();

    Optional<Category> findByCategoryName(String name);

    List<Category> findAllByCategoryName(String category);


}
