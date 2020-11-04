package com.barterAuctions.portal.controllers;

import com.barterAuctions.portal.models.DTO.AuctionDTO;
import com.barterAuctions.portal.models.DTO.UserAuctionDTO;
import com.barterAuctions.portal.models.auction.Auction;
import com.barterAuctions.portal.models.auction.ImageURLs;
import com.barterAuctions.portal.services.AuctionService;
import com.barterAuctions.portal.services.CategoryService;
import com.barterAuctions.portal.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class AuctionController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    AuctionService auctionService;
    @Autowired
    UserService userService;

   /* @GetMapping("/create")
    public void createAuction() {
        Auction a = new Auction();
        a.setLocalization("Warszawa");
        a.setTitle("Nowa aukcja testowa");
        a.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus et elementum quam. Vivamus consectetur in purus a maximus. Integer porttitor, tellus in finibus vestibulum, nunc sapien porta est, in malesuada dui lectus a leo. Aenean nunc justo, mollis ac sollicitudin a, ornare at felis. In hac habitasse platea dictumst. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Suspendisse convallis mollis mi, eu condimentum odio viverra quis. Sed sed sapien vel ipsum tempus condimentum eu vel nulla.\n" +
                "\n" +
                "Nulla lacinia auctor nulla tincidunt ullamcorper. Sed commodo luctus magna at ultricies. Cras at tempus nisi. Cras sollicitudin nisl at nulla congue, id commodo elit iaculis. Mauris tempor leo at mi porta dignissim. Praesent eget metus non dolor lobortis pellentesque. Cras molestie augue ut arcu euismod commodo. Aenean et scelerisque lorem. Suspendisse vitae lobortis justo. Morbi sed pellentesque velit, vel vulputate eros. Proin sagittis in metus eget euismod. Sed vestibulum viverra mollis. Quisque id nulla eu nulla eleifend condimentum. In ac orci semper, fermentum erat nec, tristique purus. Curabitur varius massa in metus vehicula efficitur.\n" +
                "\n" +
                "Etiam eget feugiat erat. Vestibulum eu suscipit neque, id finibus massa. Maecenas consectetur cursus leo, a efficitur lectus porttitor non. Nullam eget venenatis quam, quis vestibulum turpis. Sed varius leo nec erat scelerisque cursus. Quisque ex nullat lacus non purus tristique mattis. Pellentesque fringilla quis risus nec tristique. In hac habitasse platea dictumst. Curabitur id tincidunt tellus. Sed malesuada sed nisi eu tristique. Suspendisse potenti. Maecenas vitae turpis mi. Proin egestas erat felis, id condimentum ligula efficitur quis. Pellentesque fringilla at nisl quis fermentum. Curabitur finibus condimentum orci in vulputate.\n" +
                "\n" +
                "Suspendisse enim libero, molestie at rutrum sit amet, imperdiet vel nunc. Proin sit amet neque a augue tempor elementum. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis velit.");
        a.setImagesURL(new ArrayList(Arrays.asList(new ImageURLs( "https://thumbs.dreamstime.com/b/messy-pile-woman-clothes-15237862.jpg"), new ImageURLs("https://thumbs.dreamstime.com/b/collection-shade-blue-tone-color-t-shirts-hanging-wooden-clothes-hanger-clothing-rack-close-up-closet-over-white-142027645.jpg"))));
        a.getImagesURL().get(1).setMainPhoto(true);
        a.setPrice(new BigDecimal(55.99));
        a.setActive(true);
        a.setStartDate(LocalDate.now());
        a.setExpireDate(a.getStartDate().plusDays(7));
        a.setCategory(categoryService.findByName("Moda"));
        auctionService.save(a);
        userService.findByName("admin").getAuctions().add(a);
    }*/

    @GetMapping("/auction/{id}")
    public String auction(@PathVariable("id") long id, Model model) {
        AuctionDTO auction = auctionService.findById(id);
        model.addAttribute("auction", auction);
        UserAuctionDTO auctionOwner = auctionService.findAuctionOwner(auction);
        model.addAttribute("auctionOwner", auctionOwner);
        return "item-single";
    }

    @GetMapping("/list/{category}")
    String list(@PathVariable String category, Model model) {
        List<AuctionDTO> auctions = auctionService.findAllByCategory(category);
        model.addAttribute("auctions", auctions);
        return "items";
    }

    @GetMapping("/showAuctions/{user}")
    String listByUser(@PathVariable String user, Model model) {
        List<AuctionDTO> auctions = userService.findAllAuctionsOfAUser(user);
        model.addAttribute("auctions", auctions);
        return "items";
    }

    @GetMapping("/addToObserved/{id}")
    public String test(@PathVariable("id") long id, Model model) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        AuctionDTO auction = auctionService.findById(id);
        try {
            userService.addAuctionToObserved(userName, auction);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e);
            return "items";
        }
        return "items";
    }


}
