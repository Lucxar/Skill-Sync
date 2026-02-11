package wiegand.luca.ThesisIsComing.presentationDriving;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerNotFoundException;

@ControllerAdvice
public class BetreuerControllerAdvice {

    @ExceptionHandler(BetreuerNotFoundException.class)
    public ModelAndView handleBetreuerNotFound(BetreuerNotFoundException ex) {
        ModelAndView mav = new ModelAndView("errors/404");
        mav.setStatus(HttpStatus.NOT_FOUND);
        mav.addObject("message", ex.getMessage());
        return mav;
    }
}
