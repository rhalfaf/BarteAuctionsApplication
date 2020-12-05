package com.barterAuctions.portal.controllers;

import com.barterAuctions.portal.models.DTO.AuctionDTO;
import com.barterAuctions.portal.models.messages.Message;
import com.barterAuctions.portal.models.user.User;
import com.barterAuctions.portal.services.AuctionService;
import com.barterAuctions.portal.services.CategoryService;
import com.barterAuctions.portal.services.MessageService;
import com.barterAuctions.portal.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@ControllerAdvice
public class IndexController
{

    final CategoryService categoryService;

    final AuctionService auctionService;

    final UserService userService;

    final MessageService messageService;

    List<AuctionDTO> auctionsForMainPage;

    public IndexController(CategoryService categoryService, AuctionService auctionService, UserService userService, MessageService messageService) {
        this.categoryService = categoryService;
        this.auctionService = auctionService;
        this.userService = userService;
        this.messageService = messageService;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        auctionsForMainPage = auctionService.getAuctionsForMainPage(6);
        model.addAttribute("thumbnails", auctionsForMainPage);
        model.addAttribute("categories", categoryService.findAll().stream().map(category -> category.getCategoryName()).collect(Collectors.toList()));
    }
    @PreAuthorize("isAuthenticated()")
    @ModelAttribute
    public boolean areSomeMessagesUnread(Model model){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByName(userName);
        List<Message> userReceiptedMessages = messageService.getReceiptedMessages(userName);
        if(userReceiptedMessages.stream().anyMatch(message -> !(message.isRead()) && message.isShowRecipient())){
            long numberOfUnreadMessage = userReceiptedMessages.stream().filter(message -> !(message.isRead()) && message.isShowRecipient()).count();
            model.addAttribute("unreadMessages", numberOfUnreadMessage);
            return true;
        }
        return false;
    }

    @GetMapping("/")
    String index() {
        return "index";
    }

    @PostMapping("/")
    String indexPost() {
        return "index";
    }

    @GetMapping("/gallery")
    public String gallery(){
        return "gallery";
    }


}
