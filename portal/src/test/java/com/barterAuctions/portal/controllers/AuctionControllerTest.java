package com.barterAuctions.portal.controllers;

import com.barterAuctions.portal.config.customExceptions.UnauthorizedAccessException;
import com.barterAuctions.portal.models.DTO.AuctionDTO;
import com.barterAuctions.portal.models.auction.Auction;
import com.barterAuctions.portal.models.auction.Category;
import com.barterAuctions.portal.models.messages.Message;
import com.barterAuctions.portal.models.user.Authorities;
import com.barterAuctions.portal.models.user.User;
import com.barterAuctions.portal.services.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuctionController.class)
@WithMockUser
class AuctionControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private AuctionService auctionServiceMock;
    @MockBean
    private CategoryService categoryService;
    @MockBean
    private UserService userService;
    @MockBean
    private ImageService imageService;
    @MockBean
    private MessageService messageService;
    @MockBean
    private ModelMapper modelMapper;
    @MockBean
    private DataSource dataSource;

    private Auction dummyAuction;
    private AuctionDTO dummyAuctionDTO;
    private User dummyUser;
    private Category dummyCategory;

    @BeforeEach
    void setUp() {
        dummyUser = new User("test", "test", true, "test@wp.pl", new ArrayList<>(), new Authorities(), new ArrayList<>());
        dummyCategory = new Category("test");
        dummyAuction = new Auction(1L, "Warsaw", "dummy auction1", "foo bar", Collections.emptyList(), new BigDecimal(0), true, LocalDate.now(), LocalDate.now().plusDays(7), dummyCategory, dummyUser);
        dummyAuctionDTO = new AuctionDTO(dummyAuction);
    }

    @Test
    void should_return_single_auction_object_with_additional_model_attr() throws Exception {
        //given
        when(auctionServiceMock.findById(1L)).thenReturn(dummyAuctionDTO);
        when(auctionServiceMock.findAuctionOwner(dummyAuctionDTO)).thenReturn(dummyUser);
        //when
        ResultActions result = mvc.perform(get("/auction/1"));
        //then
        result.andExpect(status().isOk())
                .andExpect(view().name("item-single"))
                .andExpect(model().attribute("auctionOwner", hasProperty("name", is("test"))))
                .andExpect(model().attribute("auction", hasProperty("title", is("dummy auction1"))))
                .andExpect(model().attribute("message", instanceOf(Message.class)));
        verify(auctionServiceMock).findById(1L);
        verify(auctionServiceMock).findAuctionOwner(dummyAuctionDTO);
    }

    @Test
    void should_catch_NoSuchElementException_and_add_error_to_model_attr() throws Exception {
        //given
        when(auctionServiceMock.findById(1L)).thenThrow(NoSuchElementException.class);
        when(auctionServiceMock.findAuctionOwner(dummyAuctionDTO)).thenReturn(dummyUser);
        //when
        ResultActions result = mvc.perform(get("/auction/1"));
        //then
        result.andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("error", "Nie znaleziono aukcji."));
        verify(auctionServiceMock).findById(1L);
        verify(auctionServiceMock, never()).findAuctionOwner(dummyAuctionDTO);
    }


    @Test
    void should_return_page_with_one_auction_and_create_model_containing_Page_attr() throws Exception {
        //given
        Page<AuctionDTO> auctionsDTOs = new PageImpl<>(new ArrayList<>(List.of(dummyAuctionDTO)));
        when(auctionServiceMock.searchAuctionsPageable(eq("test"), ArgumentMatchers.any(Pageable.class))).thenReturn(auctionsDTOs);
        //when
        ResultActions result = mvc.perform(get("/searchPageable").param("inParam", "test").param("page", "1").param("size", "1"));
        //then
        result.andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attribute("auctions", instanceOf(Page.class)))
                .andExpect(model().attribute("inParam", "test"))
                .andExpect(model().attribute("totalPages", 1));
    }

    @Test
    void should_return_page_with_one_error_attribute_when_searched_phrase_was_not_found() throws Exception {
        //given
        Page<AuctionDTO> auctionsDTOs = new PageImpl<>(new ArrayList<>(Collections.emptyList()));
        when(auctionServiceMock.searchAuctionsPageable(eq("test"), ArgumentMatchers.any(Pageable.class))).thenReturn(auctionsDTOs);
        //when
        ResultActions result = mvc.perform(get("/searchPageable").param("inParam", "test").param("page", "1").param("size", "1"));
        //then
        result.andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("error", "Niestety nie znaleziono szukanej frazy."));
    }


    @Test
    void should_return_list_of_auctions_found_by_category_containing_one_element() throws Exception {
        //given
        Page<AuctionDTO> auctionsDTOs = new PageImpl<>(new ArrayList<>(List.of(dummyAuctionDTO)));
        when(auctionServiceMock.findAllByCategoryPageable(eq("test"), ArgumentMatchers.any(Pageable.class))).thenReturn(auctionsDTOs);
        //when
        ResultActions result = mvc.perform(get("/list/test").param("inParam", "test").param("page", "1").param("size", "1"));
        //then
        result.andExpect(view().name("itemsByCategory"))
                .andExpect(model().attribute("inParam", "test"))
                .andExpect(model().attribute("auctions", instanceOf(Page.class)))
                .andExpect(model().attribute("totalPages", 1));

    }

    @Test
    void should_return_model_with_error_attr() throws Exception {
        //given
        when(auctionServiceMock.findAllByCategoryPageable(eq("test"), ArgumentMatchers.any(Pageable.class))).thenThrow(NoSuchElementException.class);
        //when
        ResultActions result = mvc.perform(get("/list/test").param("inParam", "test").param("page", "1").param("size", "1"));
        //then
        result.andExpect(view().name("index"))
                .andExpect(model().attribute("error", "Nie znaleziono kategorii."));

    }

    @Test
    void should_return_list_of_auctions_found_by_user_name() throws Exception {
        //given
        when(userService.findAllActiveAuctionsOfAUser(eq("test"))).thenReturn(new ArrayList<>(List.of(dummyAuctionDTO)));
        //when
        ResultActions result = mvc.perform(get("/showAuctions/test"));
        //then
        result.andExpect(view().name("items"))
                .andExpect(model().attribute("auctions", hasItem(dummyAuctionDTO)));
    }

    @Test
    void should_return_model_with_error_attr_after_catch_NoSuchElementException() throws Exception {
        //given
        when(userService.findAllActiveAuctionsOfAUser(eq("test"))).thenThrow(NoSuchElementException.class);
        //when
        ResultActions result = mvc.perform(get("/showAuctions/test"));
        //then
        result.andExpect(view().name("index"))
                .andExpect(model().attribute("error", "Użytkownik o podanej nazwie nie istniej."));
    }

    @Test
    void should_return_list_of_auctions_owned_by_logged_user() throws Exception {
        //given
        when(userService.findAllAuctionsOfAUser(anyString())).thenReturn(new ArrayList<>(List.of(dummyAuctionDTO)));
        //when
        ResultActions result = mvc.perform(get("/myItems"));
        //then
        result.andExpect(view().name("myItems"))
                .andExpect(model().attribute("auctions", hasItem(dummyAuctionDTO)));

    }

    @Test
    void should_catch_NoSuchElementException_and_return_model_with_error_attr() throws Exception {
        //given
        when(userService.findAllAuctionsOfAUser(anyString())).thenThrow(new NoSuchElementException("test catch exception"));
        //when
        ResultActions result = mvc.perform(get("/myItems"));
        //then
        result.andExpect(view().name("index"))
                .andExpect(model().attribute("error", "test catch exception"));
    }

    @Test
    void should_call_auctionService_addAuctionToObserved_method_and_pass_param_with_auctionId_and_user_name() throws Exception {
        //given
        when(auctionServiceMock.findById(1L)).thenReturn(dummyAuctionDTO);
        when(auctionServiceMock.findAuctionOwner(dummyAuctionDTO)).thenReturn(dummyUser);
        //when
        ResultActions result = mvc.perform(get("/addToObserved/1"));
        //then
        result.andExpect(status().is3xxRedirection());
        verify(userService).addAuctionToObserved(eq("user"), eq(1L));
    }

    @Test
    @WithMockUser(username = "test")
    void should_return_model_with_error_message_when_user_try_add_to_observe_own_auction() throws Exception {
        //given
        when(auctionServiceMock.findById(1L)).thenReturn(dummyAuctionDTO);
        when(auctionServiceMock.findAuctionOwner(dummyAuctionDTO)).thenReturn(dummyUser);
        //when
        ResultActions result = mvc.perform(get("/addToObserved/1"));
        //then
        result.andExpect(matchAll(
                status().is3xxRedirection(),
                flash().attribute("error", "Nie możesz obserwować własnej auckji."),
                redirectedUrl("/auction/1")));
    }

    @Test
    void should_catch_IllegalStateException_when_user_try_add_to_observed_same_auction_more_then_one_time() throws Exception {
        //given
        when(auctionServiceMock.findAuctionOwner(dummyAuctionDTO)).thenReturn(dummyUser);
        when(auctionServiceMock.findById(1L)).thenReturn(dummyAuctionDTO);
        doThrow(new IllegalStateException("test catch exception")).when(userService).addAuctionToObserved("user", dummyAuctionDTO.getId());
        //when
        ResultActions result = mvc.perform(get("/addToObserved/1"));
        //then
        result.andExpect(matchAll(
                status().is3xxRedirection(),
                flash().attribute("error", "test catch exception"),
                redirectedUrl("/auction/1")));
    }

    @Test
    void should_return_list_of_observe_auctions_found_by_logged_user_name() throws Exception {
        //given
        when(auctionServiceMock.observedAuctions(anyString())).thenReturn(List.of(dummyAuctionDTO));
        //when
        ResultActions result = mvc.perform(get("/getObservedAuctions"));
        //then
        result.andExpect(matchAll(
                status().isOk(),
                view().name("observedAuctions"),
                model().attribute("auctions", hasItem(dummyAuctionDTO))));
    }

    @Test
    void should_return_model_with_new_AuctionDTO_object() throws Exception {
        //when
        ResultActions result = mvc.perform(get("/addNewAuction"));
        //then
        result.andExpect(matchAll(
                view().name("addNewAuction"),
                model().attribute("auction", instanceOf(AuctionDTO.class)),
                model().attribute("auction", hasProperty("title", is(emptyOrNullString())))));
    }

    @Test
    void should_save_auction_to_DB_and_redirect_to_all_auctions_of_user() throws Exception {
        //when
        ResultActions result = mvc.perform(post("/saveAuction").with(csrf()).flashAttr("auction", dummyAuctionDTO).param("categoryName", dummyCategory.getCategoryName()));
        //then
        verify(auctionServiceMock).addNewAuction(dummyAuctionDTO, dummyCategory.getCategoryName(), "user", null);
        result.andExpect(redirectedUrl("showAuctions/user"));
    }

    @Test
    void should_return_error_when_uploaded_file_is_bigger_then_5_Mb() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile("test", "originalFileName", MediaType.IMAGE_JPEG_VALUE, "testFile".getBytes(StandardCharsets.UTF_8)) {
            @Override
            public byte[] getBytes() throws IOException {
                return new byte[5242881];
            }
        };
        //when
        ResultActions result = mvc.perform(multipart("/saveAuction").file("auctionImages", image.getBytes()).with(csrf()).flashAttr("auction", dummyAuctionDTO).param("categoryName", dummyCategory.getCategoryName()));
        //then
        result.andExpect(view().name("addNewAuction"));
        result.andExpect(model().attribute("error", "Plik ma za duży rozmiar, maksymalny rozmair to 5 Mb."));

    }

    @Test
    void should_return_status_400_when_required_filed_is_empty() throws Exception {
        //given
        dummyAuctionDTO.setTitle("");
        //when
        ResultActions result = mvc.perform(post("/saveAuction").with(csrf()).flashAttr("auction", dummyAuctionDTO).param("categoryName", dummyCategory.getCategoryName()));
        //then
        result.andExpect(status().is4xxClientError());
    }

    @Test
    void should_call_auctionService_delete_method_with_pass_param() throws Exception {
        //when
        mvc.perform(post("/deleteAuction").with(csrf()).param("auctionId", "1"));
        //then
        verify(auctionServiceMock).deleteAuction(1L);
    }

    @Test
    void should_catch_UnauthorizedAccess_and_redirect_to_index() throws Exception {
        doThrow(new UnauthorizedAccessException()).when(auctionServiceMock).deleteAuction(anyLong());
        //when
        ResultActions result = mvc.perform(post("/deleteAuction").with(csrf()).param("auctionId", "1"));
        //then
        verify(auctionServiceMock).deleteAuction(1L);
        result.andExpect(flash().attribute("error", "Nie masz uprawnień do wykonania tej akcji."));
        result.andExpect(redirectedUrl("/"));
    }

    @Test
    void should_change_call_service_to_change_status_to_active_true_if_no_error_occur() throws Exception {
        //when
        ResultActions result = mvc.perform(post("/re-issue").with(csrf()).param("auctionId", "1"));
        //then
        verify(auctionServiceMock).reIssueAuction(1L);
        result.andExpect(redirectedUrl("/myItems"));
    }

    @Test
    void should_catch_NoSuchElementException_thrown_up_by_AuctionService() throws Exception {
        doThrow(new NoSuchElementException("Test exception")).when(auctionServiceMock).reIssueAuction(anyLong());
        //when
        ResultActions result = mvc.perform(post("/re-issue").with(csrf()).param("auctionId", "1"));
        //then
        verify(auctionServiceMock).reIssueAuction(1L);
        result.andExpect(flash().attributeExists("error"));
        result.andExpect(redirectedUrl("/myItems"));
    }

    @Test
    void should_catch_UnauthorizedAccessException_thrown_up_by_AuctionService() throws Exception {
        doThrow(new UnauthorizedAccessException()).when(auctionServiceMock).reIssueAuction(anyLong());
        //when
        ResultActions result = mvc.perform(post("/re-issue").with(csrf()).param("auctionId", "1"));
        //then
        verify(auctionServiceMock).reIssueAuction(1L);
        result.andExpect(flash().attributeExists("error"));
        result.andExpect(flash().attribute("error", "Nie masz uprawnień do wykonania tej akcji."));
        result.andExpect(redirectedUrl("/myItems"));
    }

}