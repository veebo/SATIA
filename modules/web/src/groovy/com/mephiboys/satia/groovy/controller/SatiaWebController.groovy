package com.mephiboys.satia.groovy.controller
import com.mephiboys.satia.kernel.api.KernelService
import com.mephiboys.satia.kernel.impl.entitiy.*
import com.mephiboys.satia.kernel.mock.MockedKernelService
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
public class SatiaWebController {

    protected KernelService ks = getKernelService()

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
        String userName = auth.getName();
        model.addObject("user_name", userName);
        def tests_results = [:];   
        Collection<Test> myTests = ks.getEntitiesByQuery(Test.getClass(), "SELECT * FROM tests WHERE username=?",userName);
        for (Test t : myTests) {
            Collection<Result> results = ks.getEntitiesByQuery(Result.getClass(),
                "SELECT * FROM results WHERE test_id=?",t.getTestId());
            tests_results["test"] = t;
            tests_results["results"] = results;
        }
        model.addObject("tests_results", tests_results);

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
        try {
            long testId = Long.parseLong(testIdStr,10);
        }
        catch (NumberFormatException nf) {
            throw new ResourceNotFoundException();
        }
        Test test = ks.getEntityById(Test.getClass(), testId);
        if (test == null) {
            throw new ResourceNotFoundException();
        }
        model.addObject("test", test);
        Collection<Generator> generators = ks.getEntitiesByQuery(Generator.getClass(), 
            "SELECT * FROM generators");
        model.addObject("generators", generators);
        
        return model;
    }

    @RequestMapping(value="/edit/{testId}", method=RequestMethod.POST)
    def ModelAndView updateTestPage(@PathVariable String testIdStr, HttpServletRequest request) {
        
        ModelAndView model = testEditingPage(testIdStr);
        Test test = model.getModel().get("test");
        EntityUpdater eu = new EntityUpdater(ks);
        //add new tasks
        int i = 0;
        String[] values;
        while (  ((values=request.getParameterValues("task_add_"+(i++))) != null)  ) {
            if (values.length < 2) {
                continue;
            }
            Generator gen = null;
            if (values.length >= 3) {
                String genId = values[2];
                gen = ks.getEntityByQuery(Generator.getClass(), "SELECT * FROM generators WHERE gen_id=?",
                                                genId);
            }
            if (gen == null) {
                gen = test.getGenerator();
            }
            eu.newTask(values, gen, test);
        }
        //modify and delete existing tasks
        for (Task t : test.getTasks()) {
            //update phrases and translation
            for (int j=1; j<=2; j++) {
                String newValue = request.getParameter("task"+t.getTaskId()+"_phrase"+j);
                eu.updatePhraseInTask(newValue, j, t, test);
            }
            //update generator
            String genId = request.getParameter("task"+t.getTaskid()+"_gen");
            Generator gen = ks.getEntityByQuery(Generator.getClass(), "SELECT * FROM generators WHERE gen_id=?",
                                                genId);
            if ( (gen != null) && (!gen.equals(t.getGenerator())) ) {
                t.setGenerator(gen);
                ks.saveEntity(t);
            }
            //update sourceNum if needed
            if (!t.getTranslation()."${"getPhrase"+t.getSourceNum()}"().getLang().equals(test.getSourceLang())) {
                Translation tr = t.getTranslation();
                tr.setSourceNum((t.getSourceNum() == 1) ? 2 : 1);
                ks.saveEntity(tr);
            }
            //delete
            if (request.getParameter("del_task"+t.getTaskId()) != null) {
                eu.removeTask(t, test);
            }
        }

        return defaultPage();
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
