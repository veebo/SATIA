package com.mephiboys.satia.groovy.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.mephiboys.satia.groovy.rest.Greeting
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
public class AjaxController {

    def final private ObjectMapper mapper = new ObjectMapper()
    def json(obj){ mapper.writeValueAsString(obj) }

    @RequestMapping("/greeting")
    def greeting(@RequestParam(value="name", defaultValue="World") name) {
        return json(new Greeting(id:name, content: "Hello!"))
    }
}