package com.mephiboys.satia.groovy.controller

import com.mephiboys.satia.kernel.api.KernelService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
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

}
