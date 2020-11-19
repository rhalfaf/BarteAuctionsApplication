package com.barterAuctions.portal.repositories;

import com.barterAuctions.portal.models.messages.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessagesRepository extends JpaRepository<Message, Long> {

    List<Message> findAllBySender(String sender);

    List<Message> findAllByRecipient(String recipient);

}
