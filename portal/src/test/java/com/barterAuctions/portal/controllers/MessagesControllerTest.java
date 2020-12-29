package com.barterAuctions.portal.controllers;

import com.barterAuctions.portal.config.customExceptions.UnauthorizedAccessException;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import static com.googlecode.catchexception.CatchException.*;
import static com.googlecode.catchexception.apis.CatchExceptionAssertJ.then;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessagesController.class)
@WithMockUser
class MessagesControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MessageService messageServiceMock;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private AuctionService auctionServiceMock;

    @MockBean
    private UserService userService;

    @MockBean
    private DataSource dataSource;

    private Message dummyMessage;
    private User dummyUser;
    private Auction dummyAuction;
    private AuctionDTO dummyAuctionDTO;

    @BeforeEach
    void setUp() {
        dummyMessage = new Message();
        dummyUser = new User("testUser", "test", true, "test@test.pl", new ArrayList<Auction>(), new Authorities(), new ArrayList<Auction>());
        dummyAuction = new Auction(1L, "Warszawa",
                "dummyTitle",
                "dummyDescription",
                null,
                null,
                true,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                new Category("testCategory"),
                dummyUser);
        dummyUser.addAuction(dummyAuction);
        dummyAuctionDTO = new AuctionDTO(dummyAuction);
    }

    @Test
    @WithMockUser(username = "testUserSender")
    void should_set_message_fields_and_pass_to_message_service() throws Exception {
        //given
        when(auctionServiceMock.findById(anyLong())).thenReturn(dummyAuctionDTO);
        //when
        ResultActions result = mvc.perform(post("/sendMessage")
                .with(csrf())
                .flashAttr("message", dummyMessage)
                .param("auctionId", "1")
                .param("auctionOwner", "testUser")
                .param("topic", "test topic"));
        //then
        result.andExpect(redirectedUrl("/auction/1"));
        Assertions.assertEquals("test topic", dummyMessage.getTopic());
        Assertions.assertEquals("testUserSender", dummyMessage.getSender());
        Assertions.assertEquals("testUser", dummyMessage.getRecipient());
        verify(messageServiceMock, times(1)).sendMessage(dummyMessage);
    }

    @Test
    @WithMockUser("test message sender")
    void should_return_view_with_message_if_user_have_appr_principals() throws Exception {
        //given
        //when
        ResultActions result = mvc.perform(post("/shMsg")
                .with(csrf())
                .flashAttr("message", dummyMessage)
                .param("messageText", "test message text")
                .param("messageTopic", "test message topic")
                .param("sender", "test message sender")
                .param("auctionOwner", "test message auctionOwner")
                .param("messageId", "1")
                .param("auctionId", "1"));
        //then
        result
                .andExpect(status().isOk())
                .andExpect(view().name("shwMessage"))
                .andExpect(model().attribute("messageText", "test message text"))
                .andExpect(model().attribute("sender", "test message sender"))
                .andExpect(model().attribute("auctionOwner", "test message auctionOwner"))
                .andExpect(model().attribute("auctionId", 1L))
                .andExpect(model().attribute("messageTopic", "test message topic"));
    }

    @Test
    @WithMockUser("user with out credentials")
    void should_throw_UnauthorizedException_if_user_try_show_message_and_is_not_recipient_nor_sender() throws Exception {
        //given
        //when
        ResultActions result = mvc.perform(post("/shMsg")
                .with(csrf())
                .flashAttr("message", dummyMessage)
                .param("messageText", "test message text")
                .param("messageTopic", "test message topic")
                .param("sender", "test message sender")
                .param("auctionOwner", "test message auctionOwner")
                .param("messageId", "1")
                .param("auctionId", "1"));
        //then
        result.andExpect(caughtException());
    }

    @Test
    void deleteForRecipient() {
        //given

        //when

        //then
    }

    @Test
    void deleteForSender() {
        //given

        //when

        //then
    }
}