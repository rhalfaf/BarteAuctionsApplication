package com.barterAuctions.portal.controllers;

import com.barterAuctions.portal.models.DTO.AuctionDTO;
import com.barterAuctions.portal.models.DTO.UserAuctionDTO;
import com.barterAuctions.portal.services.AuctionService;
import com.barterAuctions.portal.services.CategoryService;
import com.barterAuctions.portal.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class AuctionController {
    final CategoryService categoryService;
    final AuctionService auctionService;
    final UserService userService;

    public AuctionController(CategoryService categoryService, AuctionService auctionService, UserService userService) {
        this.categoryService = categoryService;
        this.auctionService = auctionService;
        this.userService = userService;
    }


    @GetMapping("/auction/{id}")
    public String auction(@PathVariable("id") long id, Model model) {
        try {
            AuctionDTO auction = auctionService.findById(id);
            model.addAttribute("auction", auction);
            UserAuctionDTO auctionOwner = auctionService.findAuctionOwner(auction);
            model.addAttribute("auctionOwner", auctionOwner);
            return "item-single";

        } catch (NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            return "index";
        }
    }

    @GetMapping("/list/{category}")
    String list(@PathVariable String category, Model model) {
        try {
            List<AuctionDTO> auctions = auctionService.findAllByCategory(category);
            model.addAttribute("auctions", auctions);
            return "items";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            return "index";
        }
    }

    @GetMapping("/showAuctions/{user}")
    String listByUser(@PathVariable String user, Model model) {
        try {
            List<AuctionDTO> auctions = userService.findAllAuctionsOfAUser(user);
            model.addAttribute("auctions", auctions);
            return "items";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            return "index";
        }
    }

    @GetMapping("/addToObserved/{id}")
    public String test(@PathVariable("id") long id, Model model) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        AuctionDTO auction = auctionService.findById(id);
        try {
            userService.addAuctionToObserved(userName, auction);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "items";
        }
        return "items";
    }

    @GetMapping("/addNewAuction")
    public String addNewAuction(Model model) {
        model.addAttribute("auction", new AuctionDTO());
        return "addNewAuction";
    }

    @PostMapping("/saveAuction")
    public String saveAuction(@Valid @ModelAttribute("auction") AuctionDTO auction,
                              @RequestParam("categoryName") String category,
                              BindingResult result) {
        if (result.hasErrors()) {
            return "addNewAuction";
        }
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        auctionService.addNewAuction(auction, category, userName);

        return "index";
    }
}