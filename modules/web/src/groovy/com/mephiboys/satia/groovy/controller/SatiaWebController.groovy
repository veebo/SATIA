package com.mephiboys.satia.groovy.controller

import com.mephiboys.satia.kernel.api.KernelHelper
import com.mephiboys.satia.kernel.api.KernelService
import com.mephiboys.satia.kernel.impl.entitiy.*
import org.apache.commons.lang.StringUtils
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@Controller
public class SatiaWebController {

    protected KernelService ks = KernelHelper.getKernelService();

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

    def getTestModel(User user, String testIdStr) {

        ModelAndView model = new ModelAndView();
        
        model.setViewName("test_edit");
        Collection<Generator> generators = ks.getEntitiesByQuery(Generator.class, 
            "SELECT gen_id FROM generators");
        model.addObject("generators", generators);
        Collection<Lang> langs = ks.getEntitiesByQuery(Lang.class, "SELECT lang FROM langs");
        model.addObject("langs", langs);

        //for creating new test
        if ("create".equals(testIdStr)) {
            Test newTest = new Test(createdWhen : new Date().toTimestamp(),
                user : user, tasks : new ArrayList<Task>());
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
        if ( (user == null) || (!user.getUsername().equals(test.getUser().getUsername())) ) {
            return accessDenied();
        }

        model.addObject("test", test);
        model.addObject("create", false);

        return model;
    }

    @RequestMapping(value="/edit/{testIdStr}", method=RequestMethod.GET)
    def ModelAndView testEditingPage(@PathVariable String testIdStr) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authUserName = auth.getName();
        User user = ks.getEntityById(User.class, authUserName);
        ModelAndView model = getTestModel(user, testIdStr);
        return model;
    }

    @RequestMapping(value="/edit/{testIdStr}", method=RequestMethod.POST)
    def ModelAndView updateTestPage(@PathVariable("testIdStr") String testIdStr, HttpServletRequest request) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authUserName = auth.getName();
        User user = ks.getEntityById(User.class, authUserName);
        ModelAndView model = getTestModel(user, testIdStr);

        // if not found or denied
        if (!"test_edit".equals(model.getViewName())) {
            return model;
        }

        Test test = model.getModel().get("test");
        boolean createTest = model.getModel().get("create");

        //validate and modify test fields if needed and save tet entity
        try {
            ks.updateTest(test, createTest, ["Title" : request.getParameter("test_title"),
                                       "Description" : request.getParameter("test_description"),
                                       "Generator" : request.getParameter("test_generator"),
                                       "SourceLang" : request.getParameter("test_sourcelang"),
                                       "TargetLang" : request.getParameter("test_targetlang")]);
        } catch (Exception ia) {
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

        def tasksToAdd = []

        for (int i=0; i<addedTasks; i++) {
            //extract request parameters
            values[0] = request.getParameter("add_task"+i+"_phrase1");
            values[1] = request.getParameter("add_task"+i+"_phrase2");
            Generator newTaskGen = null;
            try {
                Long genId = Long.parseLong(request.getParameter("add_task"+i+"_gen"));
                newTaskGen = ks.getEntityById(Generator.class, genId);
            }
            catch (NumberFormatException ignored) {}
            //save
            tasksToAdd << ks.newTask(values, newTaskGen, test);
            //ks.updateTaskFieldValues(createdTask, request, "add_task"+i);
        }

        ks.createTasks(test, tasksToAdd)


        //modify and delete existing tasks
        def tasksToRemove = [];
        for (Task t : test.getTasks()) {
            //delete if needed
            if (request.getParameter("del_task"+t.getTaskId()) != null) {
                tasksToRemove << t;
                continue;
            }
            //extract request parameters
            String[] phraseValues = new String[2];
            phraseValues[0] = request.getParameter("task"+t.getTaskId()+"_phrase1");
            phraseValues[1] = request.getParameter("task"+t.getTaskId()+"_phrase2");
            Long genId = null;
            try {
                String genIdParam = request.getParameter("task"+t.getTaskId()+"_gen");
                if (genIdParam.equals("null")) {
                    genId = new Long(-1);
                } else {
                    genId = Long.parseLong(genIdParam);
                }
            }
            catch (NumberFormatException ignored) {}

            //save
            ks.updateTask(test, t, phraseValues, genId);
            //ks.updateTaskFieldValues(t, request, "task"+t.getTaskId());
        }
        ks.removeTask(tasksToRemove, test);

        model.getModel().put("create", false);
        return model;
    }

    @RequestMapping(value="start_test/{testIdStr}", method=RequestMethod.GET)
    def ModelAndView startTest(@PathVariable("testIdStr") String testIdStr, HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        model.setViewName("start_test");
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
        try {
            HttpSession session = request.getSession();
            session.setAttribute("test", test);
            session.setAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
            session.setAttribute("next", new Integer(0));
            session.setAttribute("right_answers", new Integer(0));
        }
        catch (IllegalStateException ilgState) {
            return badRequest("invalidated session");
        }
        model.addObject("test", test);
        return model;
    }

