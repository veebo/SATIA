package com.mephiboys.satia.groovy.controller
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
public class SatiaWebController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    def index(ModelMap model, HttpServletRequest request) {
        model['title'] = 'THE PAGE'
        model['message'] = 'WAS GROOVED'
        return "index"
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    def home(ModelMap model, HttpServletRequest request) {
        return "home"
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    def logout(HttpServletRequest request, HttpServletResponse response) {
        def auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/";
    }

}
