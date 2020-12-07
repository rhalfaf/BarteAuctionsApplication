package com.barterAuctions.portal.repositories;

import com.barterAuctions.portal.models.messages.Message;
import com.barterAuctions.portal.models.user.Authorities;
import com.barterAuctions.portal.models.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
class MessagesRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private MessagesRepository repository;
    @Autowired
    private UserRepository userRepository;

    private User dummyUser;
    private Message dummyMessage;


    @BeforeEach
    void setUp() {
        dummyUser = new User("test", "test", true, "test@test.pl", new ArrayList<>(), new Authorities(), new ArrayList<>());
        dummyMessage = new Message(dummyUser.getName(),
                dummyUser.getName(),
                "dummy message",
                "topic",
                false,
                LocalDateTime.now(),
                1L,
                true,
                true);
        dummyUser = entityManager.persistAndFlush(dummyUser);
        dummyMessage = entityManager.persistAndFlush(dummyMessage);
    }

    @Test
    void should_return_list_of_message_found_by_sender_name_pass_as_string() {
        //when
        List<Message> result = repository.findAllBySender(dummyUser.getName());
        //then
        assertEquals(1, result.size());
        assertEquals(result.get(0).getMessage(), "dummy message");
    }

    @Test
    void should_return_empty_list_of_message_found_by_sender_name_pass_as_string() {
        //when
        List<Message> result = repository.findAllBySender("fake name");
        //then
        assertEquals(0, result.size());

    }

    @Test
    void should_return_list_of_message_found_by_recipient_name_pass_as_string() {
        //when
        List<Message> result = repository.findAllByRecipient(dummyUser.getName());
        //then
        assertEquals(1, result.size());
        assertEquals(result.get(0).getMessage(), "dummy message");
    }

    @Test
    void should_return_message_text_as_string_found_by_id() {
        //when
        String result = repository.returnMessageTextById(dummyMessage.getId());
        //then
        assertEquals(dummyMessage.getMessage(), result);
    }

    @Test
    void should_change_message_status_read_to_true() {
        //when
        boolean beforeCallMethodStatus = dummyMessage.isRead();
        repository.changeMessageReadStatus(dummyMessage.getId());
        Optional<Message> m = repository.findById(dummyMessage.getId());
        boolean afterCallMethodStatus = m.get().isRead();
        //then
        assertFalse(beforeCallMethodStatus);
        assertTrue(afterCallMethodStatus);
    }

    @Test
    void should_update_field_showRecipient_to_false() {
        //when
        repository.deleteForRecipient(dummyMessage.getId());
        Optional<Message> result = repository.findById(dummyMessage.getId());
        //then
        assertTrue(dummyMessage.isShowRecipient());
        assertFalse(result.get().isShowRecipient());
    }

    @Test
    void should_update_field_showSender_to_false() {
        //when
        repository.deleteForSender(dummyMessage.getId());
        Optional<Message> result = repository.findById(dummyMessage.getId());
        //then
        assertTrue(dummyMessage.isShowSender());
        assertFalse(result.get().isShowSender());
    }
}