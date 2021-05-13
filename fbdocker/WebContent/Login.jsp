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

<html>
    <head>
        <!-- <script type="text/javascript" id="ca_eum_ba" src="./includes/BAExt-artifact.js" data-profileUrl="https://dxc.dxi-na1.saas.broadcom.com/api/1/urn:ca:tenantId:5F2BA9BA-2E5A-4288-91D8-776C4238EBEF/urn:ca:appId:FantasyBaseball/profile?agent=browser" data-tenantID="5F2BA9BA-2E5A-4288-91D8-776C4238EBEF" data-appID="FantasyBaseball" data-appVersion="1.0" data-use-axa-appname="true" data-appKey="df7f1ae0-cab2-11ea-ab2d-49afc692c551" async ></script> -->
        <script type="text/javascript" id="ca_eum_ba" src="https://dxc.dxi-na1.saas.broadcom.com/api/1/urn:ca:tenantId:5F2BA9BA-2E5A-4288-91D8-776C4238EBEF/urn:ca:appId:FantasyBaseball/bajs?agent=browser" data-profileUrl="https://dxc.dxi-na1.saas.broadcom.com/api/1/urn:ca:tenantId:5F2BA9BA-2E5A-4288-91D8-776C4238EBEF/urn:ca:appId:FantasyBaseball/profile?agent=browser" data-tenantID="5F2BA9BA-2E5A-4288-91D8-776C4238EBEF" data-appID="FantasyBaseball" data-appVersion="1.0" data-use-axa-appname="true" data-appKey="df7f1ae0-cab2-11ea-ab2d-49afc692c551" async ></script>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta http-equiv="Content-Style-Type" content="text/css" />
        <title>Login</title>
        <link href="./includes/css/narrow.css" rel="stylesheet" type="text/css" />
        <link rel="icon" href="https://www.contractfantasybaseball.com/favicon.ico" type="image/x-icon">
        <link rel="shortcut icon" href="./favicon.ico" type="image/x-icon">
    </head>

    <script>
        function setCustomerId() {
            var user = document.getElementById("username").value;
            var event = {"customerId":user};
            BrowserAgentExtension.setCustomerId(event);
        }
        
        //function startLogin(user) {
		//    BrowserAgentExtension.startApplicationTransaction({"transactionName" : "Login",
		//        "serviceName" : "Login", 
		//        "attributes" : { "user" : user}});
    	//}
        
        //function logSuccessfulLogin(user) {
		//    BrowserAgentExtension.stopApplicationTransaction({"transactionName" : "Login",
		//        "serviceName" : "Login",
		//        "attributes" : { "user" : user}});
	    //}
	    
	    //function logFailedLogin(user) {
	    //	BrowserAgentExtension.stopApplicationTransaction({"transactionName" : "Login",
	    //        "serviceName" : "Login",
	    //        "failure" : "Login failed",
	    //        "attributes" : { "user" : user}});
	    //}
	    
	</script>

<%
	if(request.getParameter("failed") != null) {
		out.println("Error logging in. check username and password");
	}
    if((request.getParameter("user") != null) && (request.getParameter("password") != null)) {
		String user = request.getParameter("user");
		String password = request.getParameter("password");
		String loginResult = Worker.login(user, password);
		if(null != loginResult) {
			int teamId = Integer.parseInt(loginResult.substring(0, loginResult.indexOf(",")));
			String key = loginResult.substring(loginResult.indexOf(",") + 1);
			String teamName = Worker.getTeamNameById(teamId);
			if(teamId > 0) {
	        	Cookie teamIdCookie = new Cookie("team_ID", teamId + "");
	        	Cookie tokenCookie = new Cookie("token", Helper.getReverseString(key));
	        	Cookie userCookie = new Cookie("user_name", user);
	        	teamIdCookie.setMaxAge(60*60*24*132);
	        	userCookie.setMaxAge(60*60*24*132);
	        	tokenCookie.setMaxAge(60*60*24*132);
	        	response.addCookie(teamIdCookie);
	        	response.addCookie(userCookie);
	        	response.addCookie(tokenCookie);
	            session.setAttribute("team_ID", teamId + "");
	            session.setAttribute("token", Helper.getReverseString(key));
	            session.setAttribute("user_name", user);
	            response.sendRedirect("./Contracts.jsp");
			} else {
	            response.sendRedirect("./Login.jsp?failed=true");
			}
        } else {
            response.sendRedirect("./Login.jsp?failed=true");
        }
    }
%>
    <body>
        <div id="main">
            <div id="form">
                <table>
                    <form action="Login.jsp" method="post">
                        <tr>
                            <td>Username:</td>
                            <td><input id="username" type="text" name="user"></td>
                        </tr>
                        <tr>
                            <td>Password:</td>
                            <td><input type="password" name="password"></td>
                        </tr>
                        <tr>
                            <td><input type="submit" value="Submit" onclick="setCustomerId();"></td>
                        </tr>
                    </form>
                </table>
            </div>
        </div>
    </body>
</html>