    @RequestMapping(value="/task", method=RequestMethod.POST)
    def ModelAndView passTask(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        model.setViewName("task");
        int cur;
        HttpSession session;
        Test test;
        String username;
        String fullname;
        try {
            //extract session attributes
            session = request.getSession();


            test = session.getAttribute("test");
            username = session.getAttribute("username");
            fullname = session.getAttribute("fullname");
            String intNext = session.getAttribute("next");
            String intRigthAnswers = session.getAttribute("right_answers");
            if ((StringUtils.isEmpty(intNext)) || (StringUtils.isEmpty(intRigthAnswers)) || (test == null)) {
                return badRequest("invalidated session");
            }
            int next = Integer.parseInt(intNext)
            int rightAnswers = Integer.parseInt(intRigthAnswers)
            cur = next - 1;
            //check answer
            if ( (cur >= 0) && (cur < test.getTasks().size()) ) {
                Task curTask = test.getTasks().get(cur);
                long answer;
                try {
                    answer = Long.parseLong(request.getParameter("answer"));
                }
                catch (NumberFormatException nf) {
                    return badRequest("invalid request parameters");
                }
                Phrase rightPhrase = ( (curTask.getSourceNum() == 1) ?
                    curTask.getTranslation().getPhrase2() : curTask.getTranslation().getPhrase1() );
                if (answer == rightPhrase.getPhraseId()) {
                    ++rightAnswers;
                    session.setAttribute("right_answers", rightAnswers);
                }
            } else if (cur == -1) {
                String fullnameFromReq = request.getParameter("name");
                fullnameFromReq = (fullnameFromReq == null) ? "" : fullnameFromReq;
                session.setAttribute("fullname", fullnameFromReq);
            } else {
                return badRequest("invalid request parameters");
            }
            //define next task and generate answers
            try {
                Task nextTask = test.getTasks().get(next);
                ++next;
                session.setAttribute("next", new Integer(next));
                byte src = nextTask.getSourceNum();
                byte dst = (src == 1) ? 2 : 1;
                model.addObject("question", nextTask.getTranslation()."${"getPhrase"+src}"().getValue());
                //============TEST==============================
                def answers = [];
                Phrase answer = nextTask.getTranslation()."${"getPhrase"+dst}"();
                answers << ["id" : answer.getPhraseId(), "value" : answer.getValue()] <<
                           ["id" : answer.getPhraseId()+1, "value" : "aaa"] <<
                           ["id" : answer.getPhraseId()+2, "value" : "bbb"] <<
                           ["id" : answer.getPhraseId()+3, "value" : "ccc"] <<
                           ["id" : answer.getPhraseId()+4, "value" : "ddd"];
                model.addObject("answers", answers);
                //==========================================
                model.addObject("end", false);
            }
            //if no tasks left - save result
            catch (IndexOutOfBoundsException iob) {
                Result result = ks.saveResult(fullname, username, new Long(test.getTestId()), session.getId(), rightAnswers);
                model.addObject("result", result);
                model.addObject("end", true);
            }
        }
        catch (IllegalStateException ilgState) {
            return badRequest("invalidated session");
        }
        return model;
    }

    @RequestMapping(value="/reg", method=RequestMethod.GET)
    def ModelAndView userRegistrationPage() {
        ModelAndView model = new ModelAndView();
        model.setViewName("reg");
        return model;
    }

    @RequestMapping(value="/reg", method=RequestMethod.POST)
    def ModelAndView registerUser(HttpServletRequest request) {
        def regParams = ["username" : request.getParameter("username"),
        "firstName" : request.getParameter("first_name"),
        "lastName" : request.getParameter("last_name"),
        "email" : request.getParameter("email"),
        "password" : request.getParameter("password"),
        "confirmPassword" : request.getParameter("confirm_password")];
        try {
            regParams.each {k, v ->
                if ( (!k.equals("firstName")) && (!k.equals("lastName")) && (StringUtils.isEmpty(v)) ) {
                    throw new IllegalArgumentException();
                }
            }
        } catch (IllegalArgumentException ia) {
            return badRequest("empty request parameters");
        }
        if (!regParams["password"].equals(regParams["confirmPassword"])) {
            return badRequest("password confirmed incorrectly");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(regParams["password"]);
        Role userRole = ks.getEntityByQuery(Role.class, "SELECT role_id FROM roles WHERE role = ?", ["ROLE_USER"]);
        if (userRole == null) {
            return badRequest("internal error");
        }
        User newUser = new User(username : regParams["username"], password : hashedPassword, enabled : true,
            role : userRole, firstName : regParams["firstName"], lastName : regParams["lastName"],
            email : regParams["email"]);
        ks.saveEntity(newUser);
        ModelAndView model = new ModelAndView();
        model.setViewName("login");
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
            model.setViewName("403");
        } else {
            model.setViewName("redirect:/login")
        }


        return model;

    }

}
