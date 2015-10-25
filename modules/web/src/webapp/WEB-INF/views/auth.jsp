<%@ page import="com.mephiboys.satia.kernel.api.KernelService" %>
<%@ page import="javax.naming.InitialContext" %>

Auth page

<%
InitialContext ic = new InitialContext();
KernelService ejb = (KernelService)ic.lookup("java:app/satia_web/KernelServiceEJB!com.mephiboys.satia.kernel.api.KernelService");
out.print(ejb+"<br/>");
out.print("ds:"+ejb.getDataSource());
%>