<%-- 
    Document   : UserStats
    Created on : Apr 26, 2011, 10:27:51 AM
    Author     : BWatson
--%>
<%@page import="com.watson.fantasybaseball.Player"%>
<%@page import="java.util.Enumeration"%>
<%@page import="com.watson.fantasybaseball.Worker"%>
<%@page import="com.watson.fantasybaseball.common.Helper"%>
<%@page import="java.io.File"%>
<%@page import="com.watson.html.HTMLHelper"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page session= "true" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%
	if(request.getParameter("user") != null) {
		String user = request.getParameter("user");
%>
<script type="text/javascript">
    BrowserAgentExtension.startApplicationTransaction({"transactionName" : "logout",
        "serviceName" : "Logout", 
        "attributes" : { "user" : "<%=user%>"}});
</script>
<%
    }
	session.invalidate();
	Cookie [] cookies = request.getCookies();
	for(Cookie cookie : cookies) {
		cookie.setMaxAge(0);
	}
    response.sendRedirect("./Login.jsp");
%>