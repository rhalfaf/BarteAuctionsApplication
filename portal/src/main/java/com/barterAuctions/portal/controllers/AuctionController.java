package com.barterAuctions.portal.controllers;

import com.barterAuctions.portal.models.DTO.AuctionDTO;
import com.barterAuctions.portal.models.auction.Image;
import com.barterAuctions.portal.models.messages.Message;
import com.barterAuctions.portal.models.user.User;
import com.barterAuctions.portal.services.AuctionService;
import com.barterAuctions.portal.services.CategoryService;
import com.barterAuctions.portal.services.ImageService;
import com.barterAuctions.portal.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class AuctionController {
    final long maxFileSize = 5242880;
    final CategoryService categoryService;
    final AuctionService auctionService;
    final UserService userService;
    final ImageService imageService;
    final ModelMapper modelMapper;

    public AuctionController(CategoryService categoryService, AuctionService auctionService, UserService userService, ImageService imageService, ModelMapper modelMapper) {
        this.categoryService = categoryService;
        this.auctionService = auctionService;
        this.userService = userService;
        this.imageService = imageService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/auction/{id}")
    public String getSingleAuction(@PathVariable("id") Long id, Model model) {
        try {
            AuctionDTO auction = auctionService.findById(id);
            model.addAttribute("auction", auction);
            User auctionOwner = auctionService.findAuctionOwner(auction);
            model.addAttribute("auctionOwner", auctionOwner);
            model.addAttribute("message", new Message());
            return "item-single";

        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Nie znaleziono aukcji.");
            return "index";
        }
    }

    @GetMapping("/searchPageable")
    public String searchPhrase(@RequestParam("inParam") String inParam,
                               Model model,
                               @RequestParam("page") Optional<Integer> page,
                               @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        Page<AuctionDTO> auctions = auctionService.searchAuctionsPageable(inParam, PageRequest.of(currentPage - 1, pageSize));
        if (!auctions.isEmpty()) {
            createModel(inParam, model, auctions);
            return "items";
        } else {
            model.addAttribute("error", "Niestety nie znaleziono szukanej frazy.");
            return "index";
        }
    }

    public void createModel(String inParam, Model model, Page<AuctionDTO> auctions) {
        model.addAttribute("auctions", auctions);
        model.addAttribute("inParam", inParam);
        model.addAttribute("totalPages", auctions.getTotalPages());
        if (auctions.getTotalPages() > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, auctions.getTotalPages()).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
    }

    @GetMapping("/list/{inParam}")
    String listAuctionsByCategory(@PathVariable("inParam") String inParam,
                                  Model model,
                                  @RequestParam("page") Optional<Integer> page,
                                  @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        try {
            Page<AuctionDTO> auctions = auctionService.findAllByCategoryPageable(inParam, PageRequest.of(currentPage - 1, pageSize));
            createModel(inParam, model, auctions);
            return "itemsByCategory";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Nie znaleziono kategorii.");
            return "index";
        }
    }

    @GetMapping("/showAuctions/{user}")
    String listAuctionsByUser(@PathVariable String user, Model model) {
        try {
            List<AuctionDTO> auctions = userService.findAllActiveAuctionsOfAUser(user);
            model.addAttribute("auctions", auctions);
            return "items";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "Użytkownik o podanej nazwie nie istniej.");
            return "index";
        }
    }

    @GetMapping("/myItems")
    String listMyItems(Model model) {
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            List<AuctionDTO> auctions = userService.findAllAuctionsOfAUser(userName);
            model.addAttribute("auctions", auctions);
            return "myItems";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            return "index";
        }
    }

    @GetMapping("/addToObserved/{id}")
    public String addAuctionToObserved(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        AuctionDTO auctionDTO = auctionService.findById(id);
        if (userName.equals(auctionService.findAuctionOwner(auctionDTO).getName())) {
            redirectAttributes.addFlashAttribute("error", "Nie możesz obserwować własnej auckji.");
            return "redirect:/auction/" + id;
        }
        try {
            userService.addAuctionToObserved(userName, id);
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auction/" + id;
        }

        return "redirect:/auction/" + id;
    }

    @GetMapping("/getObservedAuctions")
    public String stopObserveAuction(Model model) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("auctions", auctionService.observedAuctions(userName));
        return "observedAuctions";
    }

    @GetMapping("/stopObserve/{id}")
    public String stopObserveAuction(@PathVariable Long id, Model model) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        auctionService.stopObserveAuction(userName, id);
        model.addAttribute("auctions", auctionService.observedAuctions(userName));
        return "observedAuctions";
    }


    @GetMapping("/addNewAuction")
    public String addNewAuction(Model model) {
        model.addAttribute("auction", new AuctionDTO());
        return "addNewAuction";
    }

    @PostMapping("/saveAuction")
    public String saveAuction(@Valid @ModelAttribute("auction") AuctionDTO auction,
                              @RequestParam("categoryName") String category,
                              @RequestParam(value = "auctionImages", required = false) MultipartFile[] images,
                              BindingResult result,
                              Model model) {
        if (images != null && Arrays.stream(images).sequential().anyMatch(file -> (file.getSize() > maxFileSize))) {
            model.addAttribute("error", "Plik ma za duży rozmiar, maksymalny rozmair to " + maxFileSize / 1024 / 1024 + " Mb.");
            return "addNewAuction";
        }
        if (result.hasErrors()) {
            return "addNewAuction";
        }
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        auctionService.addNewAuction(auction, category, userName, images);
        return "redirect:showAuctions/" + userName;
    }

    @PostMapping("/deleteAuction")
    public String deleteAuction(@RequestParam("auctionId") Long auctionId) {
        auctionService.deleteAuction(auctionId);
        return "redirect:/myItems";
    }

    @PostMapping("/re-issue")
    public String reIssueAuction(@RequestParam("auctionId") Long auctionId, Model model) {
        try {
            auctionService.reIssueAuction(auctionId);
        } catch (NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/myItems";
        }
        return "redirect:/myItems";
    }

    @GetMapping("/getImage/{id}")
    @ResponseBody
    void showImage(@PathVariable("id") Long id, HttpServletResponse response, Optional<Image> image) throws IOException {
        if (id != null) {
            try {
                image = Optional.ofNullable(imageService.findById(id));
                response.setContentType("image/jpeg, image/jpg, image/png");
                response.getOutputStream().write(image.get().getImageByte());
                response.getOutputStream().close();
            } catch (NoSuchElementException e) {
                image = Optional.ofNullable(imageService.findById((long) 97));
                response.setContentType("image/jpeg, image/jpg, image/png");
                response.getOutputStream().write(image.get().getImageByte());
                response.getOutputStream().close();

            }
        }
    }

}


