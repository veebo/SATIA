package com.mephiboys.satia.view;

import com.mephiboys.satia.kernel.api.KernelService;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;

public class GroovyFacade {

    KernelService b;

    private static final String MAIN_METHOD = "main";

    private static Logger logger = org.apache.log4j.Logger.getLogger(GroovyFacade.class);

    private GroovyClassLoader groovyLoader = new GroovyClassLoader();

    private GroovyObject pageResolver;

    public GroovyFacade() {
        init();
    }

    private static class GroovyFacadeHolder {
        public static final GroovyFacade INSTANCE = new GroovyFacade();
    }

    public static GroovyFacade getInstance(){
        return GroovyFacadeHolder.INSTANCE;
    }

    private void init() {
        try {
            Class pageResolverClass = groovyLoader.loadClass("com.mephiboys.satia.groovy.PageResolver");
            pageResolver = (GroovyObject) pageResolverClass.newInstance();
        } catch (Throwable e) {
            logger.error("Error during PageResolver initialization", e);
        }
    }

    public String execute(String method, ModelMap model, HttpServletRequest request){
        return pageResolver.invokeMethod(method == null ? MAIN_METHOD : method, new Object[]{model, request})+"";
    }


}
