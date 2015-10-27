package com.mephiboys.satia.groovy

import com.mephiboys.satia.kernel.api.KernelService
import org.springframework.ui.ModelMap

import javax.naming.InitialContext
import javax.servlet.http.HttpServletRequest



class PageResolver {

    private final static KERNEL_SERVICE_NAME = "java:app/satia-kernel/KernelServiceEJB";

    protected final KernelService kernelService = (KernelService)new InitialContext().lookup(KERNEL_SERVICE_NAME)

    def main(ModelMap map, HttpServletRequest request){
        map["ejb"] = kernelService.getDataSource()
        return "auth"
    }

}

