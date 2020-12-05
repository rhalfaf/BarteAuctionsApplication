package com.barterAuctions.portal.services;

import com.barterAuctions.portal.models.DTO.AuctionDTO;
import com.barterAuctions.portal.models.auction.Auction;
import com.barterAuctions.portal.models.auction.Category;
import com.barterAuctions.portal.models.user.User;
import com.barterAuctions.portal.repositories.AuctionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
class AuctionServiceTest {

    private final AuctionRepository auctionRepositoryMock = mock(AuctionRepository.class);
    private final CategoryService categoryService = mock(CategoryService.class);
    private final UserService userService = mock(UserService.class);
    private final ModelMapper modelMapper = spy(ModelMapper.class);

    private final AuctionService auctionService = new AuctionService(auctionRepositoryMock, userService, modelMapper, categoryService);

    Category dummyCategory;
    private Auction dummyAuction1;
    private Auction dummyAuction2;
    private Auction dummyAuction3;
    private AuctionDTO dummyAuctionDTO1;
    private AuctionDTO auctionDTO;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("testUser");
        dummyCategory = new Category("testCategory");
        dummyAuction1 = new Auction(1L, "Warsaw", "dummy auction1", "foo bar", Collections.emptyList(), new BigDecimal(0), true, LocalDate.now(), LocalDate.now().plusDays(7), dummyCategory, user);
        dummyAuction2 = new Auction(1L, "Warsaw", "dummy auction2", "foo bar", Collections.emptyList(), new BigDecimal(0), true, LocalDate.now(), LocalDate.now().plusDays(7), dummyCategory, user);
        dummyAuction3 = new Auction(1L, "Warsaw", "dummy auction3", "foo bar", Collections.emptyList(), new BigDecimal(0), true, null, null, null, null);
        dummyAuctionDTO1 = new AuctionDTO();
        auctionDTO = new AuctionDTO(dummyAuction1);

    }

    @Test
    void when_findById_is_called_return_auctionDTO_mapped_from_dummy_Auction_object() {
        //given
        when(auctionRepositoryMock.findById(1L)).thenReturn(Optional.of(dummyAuction1));
        //when
        AuctionDTO auctionDTOFromDummyAuction = auctionService.findById(1L);
        //then
        Assertions.assertEquals(dummyAuction1.getTitle(), auctionDTOFromDummyAuction.getTitle());
        Assertions.assertEquals(dummyAuction1.getDescription(), auctionDTOFromDummyAuction.getDescription());
        Assertions.assertEquals(dummyAuction1.getId(), auctionDTOFromDummyAuction.getId());
    }

    @Test
    void when_findById_is_called_return_NoSuchElementException() {
        //then
        Assertions.assertThrows(NoSuchElementException.class, () -> auctionService.findById(1L));

    }

    @Test
    void when_search_return_Page_object_containing_one_element() {
        //given
        List<Auction> auctionsList = new ArrayList<>();
        auctionsList.add(dummyAuction1);
        Page<Auction> page = new PageImpl<>(auctionsList);
        when(auctionRepositoryMock.findAllByActiveTrueAndTitleIgnoreCaseContainingOrActiveTrueAndDescriptionIgnoreCaseContaining(anyString(), anyString(), any(Pageable.class))).thenReturn(page);
        //when
        Page<AuctionDTO> result = auctionService.searchAuctionsPageable("test", Pageable.unpaged());
        //then
        Assertions.assertEquals( 1,result.getSize());
        Assertions.assertEquals(AuctionDTO.class, result.get().findFirst().get().getClass());
    }

    @Test
    void should_return_auction_owner_object_User() {
        //given
        when(userService.findAuctionOwner(any(AuctionDTO.class))).thenReturn(user);
        //when
        User userTest = auctionService.findAuctionOwner(auctionDTO);
        //then
        Assertions.assertEquals(userTest, user);

    }

    @Test
    void should_return_auctions_list_Page_containing_two_active_auctions() {
        //given
        List<Auction> auctionsList = new ArrayList<>();
        auctionsList.add(dummyAuction1);
        auctionsList.add(dummyAuction2);
        Page<Auction> page = new PageImpl<>(auctionsList);
        when(auctionRepositoryMock.findAllByCategoryAndActive(any(Category.class), anyBoolean(),any(Pageable.class))).thenReturn(page);
        when(categoryService.findByName(dummyCategory.getCategoryName())).thenReturn(Optional.of(dummyCategory));
        //when
        Page<AuctionDTO> result = auctionService.findAllByCategoryPageable(dummyCategory.getCategoryName(),Pageable.unpaged());
        //then
        Assertions.assertEquals(2,result.getSize());
        Assertions.assertTrue(result.stream().noneMatch(dto -> (!dto.isActive())));
    }
    @Test
    void should_throw_NoSuchElementException(){
        when(auctionRepositoryMock.findAllByCategoryAndActive(any(Category.class),anyBoolean(),any(Pageable.class))).thenThrow(NoSuchElementException.class);
        Assertions.assertThrows(NoSuchElementException.class,() -> auctionService.findAllByCategoryPageable("Motoryzacja",Pageable.unpaged()));
    }

    @Test
    void should_return_list_of_two_auction_mapped_to_auctionDTO() {
        //give
        List<Long> idList = new ArrayList<>();
        idList.add(dummyAuction1.getId());
        idList.add(dummyAuction2.getId());
        when(auctionRepositoryMock.findAuctionsIdOnly()).thenReturn(idList);
        when(auctionRepositoryMock.findById(dummyAuction1.getId())).thenReturn(Optional.of(dummyAuction1));
        when(auctionRepositoryMock.findById(dummyAuction2.getId())).thenReturn(Optional.of(dummyAuction2));
        //when
        List<AuctionDTO> auctionForMainPage = auctionService.getAuctionsForMainPage(2);
        //then
        Assertions.assertTrue(auctionForMainPage.get(0) instanceof AuctionDTO);
        Assertions.assertEquals(2, auctionForMainPage.size());

    }
    @Test
    void should_return_list_with_one_auction_mapped_to_auctionDTO_even_if_numberOfElements_is_two() {
        //give
        List<Long> idList = new ArrayList<>();
        idList.add(dummyAuction1.getId());
        when(auctionRepositoryMock.findAuctionsIdOnly()).thenReturn(idList);
        when(auctionRepositoryMock.findById(dummyAuction1.getId())).thenReturn(Optional.of(dummyAuction1));
        //when
        List<AuctionDTO> auctionForMainPage = auctionService.getAuctionsForMainPage(2);
        //then
        Assertions.assertEquals(1, auctionForMainPage.size());

    }

    @Test
    @DisplayName("Should return saved auction object.")
    void should_return_saved_object() {
        //given
        when(auctionRepositoryMock.saveAndFlush(dummyAuction1)).thenReturn(dummyAuction1);
        //when
        Auction a = auctionService.save(dummyAuction1);
        //then
        Assertions.assertEquals(a,dummyAuction1);
    }

    @Test
    void should_return_auction_dto_add_auction_to_user_set_category_for_auction() {
        //given
        when(categoryService.findByName(dummyCategory.getCategoryName())).thenReturn(Optional.of(dummyCategory));
        when(userService.findByName("user")).thenReturn(user);
        when(auctionRepositoryMock.save(any(Auction.class))).thenReturn(dummyAuction3);
        user.setAuctions(new ArrayList<Auction>());
        //when
        AuctionDTO a = auctionService.addNewAuction(dummyAuctionDTO1,"testCategory","user",new MultipartFile[0]);
        //then
        Assertions.assertEquals("testCategory", a.getCategory().getCategoryName());
        Assertions.assertTrue(user.getAuctions().contains(dummyAuction3));
        Assertions.assertEquals(a.getTitle(),dummyAuctionDTO1.getTitle());
    }

    @Test
    void should_return_observed_auctions_list() {
        //given
        user.setObservedAuctions(new ArrayList<>());
        user.getObservedAuctions().add(dummyAuction1);
        when(userService.findByName("user")).thenReturn(user);
        //when
        List<AuctionDTO> observedAuctions = auctionService.observedAuctions("user");
        //then
        Assertions.assertEquals(1,observedAuctions.size());
        Assertions.assertEquals(dummyAuction1.getTitle(), observedAuctions.get(0).getTitle());
    }

    @Test
    void should_remove_auction_from_observed_and_return_updated_list_of_observed_auctions_dtos() {
        //given
        user.setObservedAuctions(new ArrayList<>());
        user.getObservedAuctions().add(dummyAuction1);
        user.getObservedAuctions().add(dummyAuction2);
        when(userService.findByName("user")).thenReturn(user);
        when(auctionRepositoryMock.findById(1L)).thenReturn(Optional.of(dummyAuction1));
        //when
        List<AuctionDTO> updatedList = auctionService.stopObserveAuction("user", 1L);
        //then
        Assertions.assertEquals(1, user.getObservedAuctions().size());
        verify(auctionRepositoryMock,times(1)).findById(1L);

    }

    @Test
    void should_return_observed_auctionsDTOs_list_when_wrong_id() {
        //given
        user.setObservedAuctions(new ArrayList<>());
        user.getObservedAuctions().add(dummyAuction1);
        user.getObservedAuctions().add(dummyAuction2);
        when(userService.findByName("user")).thenReturn(user);
        when(auctionRepositoryMock.findById(1L)).thenReturn(Optional.of(dummyAuction1));
        //when
        List<AuctionDTO> updatedList = auctionService.stopObserveAuction("user", 12L);
        //then
        Assertions.assertEquals(2,user.getObservedAuctions().size());

    }

    @Test
    void when_user_delete_auction_set_active_as_false() {
        //given
        when(auctionRepositoryMock.findById(1L)).thenReturn(Optional.of(dummyAuction1));
        //when
        auctionService.deleteAuction(1L);
        //then
        Assertions.assertFalse(dummyAuction1.isActive());
    }

    @Test
    void when_user_reissue_auction_active_should_be_set_as_true_and_set_new_start_and_expire_date() {
        //given
        dummyAuction3.setActive(false);
        when(auctionRepositoryMock.findById(1L)).thenReturn(Optional.of(dummyAuction3));
        //when
        auctionService.reIssueAuction(1L);
        //then
        Assertions.assertTrue(dummyAuction3.isActive());
        Assertions.assertTrue(dummyAuction3.getStartDate().isEqual(LocalDate.now()));
        Assertions.assertTrue(dummyAuction3.getExpireDate().isEqual(LocalDate.now().plusDays(7)));

    }


}