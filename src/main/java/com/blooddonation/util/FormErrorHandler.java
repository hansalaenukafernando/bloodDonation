package com.blooddonation.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Form එකකින් එන අගයක් හරියට convert කරගන්න බැරි වුණොත් (උදා: හිස් date එකක්,
 * ඉලක්කම් වෙනුවට අකුරු), Spring එක white-label error page එකක් පෙන්නනවා.
 *
 * මේකෙන් ඒ වගේ අවස්ථාවකදී user ව ආපහු ආපු page එකටම යවලා,
 * අනිත් error ටික වගේම toast message එකක් පෙන්නනවා.
 */
@ControllerAdvice
public class FormErrorHandler {

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public String handleBadFormValue(Exception ex,
                                     HttpServletRequest request,
                                     RedirectAttributes ra) {

        String field = null;
        if (ex instanceof MethodArgumentTypeMismatchException) {
            field = ((MethodArgumentTypeMismatchException) ex).getName();
        } else if (ex instanceof MissingServletRequestParameterException) {
            field = ((MissingServletRequestParameterException) ex).getParameterName();
        }

        ra.addFlashAttribute("errorMessage", toLabel(field) + " is required and must be a valid value");

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null && !referer.isEmpty() ? referer : "/");
    }

    /** "expectedDonors" -> "Expected donors" */
    private String toLabel(String field) {
        if (field == null || field.isEmpty()) return "One of the fields";

        String spaced = field.replaceAll("([a-z])([A-Z])", "$1 $2").toLowerCase();
        return Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
    }
}
