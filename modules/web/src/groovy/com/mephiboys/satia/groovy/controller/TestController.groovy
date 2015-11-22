package com.mephiboys.satia.groovy.controller

import com.mephiboys.satia.kernel.api.KernelService
import com.mephiboys.satia.kernel.impl.entitiy.*

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

import javax.naming.InitialContext

@Controller
public class TestController {

    final KERNEL_SERVICE_JNDI = "java:app/satia-kernel/KernelServiceEJB"

    @RequestMapping(value = [ "/kernelTest" ], method = RequestMethod.GET)
    def ModelAndView defaultPage() {
        ModelAndView model = new ModelAndView()
        model.addObject("em", getKernelService().getEntityManager())
        model.setViewName("kernel_test");
        return model;

    }

    def getKernelService(){
        InitialContext ic = new InitialContext()
        return (KernelService)ic.lookup(KERNEL_SERVICE_JNDI)
    }

    @RequestMapping(value = "/addObjects", method = RequestMethod.GET)
    def ModelAndView addObjects() {
        KernelService ks = getKernelService();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return accesssDenied();
        }
        String authUserName = auth.getName();
        User user = ks.getEntityById(User.class, authUserName);
        if (user == null) {
            return accessDenied();
        }
        
        def tests = []
        def tasks = []
        def translations = []
        def phrases = []
        def generators = []
        def results = []
        def langs = []

        Lang eng = new Lang(lang:"eng")
        Lang rus = new Lang(lang:"rus")

        Phrase apple = new Phrase(phraseId: 1, value: "Apple", lang: eng)
        Phrase yabloko = new Phrase(phraseId: 2, value: "Яблоко", lang: rus)
        Phrase orange = new Phrase(phraseId: 3, value: "Orange", lang: eng)
        Phrase apelsin = new Phrase(phraseId: 4, value: "Апельсин", lang: rus)
        Phrase banana = new Phrase(phraseId: 5, value: "Banana", lang: eng)
        Phrase banan = new Phrase(phraseId: 6, value: "Банан", lang: rus)
        Phrase lemon = new Phrase(phraseId: 7, value: "Lemon", lang: eng)
        Phrase limon = new Phrase(phraseId: 8, value: "Лимон", lang: rus)

        Translation tr1 = new Translation(translationId: 1, phrase1: apple, phrase2: yabloko)
        Translation tr2 = new Translation(translationId: 2, phrase1: orange, phrase2: apelsin)
        Translation tr3 = new Translation(translationId: 3, phrase1: banana, phrase2: banan)
        Translation tr4 = new Translation(translationId: 4, phrase1: lemon, phrase2: limon)

        Task task1 = new Task(taskId: 1, translation: tr1, sourceNum: 2)
        Task task2 = new Task(taskId: 2, translation: tr2, sourceNum: 2)
        Task task3 = new Task(taskId: 3, translation: tr3, sourceNum: 2)
        Task task4 = new Task(taskId: 4, translation: tr4, sourceNum: 2)

        Generator g1 = new Generator(
                genId: 1,
                impl: "com.mephiboys.satia.generator.SimpleGenerator"
        )


        Test test1 = new Test(
                testId: 1,
                title: "Fruit test",
                description: "Try to pass the simplest test ever",
                createdWhen: new Date().toTimestamp(),
                user: user,
                generator: g1,
                tasks: [task1, task2, task3, task4]
        )

        Result res1 = new Result(startTime : new Date().toTimestamp(), sessionKey : "sessionKey1",
                                 value : 50.00, test : test1, user : null);
        Result res2 = new Result(startTime : new Date().toTimestamp(), sessionKey : "sessionKey2",
                                 value : 60.00, test : test1, user : null);
        Result res3 = new Result(startTime : new Date().toTimestamp(), sessionKey : "sessionKey3",
                                 value : 70.00, test : test1, user : null);

        tests << test1;
        tasks << task1 << task2 << task3 << task4;
        translations << tr1 << tr2 << tr3 << tr4;
        phrases << apple << orange << banana << lemon << yabloko << apelsin << banan << limon;
        langs << eng << rus;
        generators << g1;
        results << res1 << res2 << res3;

        def all = [langs, genrators, phrases, translations, tasks, tests, results];
        all.each{
            it.each{
                ks.saveEntity(it);
            }
        }
        return showObjectsByQuery();
    }

    @RequestMapping(value="showObjectsByIds", method=RequestMethod.GET)
    def ModelAndView showObjectsByIds() {
        ModelAndView model = new ModelAndView();
        model.setViewName("kernel_test");
        KernelService ks = getKernelService();
        Collection<Test> tests = ks.getEntitiesByIds(Test.class, [1L,2L,3L]);
        model.addObject("tests", tests);
        /*Collection<Result> results = ks.getEntitiesByIds(Result.class,
        [new ResultPK(username : "guest", testId : 1, 
                startTime : new Date().toTimestamp(), sessionKey : "sessionKey")]);
        model.addObject("results", results);*/
        return model;
    }

    @RequestMapping(value="showObjectsByQuery", method=RequestMethod.GET)
    def ModelAndView showObjectsByQuery() {
        ModelAndView model = new ModelAndView();
        model.setViewName("kernel_test");
        KernelService ks = getKernelService();
        Collection<Test> tests = ks.getEntitiesByQuery(Test.class, "SELECT test_id FROM tests", null);
        model.addObject("tests", tests);
        /*Collection<Result> results = ks.getEntitiesByQuery(Result.class,
            "SELECT username,test_id,start_time,session_key FROM results", null);
        model.addObject("results", results);*/
        return model;
    }

    @RequestMapping(value="deleteObjects", method=RequestMethod.GET)
    def ModelAndView deleteObjects() {
        KernelService ks = getKernelService();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return accesssDenied();
        }
        String authUserName = auth.getName();
        User user = ks.getEntityById(User.class, authUserName);
        if (user == null) {
            return accessDenied();
        }
        Collection<Test> tests = ks.getEntitiesByQuery(Test.class, "SELECT test_id FROM tests", null);
        tests.each {
            ks.deleteEntityById(Test.class, it.getTestId());
        }
        return showObjectsByQuery();
    }

    @RequestMapping(value = "/403kernel", method = RequestMethod.GET)
    def ModelAndView accessDenied() {
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
