package wiegand.luca.ThesisIsComing.presentationDriving;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.BetreuerNotFoundException;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.DateiNotFoundException;
import wiegand.luca.ThesisIsComing.presentationDriving.exception.BetreuerListIsEmpty;

@ControllerAdvice
public class BetreuerControllerAdvice {

    @ExceptionHandler(BetreuerNotFoundException.class)
    public ModelAndView handleBetreuerNotFound(BetreuerNotFoundException ex) {
        ModelAndView mav = new ModelAndView("errors/404");
        mav.setStatus(HttpStatus.NOT_FOUND);
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(BetreuerListIsEmpty.class)
    public ModelAndView handleBetreuerListeKannNichtAngezeigtWerden(BetreuerListIsEmpty ex) {
        ModelAndView mav = new ModelAndView("errors/404");
        mav.setStatus(HttpStatus.NOT_FOUND);
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(DateiNotFoundException.class)
    public ModelAndView handleDateiNotFound(DateiNotFoundException ex) {
        ModelAndView mav = new ModelAndView("errors/404");
        mav.setStatus(HttpStatus.NOT_FOUND);
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ModelAndView handleBadRequest(MethodArgumentTypeMismatchException ex) {
        ModelAndView mav = new ModelAndView("errors/400");
        mav.setStatus(HttpStatus.BAD_REQUEST);
        mav.addObject("message", "Ungueltige Anfrage: " + ex.getValue());
        return mav;
    }

}
