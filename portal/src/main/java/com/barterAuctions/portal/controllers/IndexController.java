package com.barterAuctions.portal.controllers;

import com.barterAuctions.portal.services.AuctionService;
import com.barterAuctions.portal.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@ControllerAdvice
public class IndexController {

    final CategoryService categoryService;

    final AuctionService auctionService;

    public IndexController(CategoryService categoryService, AuctionService auctionService) {
        this.categoryService = categoryService;
        this.auctionService = auctionService;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("thumbnails", auctionService.getAuctionsForMainPage(3));
        model.addAttribute("categories", categoryService.findAll());
    }

    @GetMapping("/")
    String index() {
        return "index";
    }

    @PostMapping("/")
    String indexPost() {
        return "index";
    }


}
