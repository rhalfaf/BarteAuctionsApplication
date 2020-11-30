package com.barterAuctions.portal.controllers;

import com.barterAuctions.portal.models.messages.Message;
import com.barterAuctions.portal.models.user.User;
import com.barterAuctions.portal.services.CategoryService;
import com.barterAuctions.portal.services.MessageService;
import com.barterAuctions.portal.services.UserService;
import org.springframework.orm.hibernate5.SpringSessionContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;

@EnableScheduling
@Controller
public class UserAccountController {

    final UserService userService;
    final CategoryService categoryService;
    final MessageService messageService;

    public UserAccountController(UserService userService, CategoryService categoryService, MessageService messageService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.messageService = messageService;
    }

    @GetMapping("/login")
    String loginGet() {
        return "login";
    }

    @PostMapping("/login")
    String login() {
        return "login";
    }


    @GetMapping("/register")
    String register(Model model) {
        model.addAttribute("newUser", new User());
        return "register";
    }


    @PostMapping("/register")
    String registered(@Valid @ModelAttribute("newUser") User user,
                      BindingResult result,
                      Model model,
                      @RequestParam("repeatedPassword") String repeatedPassword) {
        if (result.hasErrors()) {
            return "register";
        } else {
            try {
                userService.registerNewUser(user, repeatedPassword);
            } catch (IllegalArgumentException | IllegalStateException e) {
                model.addAttribute("error", e);
                model.addAttribute("newUser", user);
                return "register";
            }
            return "login";
        }
    }


    @GetMapping(value = {"/getMessages", "/getMessages/{sent}"})
    public String getMessages(@PathVariable(required = false) String sent, Model model) {
        Comparator<Message> myComparator = new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o1.getDateTime().compareTo(o2.getDateTime());
            }
        };
        String loggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Message> sentMessages = messageService.getSentMessages(loggedUser);
        List<Message> receiptMessages = messageService.getReceiptedMessages(loggedUser);
        sentMessages.sort(myComparator.reversed());
        receiptMessages.sort(myComparator.reversed());
        if (sent == null) {
            model.addAttribute("messages", receiptMessages);
            model.addAttribute("sent", false);
        } else {
            model.addAttribute("messages", sentMessages);
            model.addAttribute("sent", true);
        }
        return "messages";
    }





}
