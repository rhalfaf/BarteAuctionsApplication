package com.barterAuctions.portal.services;

import com.barterAuctions.portal.models.DTO.AuctionDTO;
import com.barterAuctions.portal.models.DTO.UserAuctionDTO;
import com.barterAuctions.portal.models.auction.Auction;
import com.barterAuctions.portal.models.auction.Image;
import com.barterAuctions.portal.models.user.User;
import com.barterAuctions.portal.repositories.AuctionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
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
        Auction auction = auctionRepository.findById(id);
        if (auction == null) {
            throw new NoSuchElementException("Wybrana aukcja nie istnieje.");
        }
        return modelMapper.map(auction, AuctionDTO.class);
    }

    public UserAuctionDTO findAuctionOwner(AuctionDTO a) {

        return userService.findByAuctions(a);
    }

    public List<AuctionDTO> findAllByCategory(String category) {
        var tmpCategory = categoryService.findByName(category);
        if (tmpCategory == null) {
            throw new NoSuchElementException("Wybrana kategoria nie istnieje.");
        }
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

    @Transactional
    public AuctionDTO addNewAuction(AuctionDTO auction, String category, String userName, MultipartFile[] images) {
        ArrayList<Image> auctionImages = new ArrayList<>();
        Arrays.stream(images).sequential().forEach(file -> {
            try {
                Image i = new Image(file.getOriginalFilename(), false, file.getContentType(), file.getBytes());
                auctionImages.add(i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        auction.setImages(auctionImages);
        auction.setCategory(categoryService.findByName(category));
        Auction entityAuction = modelMapper.map(auction, Auction.class);
        entityAuction.setStartDate(LocalDate.now());
        entityAuction.setExpireDate(LocalDate.now().plusDays(7));
        entityAuction.setActive(true);
        User user = userService.findByName(userName);
        user.getAuctions().add(entityAuction);
        return modelMapper.map(entityAuction, AuctionDTO.class);
    }
}
