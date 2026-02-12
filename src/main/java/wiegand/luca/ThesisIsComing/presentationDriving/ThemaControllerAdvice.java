package wiegand.luca.ThesisIsComing.presentationDriving;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ThemaNotFoundException;
import wiegand.luca.ThesisIsComing.presentationDriving.exception.ThemaListIsEmpty;

@ControllerAdvice
public class ThemaControllerAdvice {

    @ExceptionHandler(ThemaNotFoundException.class)
    public ModelAndView handleThemaNotFound(ThemaNotFoundException ex) {
        ModelAndView mav = new ModelAndView("errors/404");
        mav.setStatus(HttpStatus.NOT_FOUND);
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(ThemaListIsEmpty.class)
    public ModelAndView handleThemaListIsEmpty(ThemaListIsEmpty ex) {
        ModelAndView mav = new ModelAndView("errors/404");
        mav.setStatus(HttpStatus.NOT_FOUND);
        mav.addObject("message", ex.getMessage());
        return mav;
    }
}
