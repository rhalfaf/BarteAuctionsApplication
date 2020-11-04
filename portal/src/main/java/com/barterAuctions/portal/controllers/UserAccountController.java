package com.barterAuctions.portal.controllers;

import com.barterAuctions.portal.models.user.User;
import com.barterAuctions.portal.services.CategoryService;
import com.barterAuctions.portal.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class UserAccountController {

    final UserService userService;
    final CategoryService categoryService;

    public UserAccountController(UserService userService, CategoryService categoryService) {
        this.userService = userService;
        this.categoryService = categoryService;
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


}
