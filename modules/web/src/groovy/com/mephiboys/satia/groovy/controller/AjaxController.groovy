package com.mephiboys.satia.groovy.controller
import com.fasterxml.jackson.databind.ObjectMapper
import com.mephiboys.satia.kernel.api.KernelHelper
import com.mephiboys.satia.kernel.api.KernelService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
public class AjaxController {

    def final private ObjectMapper mapper = new ObjectMapper()
    def json(obj){ mapper.writeValueAsString(obj) }

    def KernelService ks = KernelHelper.getKernelService()

    @RequestMapping(value = "/actions/get/results", method = RequestMethod.GET)
    def getResultsByTestId(@RequestParam(value="test_id", required = true) testId) {
        return json("resp")
    }
}