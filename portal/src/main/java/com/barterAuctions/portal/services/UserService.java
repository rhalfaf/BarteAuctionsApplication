package com.barterAuctions.portal.services;

import com.barterAuctions.portal.models.DTO.AuctionDTO;
import com.barterAuctions.portal.models.auction.Auction;
import com.barterAuctions.portal.models.user.Authorities;
import com.barterAuctions.portal.models.user.User;
import com.barterAuctions.portal.repositories.AuctionRepository;
import com.barterAuctions.portal.repositories.RoleRepository;
import com.barterAuctions.portal.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    final BCryptPasswordEncoder bCryptPasswordEncoder;
    final UserRepository userRepository;
    final RoleRepository roleRepository;
    final AuctionRepository auctionRepository;
    final ModelMapper modelMapper;

    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository, RoleRepository roleRepository, AuctionRepository auctionRepository, ModelMapper modelMapper) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.auctionRepository = auctionRepository;
        this.modelMapper = modelMapper;
    }

    public User findByName(String name) {
        return userRepository.findByName(name);
    }


    public User registerNewUser(User user, String confirmPassword) {
        validPasswordsAreIdentical(user.getPassword(), confirmPassword);
        validIsUnique(user);
        user.setEnabled(true);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setAuthorities(new Authorities(user, roleRepository.findByRole("ROLE_USER")));
        return userRepository.saveAndFlush(user);
    }

    private void validPasswordsAreIdentical(String password, String repeatedPassword) {
        if (!password.equals(repeatedPassword)) {
            throw new IllegalStateException("Hasła muszą być identyczne");
        }
    }

    private void validIsUnique(User user) throws IllegalArgumentException {
        if (userRepository.existsByName(user.getName())) {
            throw new IllegalArgumentException("Użytkownik o podanej nazwie już istnieje.");
        } else if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Podany email jest już zarejstrowany.");
        }
    }


    public User findAuctionOwner(AuctionDTO auction) {
        Auction auctionEntity = modelMapper.map(auction, Auction.class);
        return userRepository.findByAuctions(auctionEntity);
    }

    public List<AuctionDTO> findAllActiveAuctionsOfAUser(String username) {
        User user = userRepository.findByName(username);
        if (user == null) {
            throw new NoSuchElementException("Użytkownik o podanej nazwie nie istniej.");
        }
        return user.getAuctions().stream().filter(Auction::isActive).map(auction -> modelMapper.map(auction, com.barterAuctions.portal.models.DTO.AuctionDTO.class)).collect(Collectors.toList());
    }

    public List<AuctionDTO> findAllAuctionsOfAUser(String username) {
        User user = userRepository.findByName(username);
        if (user == null) {
            throw new NoSuchElementException("Użytkownik o podanej nazwie nie istniej.");
        }
        return user.getAuctions().stream().map(auction -> modelMapper.map(auction, com.barterAuctions.portal.models.DTO.AuctionDTO.class)).collect(Collectors.toList());
    }

    @Transactional
    public void addAuctionToObserved(String userName, Long auctionId) throws IllegalStateException {
        User user = userRepository.findByName(userName);
        Auction auction = auctionRepository.findById(auctionId).orElseThrow();
        if (userRepository.findByObservedAuctionsAndName(auction, user.getName()) != null) {
            throw new IllegalStateException("Już obserwujęsz wybraną auckję.");
        } else {
            user.getObservedAuctions().add(auction);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByName(username);
        if (user == null) {
            throw new UsernameNotFoundException("Użytkownik o podanej nazwie nie isteniej.");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getName(), user.getPassword(), user.isEnabled(), true, true,
                true, new ArrayList<>(List.of(user.getAuthorities())));
    }
}
