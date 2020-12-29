package com.barterAuctions.portal.controllers;

import com.barterAuctions.portal.config.customExceptions.UnauthorizedAccessException;
import com.barterAuctions.portal.models.messages.Message;
import com.barterAuctions.portal.models.user.User;
import com.barterAuctions.portal.services.AuctionService;
import com.barterAuctions.portal.services.MessageService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MessagesController {

    final MessageService messageService;

    final AuctionService auctionService;

    public MessagesController(MessageService messageService, AuctionService auctionService) {
        this.messageService = messageService;
        this.auctionService = auctionService;
    }

    @PostMapping("/sendMessage")
    public String sendMessage(@ModelAttribute("message") Message message,
                              @RequestParam("auctionId") Long auctionId,
                              @RequestParam("auctionOwner") String auctionOwner,
                              @RequestParam("topic") String topic) throws UnauthorizedAccessException {

        if (auctionService.findById(auctionId).getUser().getName().equals(auctionOwner)) {
            message.setTopic(topic);
            message.setAuctionWhichConcernsId(auctionId);
            message.setRecipient(auctionOwner);
            message.setSender(SecurityContextHolder.getContext().getAuthentication().getName());
            messageService.sendMessage(message);
            return "redirect:/auction/" + auctionId;
        } else {
            throw new UnauthorizedAccessException();
        }
    }

    @PostMapping("/shMsg")
    public String showMessage(@RequestParam("messageId") Long id,
                              @RequestParam("messageText") String msgText,
                              @RequestParam("sender") String sender,
                              @RequestParam("auctionOwner") String auctionOwner,
                              @RequestParam("auctionId") Long auctionId,
                              @RequestParam("messageTopic") String messageTopic,
                              Model model) throws UnauthorizedAccessException {
        if (verifyPrincipals(sender) || verifyPrincipals(auctionOwner)) {
            messageService.setMessageAsRead(id);
            model.addAttribute("messageText", msgText);
            model.addAttribute("messageTopic", messageTopic);
            model.addAttribute("sender", sender);
            model.addAttribute("auctionOwner", auctionOwner);
            model.addAttribute("auctionId", auctionId);
            model.addAttribute("message", new Message());
            return "shwMessage";
        } else{
            throw new UnauthorizedAccessException();
        }
    }

    @PostMapping("/dltRecMsg")
    public String deleteForRecipient(@RequestParam("messageId") Long id) {
        messageService.deleteForRecipient(id);
        return "redirect:/getMessages";
    }

    @PostMapping("/dltSentMsg")
    public String deleteForSender(@RequestParam("messageId") Long id) {
        messageService.deleteForSender(id);
        return "redirect:/getMessages/sent";
    }

    private boolean verifyPrincipals(String userName) {
        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userName.equals(loggedUser)) {
            return true;
        } else {
            return false;
        }
    }


}
