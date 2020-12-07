package com.barterAuctions.portal.models.messages;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sender;
    private String recipient;
    @Column(length = 1000)
    private String message;
    private String topic;
    private Boolean isRead;
    private LocalDateTime dateTime;
    private Long auctionWhichConcernsId;
    private Boolean showSender;
    private Boolean showRecipient;


    public Message() {
        isRead = false;
        showSender = true;
        showRecipient = true;
    }

    public Message(String sender, String recipient, String message, String topic, Boolean isRead, LocalDateTime dateTime, Long auctionWhichConcernsId, Boolean showSender, Boolean showRecipient) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.topic = topic;
        this.isRead = isRead;
        this.dateTime = dateTime;
        this.auctionWhichConcernsId = auctionWhichConcernsId;
        this.showSender = showSender;
        this.showRecipient = showRecipient;
    }

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

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Long getAuctionWhichConcernsId() {
        return auctionWhichConcernsId;
    }

    public void setAuctionWhichConcernsId(Long auctionWhichConcernsId) {
        this.auctionWhichConcernsId = auctionWhichConcernsId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isShowSender() {
        return showSender;
    }

    public void setShowSender(boolean showSender) {
        this.showSender = showSender;
    }

    public boolean isShowRecipient() {
        return showRecipient;
    }

    public void setShowRecipient(boolean showRecipient) {
        this.showRecipient = showRecipient;
    }


}
