package com.barterAuctions.portal.services;

import com.barterAuctions.portal.models.messages.Message;
import com.barterAuctions.portal.repositories.MessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    final
    MessagesRepository repository;

    public MessageService(MessagesRepository repository) {
        this.repository = repository;
    }

    public List<Message> findAllBySender(String sender){
        return repository.findAllBySender(sender);
    }

    public List<Message> findAllByRecipient(String recipient){
        return repository.findAllByRecipient(recipient);
    }

}
