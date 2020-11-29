package com.barterAuctions.portal.repositories;

import com.barterAuctions.portal.models.auction.Auction;
import com.barterAuctions.portal.models.auction.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {


    Auction findById(long id);

    //@Query("select a from Auction a where a.category =: category")
    List<Auction> findAllByCategory(Category category, Pageable pageable);

    @Query("SELECT id FROM Auction")
    List<Long> findAuctionsIdOnly();

    Page<Auction> findAllByTitleIgnoreCaseContainingOrDescriptionIgnoreCaseContaining(String searchPhrase, String searchPhrase1, Pageable pageable);


}
