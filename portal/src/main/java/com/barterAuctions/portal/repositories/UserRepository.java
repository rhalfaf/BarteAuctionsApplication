package com.barterAuctions.portal.repositories;

import com.barterAuctions.portal.models.auction.Auction;
import com.barterAuctions.portal.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findByName(String name);

    User findByAuctions(Auction a);

    //@Query("select u from User u where u.name =:username and :auctionId member u.observedAuctions ")
    User findByObservedAuctionsAndName(Auction auction, String username);

    boolean existsByName(String name);

    boolean existsByEmail(String email);

}
