package com.barterAuctions.portal.services;

import com.barterAuctions.portal.models.DTO.AuctionDTO;
import com.barterAuctions.portal.models.DTO.UserAuctionDTO;
import com.barterAuctions.portal.models.auction.Auction;
import com.barterAuctions.portal.models.auction.Category;
import com.barterAuctions.portal.models.user.User;
import com.barterAuctions.portal.repositories.AuctionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AuctionService {

    final CategoryService categoryService;

    final AuctionRepository auctionRepository;

    final UserService userService;

    final ModelMapper modelMapper;

    public AuctionService(AuctionRepository auctionRepository, UserService userService, ModelMapper modelMapper, CategoryService categoryService) {
        this.auctionRepository = auctionRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.categoryService = categoryService;
    }


    public AuctionDTO findById(long id) {
        return modelMapper.map(auctionRepository.findById(id), AuctionDTO.class);
    }

    public UserAuctionDTO findAuctionOwner(AuctionDTO a) {
        return userService.findByAuctions(a);
    }

    public List<AuctionDTO> findAllByCategory(String category) {
        var tmpCategory = categoryService.findByName(category);
        return auctionRepository.findAllByCategory(tmpCategory)
                .stream()
                .map(auction -> modelMapper.map(auction, AuctionDTO.class))
                .collect(Collectors.toList());
    }

    public List<AuctionDTO> getAuctionsForMainPage(int numberOfElements) {
        List<AuctionDTO> randomAuctions = new ArrayList<>();
        List<Long> idsList = auctionRepository.findAuctionsIdOnly();
        Random random = new Random();
        if (numberOfElements > idsList.size()) {
            numberOfElements = idsList.size();
            getAuctionsForMainPage(numberOfElements);
        } else {
            for (int i = 0; i < numberOfElements; i++) {
                int randomIndex = random.nextInt(idsList.size());
                Auction tmpAuction = auctionRepository.findById(idsList.get(randomIndex)).orElseThrow();
                AuctionDTO auctionDTO = modelMapper.map(tmpAuction, AuctionDTO.class);
                randomAuctions.add(auctionDTO);
                idsList.remove(randomIndex);
            }
        }
        return randomAuctions;
    }

    public Auction save(Auction a) {
        return auctionRepository.saveAndFlush(a);
    }


}
