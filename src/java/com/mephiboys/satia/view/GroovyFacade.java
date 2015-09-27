package com.mephiboys.satia.view;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class GroovyFacade {

    private static Logger logger = org.apache.log4j.Logger.getLogger(GroovyFacade.class);

    private GroovyClassLoader groovyLoader = new GroovyClassLoader();

    private GroovyObject pageResolver;

    private Set<String> methods;

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
            methods = Arrays.asList(pageResolverClass
                    .getMethods()).stream().map(Method::getName).collect(Collectors.toSet());
            pageResolver = (GroovyObject) pageResolverClass.newInstance();
        } catch (Throwable e) {
            logger.error("Error during PageResolver initialization", e);
        }
    }

    public Object execute(String uri, Object args){
        String path = uri.substring(1);
        return pageResolver.invokeMethod(methods.contains(path) ? path : "defaultPage", args);
    }

    public String parseURI(String uri){
        return uri.substring(uri.indexOf('/') + 1);
    }


}
