package java.com.mephiboys.satia.controller;


import java.com.mephiboys.satia.view.GroovyFacade;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class FrontageController {

    private final GroovyFacade groovy = GroovyFacade.getInstance();

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String hello(ModelMap model, HttpServletRequest request) {
        return groovy.execute(null, model, request);
    }

}
