package com.barterAuctions.portal.services;

import com.barterAuctions.portal.models.DTO.AuctionDTO;
import com.barterAuctions.portal.models.auction.Auction;
import com.barterAuctions.portal.models.user.Authorities;
import com.barterAuctions.portal.models.user.Role;
import com.barterAuctions.portal.models.user.User;
import com.barterAuctions.portal.repositories.AuctionRepository;
import com.barterAuctions.portal.repositories.RoleRepository;
import com.barterAuctions.portal.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class UserServiceTest {

    private final BCryptPasswordEncoder bCryptPasswordEncoder = spy(BCryptPasswordEncoder.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final RoleRepository roleRepository = mock(RoleRepository.class);
    private final AuctionRepository auctionRepository = mock(AuctionRepository.class);
    private final ModelMapper modelMapper = spy(ModelMapper.class);

    private final UserService userService = new UserService(bCryptPasswordEncoder, userRepository, roleRepository, auctionRepository, modelMapper);
    private final Auction dummyAuction1 = new Auction(1L, "Warsaw", "dummy auction1", "foo bar",Collections.emptyList(), new BigDecimal(0), true, LocalDate.now(), LocalDate.now().plusDays(7), null, null);
    private final Auction dummyAuction2 = new Auction(1L, "Warsaw", "dummy auction2", "foo bar",Collections.emptyList(), new BigDecimal(0), false, LocalDate.now(), LocalDate.now().plusDays(7), null, null);
    private final AuctionDTO dummyDTO =  new AuctionDTO(dummyAuction1);
    private final User dummyUser = new User("test","test",true,"test@test.pl",new ArrayList<>(),new Authorities(),new ArrayList<>());

    @BeforeEach
    public void init(){

    }


    @Test
    void should_return_User_object_after_pass_string_with_name_as_parameter() {
        //given
        when(userRepository.findByName("test")).thenReturn(dummyUser);
        //when
        User result = userService.findByName("test");
        //then
        assertEquals(dummyUser.getName(), result.getName());
    }

    @Test
    void should_valid_password_set_user_enable_hash_password_and_return_User_object() {
        //given
        dummyUser.setEnabled(false);
        dummyUser.setAuthorities(null);
        when(userRepository.existsByEmail(dummyUser.getEmail())).thenReturn(false);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(dummyUser);
        when(roleRepository.findByRole(anyString())).thenReturn(new Role("test_role"));
        //when
        User result = userService.registerNewUser(dummyUser,"test");
        //then
        assertNotNull(result.getAuthorities());
        assertTrue(result.getEnabled());
        assertEquals(dummyUser.getName(),result.getName());
        assertEquals(dummyUser.getAuthorities().getRole().getRole(),"test_role");
    }

    @Test
    void should_throw_IllegalStateException_when_pass_different_password_for_confirmation() {
        //then
        assertThrows(IllegalStateException.class,()->userService.registerNewUser(dummyUser,"wrong password"),"Hasła muszą być identyczne");
    }

    @Test
    void should_throw_IllegalArgumentException_when_user_name_is_already_registered() {
        //given
        when(userRepository.existsByName(anyString())).thenReturn(true);
        //then
        assertThrows(IllegalArgumentException.class,()->userService.registerNewUser(dummyUser,"test"),"Użytkownik o podanej nazwie już istnieje.");
    }

    @Test
    void should_throw_IllegalArgumentException_when_email_is_already_registered() {
        //given
        when(userRepository.existsByName(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        //then
        assertThrows(IllegalArgumentException.class,()->userService.registerNewUser(dummyUser,"test"),"Podany email jest już zarejstrowany.");
    }

    @Test
    void should_return_user_auction_owner_when_pass_auction_as_a_argument() {
        //given
        when(userRepository.findByAuctions(any(Auction.class))).thenReturn(dummyUser);
        //when
        User result = userService.findAuctionOwner(dummyDTO);
        //then
        assertEquals(result.getName(),dummyUser.getName());
        verify(modelMapper, times(1)).map(dummyDTO, Auction.class);
    }

    @Test
    void should_return_list_containing_one_object_AuctionDTO_active() {
        //given
        dummyUser.getAuctions().addAll(List.of(dummyAuction1,dummyAuction2));
        when(userRepository.findByName("user")).thenReturn(dummyUser);
        //when
        List<AuctionDTO> result = userService.findAllActiveAuctionsOfAUser("user");
        //then
        assertEquals(1,result.size());
        assertTrue(result.get(0).isActive());
        assertEquals(result.get(0).getTitle(), "dummy auction1");
    }

    @Test
    void should_throw_NoSuchElementException_when_user_is_null() {
        //given
        when(userRepository.findByName(anyString())).thenReturn(null);
        //then
        assertThrows(NoSuchElementException.class,()->userService.findAllActiveAuctionsOfAUser("user"),"Użytkownik o podanej nazwie nie istniej.");
        assertThrows(NoSuchElementException.class,()->userService.findAllAuctionsOfAUser("user"),"Użytkownik o podanej nazwie nie istniej.");
    }

    @Test
    void should_return_list_containing_two_objects_AuctionDTO_one_active_true_second_active_false() {
        //given
        dummyUser.getAuctions().addAll(List.of(dummyAuction1,dummyAuction2));
        when(userRepository.findByName("user")).thenReturn(dummyUser);
        //when
        List<AuctionDTO> result = userService.findAllAuctionsOfAUser("user");
        //then
        assertEquals(2,result.size());
        assertTrue(result.get(0).isActive());
        assertFalse(result.get(1).isActive());
        assertEquals(result.get(0).getTitle(), "dummy auction1");
        assertEquals(result.get(1).getTitle(), "dummy auction2");

    }

    @Test
    void should_add_passed_auction_to_observed_for_user_founded_by_name() {
        //given
        when(userRepository.findByName("user")).thenReturn(dummyUser);
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(dummyAuction1));
        when(userRepository.findByObservedAuctionsAndName(any(Auction.class),anyString())).thenReturn(null);
        //when
        userService.addAuctionToObserved("user",1L);
        //then
        assertTrue(dummyUser.getObservedAuctions().contains(dummyAuction1));

    }

    @Test
    void addToObserved_method_should_throw_IllegalArgumentException_when_user_already_observed_auction() {
        //given
        when(userRepository.findByName("user")).thenReturn(dummyUser);
        when(auctionRepository.findById(1L)).thenReturn(Optional.of(dummyAuction1));
        when(userRepository.findByObservedAuctionsAndName(any(Auction.class),anyString())).thenReturn(dummyUser);
        //then
        assertThrows(IllegalStateException.class,()->userService.addAuctionToObserved("user",1L),"Już obserwujęsz wybraną auckję.");
    }

    @Test
    void addToObserved_method_should_throw_INoSuchElementException_when_auction_was_not_found() {
        //given
        when(userRepository.findByName("user")).thenReturn(dummyUser);
        //then
        assertThrows(NoSuchElementException.class,()->userService.addAuctionToObserved("user",1L));
    }

    @Test
    void should_return_UserDetails_object_after_pass_user_name_as_param() {
        //give
        Authorities authorities = new Authorities(dummyUser, new Role("test role"));
        dummyUser.setAuthorities(authorities);
        when(userRepository.findByName("user")).thenReturn(dummyUser);
        //when
        UserDetails user = userService.loadUserByUsername("user");
        //then
        assertEquals(dummyUser.getName(),user.getUsername());
        assertEquals(dummyUser.getAuthorities().getRole().getRole(),user.getAuthorities().stream().findFirst().get().getAuthority());
    }

    @Test
    void should_throw_UserNoFoundException_when_user_is_null(){
        //given
        when(userRepository.findByName("user")).thenReturn(null);
        //then
        assertThrows(UsernameNotFoundException.class,() -> userService.loadUserByUsername("user"),"Użytkownik o podanej nazwie nie isteniej." );

    }


}