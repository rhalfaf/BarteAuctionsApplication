package com.barterAuctions.portal.repositories;

import com.barterAuctions.portal.models.DTO.AuctionDTO;
import com.barterAuctions.portal.models.auction.Auction;
import com.barterAuctions.portal.models.user.User;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findByName(String name);

    User findByAuctions(Auction a);

    User findByObservedAuctionsAndName(Auction a, String username);

    boolean existsByName(String name);

    boolean existsByEmail(String email);

}
