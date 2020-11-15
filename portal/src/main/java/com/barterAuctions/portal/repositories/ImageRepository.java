package com.barterAuctions.portal.repositories;

import com.barterAuctions.portal.models.auction.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findById(Long id);

}
