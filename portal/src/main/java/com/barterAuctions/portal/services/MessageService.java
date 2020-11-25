package com.barterAuctions.portal.services;

import com.barterAuctions.portal.models.messages.Message;
import com.barterAuctions.portal.repositories.MessagesRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    final MessagesRepository repository;

    public MessageService(MessagesRepository repository) {
        this.repository = repository;
    }

    public List<Message> findAllBySender(String sender){
        return repository.findAllBySender(sender);
    }

    public List<Message> findAllByRecipient(String recipient){
        return repository.findAllByRecipient(recipient);
    }

    public void sendMessage(Message message){
        message.setDateTime(LocalDateTime.now().with(LocalTime.now()));
        repository.saveAndFlush(message);
    }

    public List<Message> getReceiptedMessages(String recipient){

        return repository.findAllByRecipient(recipient).stream().filter(Message::isShowRecipient).collect(Collectors.toList());
    }

    public List<Message> getSentMessages(String sender){

        return repository.findAllBySender(sender).stream().filter(Message::isShowSender).collect(Collectors.toList());
    }

    public void setMessageAsReaded(Long id){
        repository.changeMessageReadStatus(id);
    }

    public String readMessageById(Long id){
        repository.changeMessageReadStatus(id);
        return repository.returnMessageTextById(id);
    }

    public void deleteForRecipient(Long id){
        repository.deleteForRecipient(id);
    }

    public void deleteForSender(Long id){
        repository.deleteForSender(id);
    }

}
