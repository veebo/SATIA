package com.mephiboys.satia.groovy.controller
import com.mephiboys.satia.kernel.api.KernelHelper
import com.mephiboys.satia.kernel.api.KernelService
import com.mephiboys.satia.kernel.impl.entitiy.*
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
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

        //add generators, fields and langs to model
        model.setViewName("test_edit");
        Collection<Generator> generators = ks.getEntitiesByQuery(Generator.class, 
            "SELECT gen_id FROM generators");
        model.addObject("generators", generators);

        def genFields = [:];
        for (Generator g : generators) {
            genFields[g.genId] = ks.getEntitiesByQuery(Field.class,
                "SELECT field_id FROM fields WHERE gen_id = ?", g.genId);
        }
        model.addObject("gen_fields", genFields);

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
            model.setViewName("404");
            return model;
        }
        Test test = ks.getEntityById(Test.class, testId);
        if (test == null) {
            model.setViewName("404");
            return model;
        }
        if ( (user == null) || (!user.getUsername().equals(test.getUser().getUsername())) ) {
            model.setViewName("403");
            return model;
        }
        model.addObject("test", test);
        model.addObject("create", false);
        
        //add field values for tasks
        def tasksFieldsValues = [:];
        for (Task t : test.tasks) {
            def fieldValues = [:];
            Long taskGenId = ((t.generator != null) ? t.generator : test.generator).genId;
            for (Field f : genFields[taskGenId]) {
                Collection<FieldValue> fvalues = ks.getEntitiesByQuery(FieldValue.class,
                    "SELECT field_value_id FROM field_values WHERE field_id = ? AND task_id = ?",
                    f.fieldId, t.taskId);
                List<FieldValue> fvaluesCopy = new ArrayList<FieldValue>(fvalues);
                fieldValues[f.fieldId] = fvaluesCopy;
            }
            tasksFieldsValues[t.taskId] = fieldValues;
        }
        model.addObject("tasks_fields_values", tasksFieldsValues);
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

    private def deleteFieldValues(tasksFieldsValues, taskId) {
        tasksFieldsValues[taskId].each { fId, fValues ->
            for (FieldValue fValue : fValues) {
                ks.deleteEntityById(FieldValue.class, fValue.fieldValueId);
            }
        }
        tasksFieldsValues[taskId] = [:];
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
        def genFields = model.getModel().get("gen_fields");
        def tasksFieldsValues = model.getModel().get("tasks_fields_values");
        if (tasksFieldsValues == null) {
            tasksFieldsValues = [:];
            model.addObject("tasks_fields_values", tasksFieldsValues);
        }

        //validate and modify test fields if needed and save tet entity
        test = ks.updateTest(test, createTest, ["Title" : request.getParameter("test_title"),
                                       "Description" : request.getParameter("test_description"),
                                       "Generator" : request.getParameter("test_generator"),
                                       "SourceLang" : request.getParameter("test_sourcelang"),
                                       "TargetLang" : request.getParameter("test_targetlang")]);
        if (test == null) {
            return badRequest();
        }
        model.addObject("create", false);

        //add new tasks
        int addedTasks;
        try {
            addedTasks = Integer.parseInt(request.getParameter("added_tasks_num"));
        } catch (NumberFormatException nf) {
            addedTasks = 0;
        }
        String[] values = new String[2];
        def tasksToAdd = []
        for (int i = 0; i < addedTasks; i++) {
            values[0] = request.getParameter("add_task" + i + "_phrase1");
            values[1] = request.getParameter("add_task" + i + "_phrase2");
            Long genId = null;
            try {
                genId = Long.parseLong(request.getParameter("add_task"+i+"_gen"));
            } catch (NumberFormatException ignored) {}
            Task createdTask = ks.newTask(values, genId, test);
            tasksToAdd << createdTask;
        }
        ks.createTasks(test, tasksToAdd);
        //add field values for new tasks
        for (int i = 0; i < addedTasks; i++) {
            Task t = tasksToAdd.get(i);
            if (t == null) {
                continue;
            }
            Generator taskGen = (t.generator != null) ? t.generator : test.generator;
            def fieldsValues = [:];
            for (Field f : genFields[taskGen.genId]) {
                String[] fValues = request.getParameterValues("add_task" + i + "_field" + f.fieldId);
                fieldsValues[f.fieldId] = ks.addFieldValues(f, t, fValues);
            }
            tasksFieldsValues[t.taskId] = fieldsValues;
        }

        //modify and delete existing tasks
        def tasksToRemove = [];
        for (Task t : test.getTasks()) {
            //delete if needed
            if (request.getParameter("del_task"+t.getTaskId()) != null) {
                tasksToRemove << t;
                deleteFieldValues(tasksFieldsValues, t.taskId);
                continue;
            }

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

            Generator oldGen = t.generator;
            ks.updateTask(test, t, phraseValues, genId);
            Generator newGen = t.generator;
            //if generator is updated - remove field values
            if ( ((oldGen == null) && (newGen != null)) || ((oldGen != null) && (!oldGen.equals(newGen))) ) {
                deleteFieldValues(tasksFieldsValues, t.taskId);
            }
            //update field values for current task
            Generator taskGen = (t.generator != null) ? t.generator : test.generator;
            for (Field f : genFields[taskGen.genId]) {
                //update and delete existing values for one field
                List<FieldValue> fieldValues = tasksFieldsValues[t.taskId][f.fieldId];
                if (fieldValues == null) {
                    continue;
                }
                for (Iterator<FieldValue> iter = fieldValues.listIterator(); iter.hasNext(); ) {
                    FieldValue fv = iter.next();
                    String del = request.getParameter("del_field_value_" + fv.fieldValueId);
                    if (del != null) {
                        iter.remove();
                        ks.deleteEntityById(FieldValue.class, fv.fieldValueId);
                        continue;
                    }
                    String newFValue = request.getParameter("field_value_" + fv.fieldValueId);
                    if  (newFValue != null) {
                        ks.updateFieldValue(fv, newFValue);
                    }
                }
                //add new values
                String[] valuesToAdd = request.getParameterValues("task" + t.taskId + "_field" + f.fieldId);
                List<FieldValue> addedValues = ks.addFieldValues(f, t, valuesToAdd);
                for (FieldValue addedValue : addedValues) {
                    tasksFieldsValues[t.taskId][f.fieldId].add(addedValue);
                }
            };
        }
        ks.removeTasks(tasksToRemove, test);

        return model;
    }

    @RequestMapping(value="/remove/{testIdStr}", method=RequestMethod.GET)
    def removeTestPage(@PathVariable("testIdStr") String testIdStr) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authUserName = auth.getName();
        User user = ks.getEntityById(User.class, authUserName);
        ModelAndView model = getTestModel(user, testIdStr);
        if (!"test_edit".equals(model.getViewName())) {
            return model;
        }
        boolean create = model.getModel().get("create");
        if (create) {
            return notFound();
        }
        def tasksFieldsValues = model.getModel().get("tasks_fields_values");
        tasksFieldsValues.each { taskId, fieldsValues ->
            fieldsValues.each {fieldId, fvalues ->
                for (FieldValue fv : fvalues) {
                    ks.deleteEntityById(FieldValue.class, fv.getFieldValueId());
                }
            };
        };
        ks.removeTest(model.getModel().get("test"));
        return defaultPage();
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
        int next;
        int rightAnswers;
        try {
            session = request.getSession();
            String sessionId = session.getId();
            if (sessionId == null) {
                return badRequest("invalid session id");
            }

            test = session.getAttribute("test");
            if (!(test instanceof Test)) {
                return badRequest("invalid session attribute: test");
            }

            String nextFromSess = session.getAttribute("next");
            String rightAnswersFromSess = session.getAttribute("right_answers");
            try {
                if (nextFromSess != null) {
                    next = Integer.parseInt(nextFromSess);
                } else {
                    session.setAttribute("next", new Integer(0));
                    next = 0;
                }
                if (rightAnswersFromSess == null) {
                    session.setAttribute("right_answers", new Integer(0));
                    rightAnswers = 0;
                } else {
                    rightAnswers = Integer.parseInt(rightAnswersFromSess);
                }
            } catch (NumberFormatException nf) {
                return badRequest("invalid session attributes : " + nextFromSess + " " + rightAnswersFromSess);
            }
            cur = next - 1;
            long answer;
            String answerFromReq = request.getParameter("answer");


            if ((next > 0) && (next < test.getTasks().size()) && (answerFromReq == null)) {
                next = cur;
                cur = cur - 1;
            }
            

            if ( (cur >= -1) && (cur < test.getTasks().size()) ) {
                try {
                    if (answerFromReq != null) {
                        answer = Long.parseLong(answerFromReq);
                        Task curTask = test.getTasks().get(cur);
                        Phrase rightPhrase = ( (curTask.getSourceNum() == 1) ?
                            curTask.getTranslation().getPhrase2() : curTask.getTranslation().getPhrase1() );
                        if (answer == rightPhrase.getPhraseId()) {
                            ++rightAnswers;
                            session.setAttribute("right_answers", new Integer(rightAnswers));
                        }
                    }
                } catch (NumberFormatException nf) {
                    return badRequest("invalid request parameter : answer");
                }

                if (cur == test.getTasks().size() - 1) {
                    username = session.getAttribute("username");
                    fullname = session.getAttribute("fullname");
                    Date startTime = session.getAttribute("start_time");
                    if (startTime == null) {
                        return badRequest("invalid session attribute : start_time");
                    }
                    Result result = ks.saveResult(fullname, username, new Long(test.getTestId()), sessionId, rightAnswers, startTime);
                    session.setAttribute("result", result);
                    ++next;
                    session.setAttribute("next", new Integer(next));

                    model.addObject("result", result);
                    model.addObject("end", true);

                    return model;
                }
                else if (cur == -1) {
                    String fullnameFromReq = request.getParameter("name");
                    if (fullnameFromReq != null) {
                        session.setAttribute("fullname", fullnameFromReq);
                    }
                    session.setAttribute("start_time", new Date());
                }

                Task nextTask = test.getTasks().get(next);
                ++next;
                session.setAttribute("next", new Integer(next));


                Translation translation = nextTask.getTranslation();

                Phrase sourcePhrase = (nextTask.getSourceNum() == 1
                        ? translation.getPhrase1()
                        : translation.getPhrase2()
                );

                Phrase translatedPhrase = (nextTask.getSourceNum() == 1
                        ? translation.getPhrase2()
                        : translation.getPhrase1()
                );

                List<String> wrongAnswers = ks.generateAnswers(sourcePhrase.getValue(),
                        translatedPhrase.getValue(), nextTask);


                model.addObject("question", sourcePhrase.getValue());
                def answers = [];

                def i = 1
                for (String a : wrongAnswers){
                    answers << ["id" : translatedPhrase.getPhraseId()+(i++), "value" : a];
                }

                answers.add((int)(wrongAnswers.size()*Math.random()),
                        ["id" : translatedPhrase.getPhraseId(), "value" : translatedPhrase.getValue()]);

                model.addObject("answers", answers);
                model.addObject("end", false); ;
            }
            else if (cur >= test.getTasks().size()) {
                model.addObject("end", true);
                Result result = session.getAttribute("result");
                if (result == null) {
                    return badRequest("invalid session attribute : result");
                }
                model.addObject("result", result);
            }
            else {
                return badRequest("invalid session attribute : next = " + next);
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
        ModelAndView model = new ModelAndView();
        model.setViewName("reg");

        def paramNames = ["username", "first_name", "last_name", "email", "password", "confirm_password"];
        def regParams = [:];
        for (String param : paramNames) {
            String filteredParam = ks.filterString(request.getParameter(param));
            if (filteredParam == null) {
                model.addObject("error_message", "Illegal input");
                return model;
            }
            regParams[param] = filteredParam;
        };

        if (!regParams["password"].equals(regParams["confirm_password"])) {
            model.addObject("error_message", "password confirmed incorrectly");
            return model;
        }

        User existingUser = ks.getEntityById(User.class, regParams["username"]);
        if (existingUser != null) {
            model.addObject("error_message", "User with the same name already exists");
            return model;
        }
        existingUser = ks.getEntityByQuery(User.class,
            "SELECT username FROM users WHERE email = ?", regParams["email"]);
        if (existingUser != null) {
            model.addObject("error_message", "User with the same email already exists");
            return model;
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(regParams["password"]);
        Role userRole = ks.getEntityByQuery(Role.class, "SELECT role_id FROM roles WHERE role = 'ROLE_USER'", null);
        if (userRole == null) {
            return badRequest("internal error");
        }
        User newUser = new User(username : regParams["username"], password : hashedPassword, enabled : true,
            role : userRole, firstName : regParams["first_name"], lastName : regParams["last_name"],
            email : regParams["email"]);
        ks.saveEntity(newUser);

        model.setViewName("home");
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
        if (auth instanceof AnonymousAuthenticationToken){
            model.addObject("display_logout_form", "none");
            if (error != null) {
                model.addObject("error", "Invalid username and password!");
            }
        } else {
            model.addObject("display_login_form", "none");
            model.addObject("msg", "You're already logged in.")
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
