package com.barterAuctions.portal.services;

import com.barterAuctions.portal.models.DTO.AuctionDTO;
import com.barterAuctions.portal.models.DTO.UserAuctionDTO;
import com.barterAuctions.portal.models.auction.Auction;
import com.barterAuctions.portal.models.user.Authorities;
import com.barterAuctions.portal.models.user.User;
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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    final BCryptPasswordEncoder bCryptPasswordEncoder;
    final UserRepository userRepository;
    final RoleRepository roleRepository;
    final ModelMapper modelMapper;

    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository, RoleRepository roleRepository, ModelMapper modelMapper) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    public User findByName(String name) {
        return userRepository.findByName(name);
    }


    public void registerNewUser(User user, String confirmPassword) {
        validPasswordsAreIdentical(user.getPassword(), confirmPassword);
        validIsUnique(user);
        user.setEnabled(true);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setAuthorities(new Authorities(user, roleRepository.findByRole("ROLE_USER")));
        userRepository.saveAndFlush(user);
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


    public UserAuctionDTO findByAuctions(AuctionDTO auctionDTO) {
        Auction a = modelMapper.map(auctionDTO, Auction.class);
        User user = userRepository.findByAuctions(a);
        UserAuctionDTO userAuctionDTO = modelMapper.map(user, UserAuctionDTO.class);
        return userAuctionDTO;
    }

    public List<AuctionDTO> findAllAuctionsOfAUser(String username){
        User user = userRepository.findByName(username);
        return user.getAuctions().stream().map(auction -> modelMapper.map(auction, AuctionDTO.class)).collect(Collectors.toList());
    }

    @Transactional
    public void addAuctionToObserved(String userName, AuctionDTO auction) {
        User user = userRepository.findByName(userName);
        Auction mappedAuction = modelMapper.map(auction, Auction.class);
        if (userRepository.findByObservedAuctionsAndName(mappedAuction, user.getName()) != null) {
            throw new IllegalStateException("Już obserwujęsz wybraną auckję.");
        } else {
            user.getObservedAuctions().add(mappedAuction);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByName(username);
        if (user == null) {
            throw new UsernameNotFoundException("Użytkownik o podanej nazwie nie isteniej");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), user.isEnabled(), true, true,
                true, (Collection<? extends GrantedAuthority>) user.getAuthorities());
    }
}
