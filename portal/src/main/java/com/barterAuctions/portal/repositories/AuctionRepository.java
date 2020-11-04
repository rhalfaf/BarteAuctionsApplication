package com.barterAuctions.portal.repositories;

import com.barterAuctions.portal.models.auction.Auction;
import com.barterAuctions.portal.models.auction.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    Auction findById(long id);

    List<Auction> findAllByCategory(Category category);

    @Query("SELECT id FROM Auction")
    List<Long> findAuctionsIdOnly();



}
