package wiegand.luca.ThesisIsComing.presentationDriving;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import wiegand.luca.ThesisIsComing.ApplicationService.exception.ZuordnungNichtErlaubtException;

@ControllerAdvice
public class SecurityControllerAdvice {

  @ExceptionHandler(ZuordnungNichtErlaubtException.class)
  public ModelAndView handleZuordnungNichtErlaubt(ZuordnungNichtErlaubtException ex) {
    ModelAndView mav = new ModelAndView("errors/403");
    mav.setStatus(HttpStatus.FORBIDDEN);
    mav.addObject("message", ex.getMessage());
    return mav;
  }
}
