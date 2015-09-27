package com.mephiboys.satia.servlet;

//import groovy.lang.GroovyCodeSource;
//import groovy.lang.GroovyShell;
//import org.apache.log4j.Logger;

import com.mephiboys.satia.view.GroovyFacade;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Arrays;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;


@WebServlet("/")
public class FrontageServlet extends HttpServlet {

    private static Logger logger = org.apache.log4j.Logger.getLogger(FrontageServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            GroovyFacade groovy = GroovyFacade.getInstance();
            logger.debug("request.getPathInfo():"+request.getPathInfo());
            logger.debug("request.getRequestURI():"+request.getRequestURI());
            logger.debug("request.getContextPath():"+request.getContextPath());
            logger.debug("request.getRequestURL():"+request.getRequestURL());
            response.getWriter().print(groovy.execute(request.getPathInfo(), null));
        } catch (Throwable e){
            logger.error("Internal Server Error", e);
        }
    }
}