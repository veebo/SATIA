package com.mephiboys.satia.groovy.controller
import com.fasterxml.jackson.databind.ObjectMapper
import com.mephiboys.satia.kernel.api.KernelHelper
import com.mephiboys.satia.kernel.api.KernelService
import com.mephiboys.satia.kernel.impl.entitiy.Result
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
    def getResultsByTestId(@RequestParam(value="test_id", required = true) long testId) {
        def results = ks.getEntitiesByQuery(
                Result.class,
                "select test_id,start_time,session_key from results where test_id = ?",
                testId
        );
        def response = []
        results.each {
            response.add([
                    'fullname' : it.fullname,
                    'sesstionKey' : it.sessionKey,
                    'startTime' : it.startTime,
                    'testId' : it.test.testId,
                    'username' : it.user == null ? "" : it.user.username,
                    'value' : it.value
            ])
        }
        return json(response)
    }
}