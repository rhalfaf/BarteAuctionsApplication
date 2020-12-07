package com.barterAuctions.portal.repositories;

import com.barterAuctions.portal.models.auction.Auction;
import com.barterAuctions.portal.models.auction.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
class AuctionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private AuctionRepository repository;

    private Auction dummyAuction1Active;
    private Auction dummyAuction2NotActive;
    private Category dummyCategory;

    @BeforeEach
    void setUp() {
        dummyCategory = new Category("dummyCategory");
        dummyAuction1Active = new Auction(null, "Warsaw", "dummy auction1", "foo bar",
                Collections.emptyList(), new BigDecimal(0), true, LocalDate.now(),
                LocalDate.now().plusDays(7), dummyCategory, null);
        dummyAuction2NotActive = new Auction(null, "Warsaw", "dummy auction2", "foo bar",
                Collections.emptyList(), new BigDecimal(0), false, LocalDate.now(),
                LocalDate.now().plusDays(7), dummyCategory, null);
        dummyCategory = entityManager.persistAndFlush(dummyCategory);
        dummyAuction1Active = entityManager.persistAndFlush(dummyAuction1Active);
        dummyAuction2NotActive = entityManager.persistAndFlush(dummyAuction2NotActive);
    }

    @Test
    void should_return_optional_of_auction_found_by_id() {
        //when
        Optional<Auction> result = repository.findById(dummyAuction1Active.getId());
        //then
        Assertions.assertEquals(dummyAuction1Active.getTitle(), result.get().getTitle());
    }

    @Test
    void should_return_page_with_two_active_auctions() {
        //when
        dummyAuction2NotActive.setActive(true);
        Page<Auction> result = repository.findAllByCategoryAndActive(dummyCategory, true, Pageable.unpaged());
        //then
        Assertions.assertEquals(dummyAuction1Active.getTitle(), result.get().findFirst().get().getTitle());
        Assertions.assertEquals(2, result.getSize());
    }

    @Test
    void should_return_page_with_one_active_auctions() {
        //when
        Page<Auction> result = repository.findAllByCategoryAndActive(dummyCategory, true, Pageable.unpaged());
        //then
        Assertions.assertEquals(dummyAuction1Active.getTitle(), result.get().findFirst().get().getTitle());
        Assertions.assertEquals(1, result.getSize());
    }

    @Test
    void should_return_list_of_auctions_ids_only_if_action_is_active_containing_one_id() {
        //when
        List<Long> result = repository.findAuctionsIdOnly();
        //then
        Assertions.assertEquals(1,result.size());
        Assertions.assertEquals(1L,result.get(0));
    }

    @Test
    void should_return_page_of_auctions_where_description_or_title_contain_searched_phrase_and_is_active_ignore_cases() {
        //given
        dummyAuction2NotActive.setActive(true);
        //when
        Page<Auction> result = repository.
        findAllByActiveTrueAndTitleIgnoreCaseContainingOrActiveTrueAndDescriptionIgnoreCaseContaining(
                "dummy auction2",
                "FOO BAR",
                Pageable.unpaged());
        //then
        result.forEach(auction -> System.out.println(auction.getTitle()));
        Assertions.assertEquals(2,result.getSize());
    }
}