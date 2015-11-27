package com.mephiboys.satia.groovy.controller
import com.mephiboys.satia.kernel.api.KernelService
import com.mephiboys.satia.kernel.impl.entitiy.*
import com.mephiboys.satia.kernel.mock.MockedKernelService
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
public class SatiaWebController {

    protected KernelService ks = getKernelService()

    KernelService getKernelService() {
        return new MockedKernelService();
    };

    @RequestMapping(value = [ "/" ], method = RequestMethod.GET)
    def ModelAndView defaultPage() {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        ModelAndView model = new ModelAndView();
        model.setViewName("home");
        String userName = auth.getName();
        model.addObject("user_name", userName);
        def tests_results = [];   
        Collection<Test> myTests = ks.getEntitiesByQuery(Test.class,
            "SELECT test_id FROM tests WHERE username=?",userName);
        for (Test t : myTests) {
            if (t == null) {
                continue;
            }
            Collection<Result> results = ks.getEntitiesByQuery(Result.class,
                "SELECT username,test_id,start_time,session_key FROM results WHERE test_id=?",t.getTestId());
            def tr = [:];
            tr["test"] = t;
            tr["results"] = results;
            tests_results << tr;
        }
        model.addObject("tests_results", tests_results);

        return model;
    }

    @RequestMapping(value="/edit/{testIdStr}", method=RequestMethod.GET)
    def ModelAndView testEditingPage(@PathVariable String testIdStr) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authUserName = auth.getName();
        User user = ks.getEntityById(User.class, authUserName);

        ModelAndView model = new ModelAndView();
        model.setViewName("test_edit");
        Collection<Generator> generators = ks.getEntitiesByQuery(Generator.class, 
            "SELECT gen_id FROM generators");
        model.addObject("generators", generators);
        Collection<Lang> langs = ks.getEntitiesByQuery(Lang.class, "SELECT lang FROM langs");
        model.addObject("langs", langs);

        //for creating new test
        if (testIdStr.equals("create")) {
            Test newTest = new Test();
            newTest.setUser(user);
            newTest.setTasks(new ArrayList<Task>());
            model.addObject("test", newTest);
            model.addObject("create", true);
            return model;
        }
        //for editing existing test
        Long testId;
        try {
            testId = new Long(Long.parseLong(testIdStr,10));
        }
        catch (NumberFormatException nf) {
            return notFound();
        }
        Test test = ks.getEntityById(Test.class, testId);
        if (test == null) {
            return notFound();
        }
        /*if (!(test.getUser().equals(user))) {
            return accessDenied();
        }*/
        model.addObject("test", test);
        model.addObject("create", false);
        return model;
    }

    @RequestMapping(value="/edit/{testIdStr}", method=RequestMethod.POST)
    def ModelAndView updateTestPage(@PathVariable("testIdStr") String testIdStr, HttpServletRequest request) {

        ModelAndView model = testEditingPage(testIdStr);
        // if not found or denied
        if (!model.getViewName().equals("test_edit")) {
            return model;
        }

        Test test = model.getModel().get("test");
        EntityUpdater eu = new EntityUpdater(ks);

        //validate and modify test fields if needed and save tet entity
        try {
            eu.updateTestFields(test, ["Title" : request.getParameter("test_title"),
                                       "Description" : request.getParameter("test_description"),
                                       "Generator" : request.getParameter("test_generator"),
                                       "SourceLang" : request.getParameter("test_sourcelang"),
                                       "TargetLang" : request.getParameter("test_targetlang")]);
        } catch (IllegalArgumentException ia) {
            return badRequest(ia.getMessage());
        }

        //add new tasks
        int addedTasks;
        try {
            addedTasks = Integer.parseInt(request.getParameter("added_tasks_num"));
        } catch (NumberFormatException nf) {
            addedTasks = 0;
        }
        String[] values = new String[2];
        for (int i=0; i<addedTasks; i++) {
            values[0] = request.getParameter("add_task"+i+"_phrase1");
            values[1] = request.getParameter("add_task"+i+"_phrase2");
            Generator newTaskGen = null;
            try {
                Long genId = Long.parseLong(request.getParameter("add_task"+i+"_gen"));
                newTaskGen = ks.getEntityById(Generator.class, genId);
                if (newTaskGen == null) {
                    newTaskGen = test.getGenerator();
                }
            }
            catch (NumberFormatException nf) {
                newTaskGen = test.getGenerator();
            }
            try {
                eu.newTask(values, newTaskGen, test);
            } catch (IllegalArgumentException ia) {
                continue;
            }
        }

        //modify and delete existing tasks
        for (Task t : test.getTasks()) {
            //delete if needed
            if (request.getParameter("del_task"+t.getTaskId()) != null) {
                eu.removeTask(t, test);
                continue;
            }
            //update phrases and translation
            for (int j=1; j<=2; j++) {
                String newValue = request.getParameter("task"+t.getTaskId()+"_phrase"+j);
                try {
                    eu.updatePhraseInTask(newValue, j, t, test);
                } catch (IllegalArgumentException ia) {
                    continue;
                }
            }
            //update generator
            String genId = request.getParameter("task"+t.getTaskId()+"_gen");
            Generator taskGen = ks.getEntityById(Generator.class, genId);
            if ( (taskGen != null) && (!taskGen.equals(t.getGenerator())) ) {
                t.setGenerator(taskGen);
                ks.saveEntity(t);
            }
            //update sourceNum if needed
            if (!t.getTranslation()."${"getPhrase"+t.getSourceNum()}"().getLang().equals(test.getSourceLang())) {
                t.setSourceNum((t.getSourceNum() == (byte)1) ? (byte)2 : (byte)1);
                ks.saveEntity(t);
            }
        }

        //save test
        ks.saveEntity(test);

        return model;
    }

    @RequestMapping(value="/404")
    def ModelAndView notFound() {
        ModelAndView model = new ModelAndView();
        model.setViewName("404");
        return model;
    }

    @RequestMapping(value="/400")
    def ModelAndView badRequest(String msg) {
        ModelAndView model = new ModelAndView();
        model.addObject("msg", msg);
        model.setViewName("400");
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
            model.addAttribute("msg", "You've been logged out successfully.");
        }
        return "redirect:/login";
    }

    @RequestMapping(value = "/403", method = RequestMethod.GET)
    def ModelAndView accessDenied() {
        ModelAndView model = new ModelAndView();
        //check if user is login
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
//            UserDetails userDetail = (UserDetails) auth.getPrincipal();
//            model.addObject("username", userDetail.getUsername());
            model.setViewName("403");
        } else {
            model.setViewName("redirect:/login")
        }


        return model;

    }

}
