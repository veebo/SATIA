package com.mephiboys.satia.groovy.controller

import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import java.util.List;
import java.com.mephiboys.satia.kernel.KernelService;
import java.com.mephiboys.satia.kernel.impl.entitiy.*;

@Controller
public class SatiaWebController {

    @RequestMapping(value = [ "/", "/welcome**" ], method = RequestMethod.GET)
    def ModelAndView defaultPage() {
        
        ModelAndView model = new ModelAndView();
        model.setViewName("home");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = KernelService.getUserByName(auth.getName());
        model.addObject("user_name", user?.getName());
        if (user == null) {
            return model;
        }
        // CREATED TESTS
        def tests = [];
        for (Test t : user.getTests()) {
            tests << ["title" : t.getTitle(), "descr" : t.getDescription()];
        }
        model.addObject("created_tests", tests);
        //AVAILABLE TESTS AND RESULTS
        def testResults = [:];
        for (Test t : KernelService.getAllTests()) {
            def results = [];
            for (Result r : user.getResults()) {
                if (r.getTest().equals(t)) {
                    results << ["grade" : r.getValue(), "time" : r.getStart_time()];
                    break;
                }
            }
            testResults[t.getTitle()] = results;
        }
        model.addObject("available_tests", testResults);

        return model;
    }

    @RequestMapping(value = "/admin**", method = RequestMethod.GET)
    def ModelAndView adminPage() {

        ModelAndView model = new ModelAndView();
        model.addObject("title", "Spring Security Password Encoder");
        model.addObject("message", "This page is for ROLE_ADMIN only!");
        model.setViewName("admin");

        return model;

    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    def ModelAndView login(@RequestParam(value = "error", required = false) String error) {

        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
        ModelAndView model = new ModelAndView();
        model.addObject("vis", "visible");
        if (!(auth instanceof AnonymousAuthenticationToken)){
            model.addObject("vis", "hidden");
            model.addObject("msg", "You're already logged in.")
        } else if (error != null) {
            model.addObject("error", "Invalid username and password!");
        }

        model.setViewName("login");

        return model;

    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    def logout(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        def auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
            model.addObject("msg", "You've been logged out successfully.");
        }
        return "redirect:/login";
    }

    @RequestMapping(value = "/403", method = RequestMethod.GET)
    def ModelAndView accesssDenied() {
        ModelAndView model = new ModelAndView();
        //check if user is login
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetail = (UserDetails) auth.getPrincipal();
            model.addObject("username", userDetail.getUsername());
        }

        model.setViewName("403");
        return model;

    }

}
