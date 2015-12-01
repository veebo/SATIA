<%@ page import="com.mephiboys.satia.kernel.api.KernelService" %>
<%@ page import="com.mephiboys.satia.kernel.impl.entitiy.*" %>
<%@ page import="groovy.lang.Binding" %>
<%@ page import="groovy.lang.GroovyShell" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apache.commons.lang.exception.ExceptionUtils" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="java.io.ByteArrayOutputStream" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.PrintStream" %>
<%@ page import="java.util.HashMap" %>
Kernel Groovy Shell <br>
<form action="test_kernel.jsp" id="groovyForm">
    Input:<br>
    <textarea name="input" form="groovyForm" rows="10" cols="100"><%=
    StringUtils.isEmpty(request.getParameter("input")) ? "Enter code" : request.getParameter("input")
    %></textarea><br>
    <input type="submit">
</form>
Output:<br>
<textarea name="output" rows="20" cols="100"><%
    if (!StringUtils.isEmpty(request.getParameter("input"))){
        try {
            InitialContext ic = new InitialContext();
            KernelService ks = (KernelService)ic.lookup("java:app/satia-kernel/KernelServiceEJB");
            Binding binding = new Binding();

            binding.setProperty("ks", ks);
            binding.setProperty("Generator",  new HashMap(){{put("class", Generator.class);}});
            binding.setProperty("Lang",  new HashMap(){{put("class", Lang.class);}});
            binding.setProperty("Phrase",  new HashMap(){{put("class", Phrase.class);}});
            binding.setProperty("Result",  new HashMap(){{put("class", Result.class);}});
            binding.setProperty("ResultPK",  new HashMap(){{put("class", ResultPK.class);}});
            binding.setProperty("Role",  new HashMap(){{put("class", Role.class);}});
            binding.setProperty("Task",  new HashMap(){{put("class", Task.class);}});
            binding.setProperty("Test",  new HashMap(){{put("class", Test.class);}});
            binding.setProperty("Translation",  new HashMap(){{put("class", Translation.class);}});
            binding.setProperty("User",  new HashMap(){{put("class", User.class);}});

            ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
            binding.setProperty("out", out);
            GroovyShell shell = new GroovyShell(binding);
            shell.evaluate("import com.mephiboys.satia.kernel.impl.entitiy.*;");
            out.newLine();
            out.println("Execution result: "+ shell.evaluate(request.getParameter("input")));
        } catch (Throwable e){
            out.println(ExceptionUtils.getFullStackTrace(e));
        }
    }
%>
</textarea>

