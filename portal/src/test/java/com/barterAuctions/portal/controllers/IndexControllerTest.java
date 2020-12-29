package com.barterAuctions.portal.controllers;

import com.barterAuctions.portal.models.DTO.AuctionDTO;
import com.barterAuctions.portal.models.auction.Auction;
import com.barterAuctions.portal.models.auction.Category;
import com.barterAuctions.portal.models.messages.Message;
import com.barterAuctions.portal.models.user.Authorities;
import com.barterAuctions.portal.models.user.User;
import com.barterAuctions.portal.services.AuctionService;
import com.barterAuctions.portal.services.CategoryService;
import com.barterAuctions.portal.services.MessageService;
import com.barterAuctions.portal.services.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.ui.Model;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(IndexController.class)
@WithMockUser
class IndexControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CategoryService categoryServiceMock;

    @MockBean
    private AuctionService auctionServiceMock;

    @MockBean
    private UserService userServiceMock;

    @MockBean
    private MessageService messageServiceMock;

    @MockBean
    private DataSource dataSource;

    private Auction dummyAuction;
    private AuctionDTO dummyAuctionDTO;
    private User dummyUser;
    private Category dummyCategory;
    private Message dummyMessage = createTestMessages("test");
    private List<AuctionDTO> auctionsForMainPage;

    @BeforeEach
    void setUp() {
        dummyUser = new User("test", "test", true, "test@wp.pl", new ArrayList<>(), new Authorities(), new ArrayList<>());
        dummyCategory = new Category("test");
        dummyAuction = new Auction(1L, "Warsaw", "dummy auction1", "foo bar", Collections.emptyList(), new BigDecimal(0), true, LocalDate.now(), LocalDate.now().plusDays(7), dummyCategory, dummyUser);
        dummyAuctionDTO = new AuctionDTO(dummyAuction);
        auctionsForMainPage = new ArrayList<>();
        auctionsForMainPage.add(dummyAuctionDTO);
    }

    @Test
    void after_start_application_controller_advice_should_add_two_attributes_to_model() throws Exception {
        //given
        IndexController indexController = new IndexController(categoryServiceMock,auctionServiceMock,userServiceMock,messageServiceMock);
        when(auctionServiceMock.getAuctionsForMainPage(anyInt())).thenReturn(auctionsForMainPage);
        when(categoryServiceMock.findAll()).thenReturn(new ArrayList<>(List.of(dummyCategory)));
        //when
        ResultActions result = mvc.perform(get("/"));
        //then
        result.andExpect(model().attribute("thumbnails", hasItem(dummyAuctionDTO)));
        result.andExpect(model().attribute("categories",hasItem("test")));
    }

    @Test
    @WithMockUser(username = "test")
    void should_return_true_and_add_model_attribute_with_number_of_unread_message_equals_one() throws Exception {
        //given
        List<Message> unreadMessage = new ArrayList<>(List.of(dummyMessage));
        when(userServiceMock.findByName(eq("test"))).thenReturn(dummyUser);
        when(messageServiceMock.getReceiptedMessages(anyString())).thenReturn(unreadMessage);
        //when
        ResultActions result = mvc.perform(get("/"));
        //then
        result.andExpect(model().attribute("unreadMessages",1L));
    }

    @Test
    @WithMockUser(username = "test")
    void should_return_true_and_add_model_attribute_with_number_of_unread_message_equals_null() throws Exception {
        //given
        dummyMessage.setRead(true);
        List<Message> unreadMessage = new ArrayList<>(List.of(dummyMessage));
        when(userServiceMock.findByName(eq("test"))).thenReturn(dummyUser);
        when(messageServiceMock.getReceiptedMessages(anyString())).thenReturn(unreadMessage);
        //when
        ResultActions result = mvc.perform(get("/"));
        //then
        result.andExpect(model().attribute("unreadMessages",nullValue()));
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