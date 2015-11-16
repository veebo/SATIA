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
        KernelService ks = getKernelService();
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
        KernelService ks = getKernelService();
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

    def newPhrase(String newValue, Lang lang) {
        //...
    }

    def newTask(String[] values, Genrator gen) {
        //...
    }

    def updatePhraseInTask(String newValue, int i, Task t, Test test) {
        if (newValue.equals("")) {
            return;
        }
        if (!newValue.equals(  t.getTranslation()."${"getPhrase"+i}"().getValue()  )) {
            //check if translation is used in other tasks
            Collection<Task> relTasks = ks.getEntitesByQuery(Task.getClass(),
                                        "SELECT * FROM tasks where translation_id=?",
                                        t.getTranslation().getTranslationId());
            //  if yes - create new translation
            if (!relTasks.isEmpty()) {
                Translation newTr = new Translation(t.getTranslation().getPhrase1(),
                                                    t.getTranslation().getPhrase2());
                ks.saveEntity(newTr);
                t.setTranslation(newTr);
                ks.saveEntity(t);
            }
            //check if this phrase is used in other translations
            Phrase phrase = t.getTranslation()."${"getPhrase"+i}"();
            Collection<Object> query_res = ks.getEntityByQuery(Object.getClass(), "SELECT * FROM translations 
                                                                WHERE tr.phrase1_id=? OR
                                                                    tr.phrase2_id=?",
                                                        phrase.getPhraseId(), phrase.getPhraseId());
            //  if not - replace old value
            if (!query_res.isEmpty()) {
                phrase.setValue(newValue);
                ks.saveEntity(phrase);
            }
            //  if yes - create new phrase with value
            else {
                Phrase p = newPhrase(newValue, t.getTranslation()."${"getPhrase"+i}"().getLang(), i);
                t.getTranslation()."${setPhrase+i}"(p);
                ks.saveEntity(t.getTranslation());
            }
        }
    }

    @RequestMapping(value="/edit/{testId}", method=RequestMethod.POST)
    def ModelAndView updateTestPage(@PathVariable String testIdStr, HttpServletRequest request) {
        
        ModelAndView model = testEditingPage(testIdStr);
        Test test = model.getModel().get("test");
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
            newTask(values, gen);
        }
        //modify and delete existing tasks
        for (Task t : test.getTasks()) {
            //update phrases and translation
            for (int i : [1,2]) {
                String newValue = request.getParameter("task"+t.getTaskId()+"_phrase"+i);
                updatePhraseInTask(newValue, i, t, test);
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
                //...
            }
        }
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
