package com.barterAuctions.portal.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NumberFormatException.class)
    public String numberFormatException(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Przepraszamy coś poszło nie tak.");
        return "redirect:/";

    }

}
