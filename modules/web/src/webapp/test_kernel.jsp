<%@ page import="com.mephiboys.satia.kernel.api.KernelService" %>
<%@ page import="com.mephiboys.satia.kernel.impl.entitiy.*" %>
<%@ page import="groovy.lang.Binding" %>
<%@ page import="groovy.lang.GroovyShell" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apache.commons.lang.exception.ExceptionUtils" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="java.io.File" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="com.mephiboys.satia.kernel.test.DbAction" %>
Kernel Groovy Shell <br>

<form action="test_kernel.jsp" id="dbForm">
    <button type="submit" name="dbAction" value="fill">Fill Database</button>
    <button type="submit" name="dbAction" value="clear">Clear Database</button>
</form>

<form action="test_kernel.jsp" id="groovyForm">
    Input:<br>
    <textarea name="input" form="groovyForm" rows="10" cols="100"><%=
    StringUtils.isEmpty(request.getParameter("input")) ? "Enter code" : request.getParameter("input")
    %></textarea><br>
    <button type="submit">Execute</button>
</form>
Output:<br>
<textarea name="output" rows="20" cols="100"><%

    try {
        InitialContext ic = new InitialContext();
        KernelService ks = (KernelService)ic.lookup("java:app/satia-kernel/KernelServiceEJB");


        if (!StringUtils.isEmpty(request.getParameter("dbAction"))){
            String dbAction = request.getParameter("dbAction");
            if ("fill".equals(dbAction)){
                DbAction.fillDb(ks);
            } else if ("clear".equals(dbAction)) {
                DbAction.clearDb(ks);
            }
        }

        if (!StringUtils.isEmpty(request.getParameter("input"))){
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
            binding.setProperty("out", out);

            GroovyShell shell = new GroovyShell(binding);
            shell.evaluate("import com.mephiboys.satia.kernel.impl.entitiy.*;");
            out.newLine();
            out.println("Execution result: "+ shell.evaluate(request.getParameter("input")));
        }
    } catch (Throwable e){
        out.println(ExceptionUtils.getFullStackTrace(e));
    }

%>
</textarea>

