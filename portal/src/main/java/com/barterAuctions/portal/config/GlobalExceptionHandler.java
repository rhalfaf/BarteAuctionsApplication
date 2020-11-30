package com.barterAuctions.portal.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MultipartException.class)
    public String handleError1(MultipartException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Plik ma za duży rozmiar, maksymalny rozmair to 5 Mb.");
        return "redirect:/addNewAuction";

    }


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleError2(MaxUploadSizeExceededException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Plik ma za duży rozmiar, maksymalny rozmair to 5 Mb.");
        return "redirect:/addNewAuction";

    }

    @ExceptionHandler(NumberFormatException.class)
    public String numberFormatException(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Przepraszamy coś poszło nie tak.");
        return "redirect:/";

    }

}
