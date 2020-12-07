package com.barterAuctions.portal.repositories;

import com.barterAuctions.portal.models.auction.Auction;
import com.barterAuctions.portal.models.auction.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;


public interface AuctionRepository extends JpaRepository<Auction, Long> {

    @Override
    Optional<Auction> findById(Long id);

    Page<Auction> findAllByCategoryAndActive(Category category, Boolean active, Pageable pageable);

    Page<Auction> findAllByActiveTrueAndTitleIgnoreCaseContainingOrActiveTrueAndDescriptionIgnoreCaseContaining(String searchPhrase, String searchPhrase1, Pageable pageable);

    @Query("SELECT a.id FROM Auction a where a.active = true")
    List<Long> findAuctionsIdOnly();


}
