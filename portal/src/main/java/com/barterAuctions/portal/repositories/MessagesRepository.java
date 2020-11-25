package com.barterAuctions.portal.repositories;

import com.barterAuctions.portal.models.messages.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface MessagesRepository extends JpaRepository<Message, Long> {

    List<Message> findAllBySender(String sender);

    List<Message> findAllByRecipient(String recipient);

    @Query("SELECT message FROM Message  WHERE  id = :id")
    String returnMessageTextById(Long id);

    @Modifying
    @Query("update Message m set m.isRead = true where m.id = :id")
    void changeMessageReadStatus(Long id);

    @Modifying
    @Query("update Message m set m.showRecipient = false where m.id = :id")
    void deleteForRecipient(Long id);

    @Modifying
    @Query("update Message m set m.showSender = false where m.id = :id")
    void deleteForSender(Long id);

}
