package com.mephiboys.satia.groovy.controller
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.log4j.Logger
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest

@ControllerAdvice
public class ExceptionHandlingController {

    def private static DEFAULT_ERROR_VIEW = "error";

    def private static Logger log = org.apache.log4j.Logger.getLogger(ExceptionHandlingController.class);

    @ExceptionHandler(value = [Throwable.class])
    def ModelAndView defaultErrorHandler(HttpServletRequest request, Exception e) {
        log.error("Internal Server Error", e);

        ModelAndView error = new ModelAndView(DEFAULT_ERROR_VIEW);
        error.addObject('datetime', new Date())
        error.addObject('url', request.getRequestURL())
        error.addObject('stacktrace', ExceptionUtils.getFullStackTrace(e))
        return error;
    }
}