package com.barterAuctions.portal.models.messages;

import com.barterAuctions.portal.models.auction.Auction;

import javax.persistence.*;

@Entity
@Table
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sender;
    private String recipient;
    @Lob
    private String message;
    private boolean isRead;
    @OneToOne
    private Auction auctionWhichConcerns;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Auction getAuctionWhichConcerns() {
        return auctionWhichConcerns;
    }

    public void setAuctionWhichConcerns(Auction auctionWhichConcerns) {
        this.auctionWhichConcerns = auctionWhichConcerns;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
