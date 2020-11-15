package com.barterAuctions.portal.controllers;

import com.barterAuctions.portal.models.DTO.AuctionDTO;
import com.barterAuctions.portal.models.DTO.UserAuctionDTO;
import com.barterAuctions.portal.models.auction.Image;
import com.barterAuctions.portal.services.AuctionService;
import com.barterAuctions.portal.services.CategoryService;
import com.barterAuctions.portal.services.ImageService;
import com.barterAuctions.portal.services.UserService;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
public class AuctionController {
    final long maxFileSize = 5242880;
    final CategoryService categoryService;
    final AuctionService auctionService;
    final UserService userService;
    final ImageService imageService;

    public AuctionController(CategoryService categoryService, AuctionService auctionService, UserService userService, ImageService imageService) {
        this.categoryService = categoryService;
        this.auctionService = auctionService;
        this.userService = userService;
        this.imageService = imageService;
    }


    @GetMapping("/auction/{id}")
    public String auction(@PathVariable("id") long id, Model model, HttpServletResponse response) {
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
                              @RequestParam("auctionImages") MultipartFile[] images,
                              BindingResult result,
                              Model model) {

            if (Arrays.stream(images).sequential().anyMatch(file -> (file.getSize() > maxFileSize))) {
                model.addAttribute("error", "Plik ma za duży rozmiar, maksymalny rozmair to " +maxFileSize/1024/1024 +" Mb.");
                return "addNewAuction";
            }
            if (result.hasErrors()) {
                return "addNewAuction";
            }
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            auctionService.addNewAuction(auction, category, userName, images);

        return "redirect: /showAuctions/" + userName;
    }


    @GetMapping("/getImage/{id}")
    @ResponseBody
    void showImage(@PathVariable("id") Long id, HttpServletResponse response, Optional<Image> image)
            throws ServletException, IOException {
        image = Optional.ofNullable(imageService.findById(id));
        response.setContentType("image/jpeg, image/jpg, image/png");
        response.getOutputStream().write(image.get().getImageByte());
        response.getOutputStream().close();
    }


}


