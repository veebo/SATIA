package com.mephiboys.satia.groovy.controller

import com.mephiboys.satia.kernel.mock.MockedKernelService
import com.mephiboys.satia.kernel.api.KernelService
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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.ModelAndView
import org.springframework.http.HttpStatus

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import java.util.Collection;
import com.mephiboys.satia.kernel.impl.entitiy.*

@Controller
public class SatiaWebController {

    protected KernelService kernelService = getKernelService()

    KernelService getKernelService() {
        return new MockedKernelService();
    };

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public final class ResourceNotFoundException extends RuntimeException {}

    @RequestMapping(value = [ "/", "/welcome**" ], method = RequestMethod.GET)
    def ModelAndView defaultPage() {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return accesssDenied();
        }

        ModelAndView model = new ModelAndView();
        model.setViewName("home");
        MockedKernelService ks = (MockedKernelService)getKernelService();
        String userName = auth.getName();
        model.addObject("user_name", userName);
        def myTests = [];
        Collection<Test> allTests = ks.getAllTests();
        for (Test t : allTests) {
            if (t.getUser().getUsername().equals(userName)) {
                Collection<Result> results = ks.getResultsByTest(t);
                myTests << ["test" : t, "results" : results];
                break;
            }
        }
        model.addObject("my_tests", myTests);
        model.addObject("all_tests", allTests);

        return model;
    }

    @RequestMapping(value="/edit/{testId}", method=RequestMethod.GET)
    def ModelAndView testEditingPage(@PathVariable String testIdStr) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return accesssDenied();
        }

        ModelAndView model = new ModelAndView();
        model.setViewName("test_edit");
        MockedKernelService ks = (MockedKernelService)getKernelService();
        try {
            long testId = Long.parseLong(testIdStr,10);
        }
        catch (NumberFormatException nf) {
            throw new ResourceNotFoundException();
        }
        Test test = ks.getTestById(testId);
        if (test == null) {
            throw new ResourceNotFoundException();
        }
        model.addObject("test", test);
        model.addObject("generators", ks.getAllGenerators());
        
        return model;
    }

    /*@RequestMapping(value="/edit/{testId}", method=RequestMethod.POST)
    def ModelAndView updateTestPage(@PathVariable String testIdStr, HttpServletRequest request) {
        
        ModelAndView model = testEditingPage(testIdStr);
        Test test = model.getModel().get("test");
        Phrase[] phrases = new Phrase[2];
        //modify existing phrases and tasks
        for (Task t : test.getTasks()) {
            boolean modified = false;
            for (int i=0; i<2; i++) {
                String newValue = request.getParameter("task_"+t.getTaskId()+"_phrase"+i);
                if (!it.equals(  t.getTranslation()."${getPhrase+i}"()  )) {
                    modified = true;
                    //check if this phrase is used in other tasks
                    //  if not - replace value to the new value
                    //  else - create new phrase with value
                    //save new Phrase instances in 'phrases' array
                }
            }
            if (modified) {
                //modify translation of current task
            }
        }
        //add new tasks
        //  ...
    }*/

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
