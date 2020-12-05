package com.barterAuctions.portal.services;

import com.barterAuctions.portal.models.DTO.AuctionDTO;
import com.barterAuctions.portal.models.auction.Auction;
import com.barterAuctions.portal.models.auction.Image;
import com.barterAuctions.portal.models.user.User;
import com.barterAuctions.portal.repositories.AuctionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    List<AuctionDTO> randomAuctions = new ArrayList<>();

    @Autowired
    public AuctionService( AuctionRepository auctionRepository, UserService userService, ModelMapper modelMapper, CategoryService categoryService) {
        this.auctionRepository = auctionRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.categoryService = categoryService;
    }

    public AuctionDTO findById(Long id) throws NoSuchElementException {
            Auction auction = auctionRepository.findById(id).orElseThrow();
            if(!auction.isActive()){
                throw new NoSuchElementException();
            }
            return modelMapper.map(auction, com.barterAuctions.portal.models.DTO.AuctionDTO.class);
    }

    public Optional<Auction> tmpFindById(Long id) {
        return auctionRepository.findById(id);
    }

    public Page<AuctionDTO> searchAuctionsPageable(String searchPhrase, Pageable pageable){
        Page<Auction> auctions = auctionRepository.findAllByActiveTrueAndTitleIgnoreCaseContainingOrActiveTrueAndDescriptionIgnoreCaseContaining(searchPhrase,searchPhrase , pageable);
        Page<AuctionDTO> auctionDTOs = auctions.map(AuctionDTO::new);
        return auctionDTOs;
    }

    public User findAuctionOwner(AuctionDTO auction) {
        return userService.findAuctionOwner(auction);
    }

    public Page<AuctionDTO> findAllByCategoryPageable(String category, Pageable pageable) throws NoSuchElementException {
        var tmpCategory = categoryService.findByName(category).orElseThrow();
        Page<Auction> auctions = auctionRepository.findAllByCategoryAndActive(tmpCategory,true, pageable);
        return auctions.map(AuctionDTO::new);
    }

    public List<AuctionDTO> getAuctionsForMainPage(int numberOfElements) {
        List<Long> idsList = auctionRepository.findAuctionsIdOnly();
        Random random = new Random();
        if(!randomAuctions.isEmpty()){
            return randomAuctions;
        }
        if (numberOfElements > idsList.size()) {
            numberOfElements = idsList.size();
            getAuctionsForMainPage(numberOfElements);
        } else {
            for (int i = 0; i < numberOfElements; i++) {
                int randomIndex = random.nextInt(idsList.size());
                Auction tmpAuction = auctionRepository.findById(idsList.get(randomIndex)).orElseThrow();
                AuctionDTO auctionDTO = modelMapper.map(tmpAuction, com.barterAuctions.portal.models.DTO.AuctionDTO.class);
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
        if (images.length > 0 && !images[0].isEmpty()) {
            Arrays.stream(images).sequential().forEach(file -> {
                try {
                    Image i = new Image(file.getOriginalFilename(), false, file.getContentType(), file.getBytes());
                    auctionImages.add(i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            auctionImages.get(0).setMainPhoto(true);
        }
        auction.setCategory(categoryService.findByName(category).orElseThrow());
        Auction entityAuction = modelMapper.map(auction, Auction.class);
        entityAuction.addImages(auctionImages);
        entityAuction.setStartDate(LocalDate.now());
        entityAuction.setExpireDate(LocalDate.now().plusDays(7));
        entityAuction.setActive(true);
        User user = userService.findByName(userName);
        user.addAuction(auctionRepository.save(entityAuction));
        return modelMapper.map(entityAuction, com.barterAuctions.portal.models.DTO.AuctionDTO.class);
    }

    public List<AuctionDTO> observedAuctions(String userName) {
        User user = userService.findByName(userName);
        return user.getObservedAuctions().stream().map(auction -> modelMapper.map(auction, com.barterAuctions.portal.models.DTO.AuctionDTO.class)).collect(Collectors.toList());
    }

    @Transactional
    public List<AuctionDTO> stopObserveAuction(String userName, Long id) throws NoSuchElementException {
        User user = userService.findByName(userName);
        try {
            Auction auction1 = auctionRepository.findById(id).orElseThrow();
            user.getObservedAuctions().remove(auction1);
        } catch (NoSuchElementException e) {
            return user.getObservedAuctions().stream().map(auction -> modelMapper.map(auction, com.barterAuctions.portal.models.DTO.AuctionDTO.class)).collect(Collectors.toList());
        }
        return user.getObservedAuctions().stream().map(auction -> modelMapper.map(auction, com.barterAuctions.portal.models.DTO.AuctionDTO.class)).collect(Collectors.toList());
    }
    @Transactional
    public void deleteAuction(Long auctionId) {
        auctionRepository.findById(auctionId).orElseThrow(()-> new NoSuchElementException("Nie znaleziono auckji.")).setActive(false);
    }
    @Transactional
    public void reIssueAuction(Long auctionId) {
        var auction = auctionRepository.findById(auctionId).orElseThrow(()-> new NoSuchElementException("Nie znaleziono auckji."));
        auction.setActive(true);
        auction.setStartDate(LocalDate.now());
        auction.setExpireDate(auction.getStartDate().plusDays(7));
    }





}
