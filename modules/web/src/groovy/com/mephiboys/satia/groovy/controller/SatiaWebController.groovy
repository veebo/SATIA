package com.mephiboys.satia.groovy.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import javax.servlet.http.HttpServletRequest

@Controller
public class SatiaWebController {

    @RequestMapping(value = "/**", method = RequestMethod.GET)
    def main(ModelMap model, HttpServletRequest request) {
        model['title'] = 'THE PAGE'
        model['message'] = 'WAS GROOVED'
        return "auth";
    }

}
