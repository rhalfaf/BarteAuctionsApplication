package com.barterAuctions.portal.services;

import com.barterAuctions.portal.models.messages.Message;
import com.barterAuctions.portal.repositories.MessagesRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class MessageServiceTest {

    private MessagesRepository messagesRepository = Mockito.mock(MessagesRepository.class);
    private MessageService messageService = new MessageService(messagesRepository);
    private Message message1 = createTestMessages("message1");
    private Message message2 = createTestMessages("message2");

    @BeforeEach
    public void init() {
        when(messagesRepository.findAllBySender("user")).thenReturn(List.of(message1));
        when(messagesRepository.saveAndFlush(any(Message.class))).thenReturn(message1);

    }

    @Test
    void will_return_list_of_messages_via_sender() {
        //when
        List<Message> result = messageService.findAllBySender("user");
        //then
        assertEquals(result.get(0), message1);
    }

    @Test
    void will_return_list_of_messages_via_rec() {
        //when
        List<Message> result = messageService.findAllBySender("user");
        //then
        assertEquals(result.get(0), message1);
    }

    @Test
    void should_set_message_time_and_save_in_database() {
        //when
        messageService.sendMessage(message1);
        //then
        assertNotNull(message1.getDateTime());
        verify(messagesRepository, times(1)).saveAndFlush(message1);
    }

    @Test
    void should_return_list_of_messages_by_recipient_if_message_shall_be_visible_for_user() {
        //given
        message2.setShowRecipient(false);
        when(messagesRepository.findAllByRecipient("user")).thenReturn(List.of(message1,message2));
        //when
        List<Message> result = messageService.getReceiptedMessages("user");
        //then
        assertEquals(1,result.size());
        verify(messagesRepository, times(1)).findAllByRecipient("user");

    }
    @Test
    void should_return_list_of_messages_by_sender_if_message_shall_be_visible_for_user(){
        message2.setShowSender(false);
        when(messagesRepository.findAllBySender("user")).thenReturn(List.of(message1,message2));
        //when
        List<Message> result = messageService.getSentMessages("user");
        //then
        assertEquals(1,result.size());
        verify(messagesRepository, times(1)).findAllBySender("user");
    }

    @Test
    void should_change_message_read_status_to_true() {
        //when
        messageService.setMessageAsRead(1L);
        //then
        verify(messagesRepository).changeMessageReadStatus(1L);
    }

    @Test
    void should_change_message_read_status_and_return_message_text_as_string() {
        //given
        when(messagesRepository.returnMessageTextById(1L)).thenReturn("test");
        //when
        String result = messageService.readMessageById(1L);
        //then
        verify(messagesRepository, times(1)).changeMessageReadStatus(1L);
        assertEquals("test",result);

    }

    @Test
    void should_change_field_show_for_recipient_to_false() {
        //when
        messageService.deleteForRecipient(1L);
        //then
        verify(messagesRepository, times(1)).deleteForRecipient(1L);
    }

    @Test
    void should_change_field_show_for_sender_to_false() {
        //when
        messageService.deleteForSender(1L);
        //then
        verify(messagesRepository, times(1)).deleteForSender(1L);
    }

    private Message createTestMessages(String topic) {
        Message message = new Message();
        message.setId(1L);
        message.setSender("Sender");
        message.setRecipient("Recipient");
        message.setMessage("Test message");
        message.setTopic(topic);
        message.setRead(false);
        message.setDateTime(null);
        message.setAuctionWhichConcernsId(1L);
        message.setShowRecipient(true);
        message.setShowRecipient(true);


        return message;
    }
}