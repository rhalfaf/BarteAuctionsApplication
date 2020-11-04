package com.barterAuctions.portal.models.DTO;

import com.barterAuctions.portal.models.auction.Auction;
import com.barterAuctions.portal.models.user.User;

import java.util.List;

public class UserAuctionDTO {

    String name;
    String email;
    private List<Auction> auctions;

    public UserAuctionDTO() {
    }

    public UserAuctionDTO(String name, String email, List<Auction> auctions) {
        this.name = name;
        this.email = email;
        this.auctions = auctions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Auction> getAuctions() {
        return auctions;
    }

    public void setAuctions(List<Auction> auctions) {
        this.auctions = auctions;
    }
}
