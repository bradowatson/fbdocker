<%@page import="com.watson.fantasybaseball.common.Helper"%>
<%@page import="java.util.Enumeration"%>
<%@page import="com.watson.fantasybaseball.Worker"%>
<%@page import="java.io.File"%>
<%@page import="com.watson.html.HTMLHelper"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page session= "true" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%
	String token = null;
	boolean loggedIn = false;
	Cookie [] cookies = request.getCookies();
	String team = "";
	String username = "";
    int teamId = 1;
    String contracts = "";
    String budgetInfo = "";
	if(session.getAttribute("token") != null) {
		token = Helper.getReverseString((String) session.getAttribute("token"));
		loggedIn = true;
	} else {
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				if(cookie.getName().toLowerCase().contains("token")) {
					token = Helper.getReverseString(cookie.getValue());
					loggedIn = true;
				}
			}
		}
	}
	if(!loggedIn || null == token) {
		response.sendRedirect("./Login.jsp");
	} else {
	    String extension = "";
	    String msg = "<br>";
	    if(request.getParameter("team") != null) {
	        team = request.getParameter("team");
	        teamId = Integer.parseInt(team);
	    } else {
	    	if(cookies != null) {
	    		for(Cookie cookie : cookies) {
	    			if(cookie.getName().contains("team_ID")) {
	    				team = cookie.getValue();
	    			} else if(cookie.getName().contains("user_name")) {
	    				username = cookie.getValue();
	    			}
	    		}
	    	}
	    }
	    if(!team.equalsIgnoreCase("")) {
	    	teamId = Integer.parseInt(team);
	    }
	    if(request.getParameter("extension") != null) {
	        extension = request.getParameter("extension");
	        if(!extension.contains("nothing")) {
	            String split[] = extension.split(",");
	            int playerId = Integer.parseInt(split[0]);
	            if(split[1].contains("expire")) {
	                msg = Worker.expireContract(teamId, playerId);
	            } else {
	            	int length = Integer.parseInt(split[1]);
	            	char type = split[2].charAt(0);
	                msg = Worker.extendContract(teamId, playerId, length, (Worker.getYear() + 1), (Worker.getYear() + 1 + length), length, true, type);
	            }
	        }
	    }
	    if(msg.toUpperCase().contains("UNABLE")) {
	        out.println("<center><font color=\"red\"><b>" + msg + "</b></font></center>");
	    } else {
	        out.println("<center><font color=\"white\"><b>" + msg + "</b></font></center>");
	    }
	    try {
	    	contracts = HTMLHelper.displayPlayersJSON(teamId, false, token);
	    	//contracts = HTMLHelper.displayPlayersWithEditsJSON(teamId, false, token);
	    	budgetInfo = HTMLHelper.displayBudgetInfo(teamId);
		    team = Worker.getTeamNameById(teamId);
	    } catch (Exception e) {
	    	response.sendRedirect("./Login.jsp");
	    }
	}
    
%>

<html>
    <head>
        <!-- <script type="text/javascript" id="ca_eum_ba" src="./includes/BAExt-artifact.js" data-profileUrl="https://dxc.dxi-na1.saas.broadcom.com/api/1/urn:ca:tenantId:5F2BA9BA-2E5A-4288-91D8-776C4238EBEF/urn:ca:appId:FantasyBaseball/profile?agent=browser" data-tenantID="5F2BA9BA-2E5A-4288-91D8-776C4238EBEF" data-appID="FantasyBaseball" data-appVersion="1.0" data-use-axa-appname="true" data-appKey="df7f1ae0-cab2-11ea-ab2d-49afc692c551" async ></script> -->
        <script type="text/javascript" id="ca_eum_ba" src="https://dxc.dxi-na1.saas.broadcom.com/api/1/urn:ca:tenantId:5F2BA9BA-2E5A-4288-91D8-776C4238EBEF/urn:ca:appId:FantasyBaseball/bajs?agent=browser" data-profileUrl="https://dxc.dxi-na1.saas.broadcom.com/api/1/urn:ca:tenantId:5F2BA9BA-2E5A-4288-91D8-776C4238EBEF/urn:ca:appId:FantasyBaseball/profile?agent=browser" data-tenantID="5F2BA9BA-2E5A-4288-91D8-776C4238EBEF" data-appID="FantasyBaseball" data-appVersion="1.0" data-use-axa-appname="true" data-appKey="df7f1ae0-cab2-11ea-ab2d-49afc692c551" async ></script>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta http-equiv="Content-Style-Type" content="text/css" />
        <title>Contracts for <%=team%></title>
        <link href="./includes/css/table.css" rel="stylesheet" type="text/css" />
        <link rel="icon" href="https://www.contractfantasybaseball.com/favicon.ico" type="image/x-icon">
        <link rel="shortcut icon" href="./favicon.ico" type="image/x-icon">
        <script type="text/javascript">
	        function standby(id) {
	            document.getElementById(id).src = 'https://s.yimg.com/iu/api/res/1.2/TcM85WhJ.fAOHWf2QKLjIw--~C/YXBwaWQ9eXNwb3J0cztjaD0yMDA7Y3I9MTtjdz0xNTM7ZHg9NzQ7ZHk9MDtmaT11bGNyb3A7aD02MDtxPTEwMDt3PTQ2/https://s.yimg.com/dh/ap/default/140828/silhouette@2x.png'
	        }
	        
	        function setCustomerId() {
	            //var user = document.getElementById("username").value;
	            var user = '<%=username%>'
	            var event = {"customerId":user};
	            BrowserAgentExtension.setCustomerId(event);
	        }
        </script>
    </head>
    <body>
        <div id="main">
        	<table align="center" style="width:1300px">
        		<tr>
        			<td align="right">
			        	<div id="logout">
			        		<a href="./Logout.jsp">Logout</a>
			        	</div>
        			</td>
        		</tr>
	        	<tr>
	        		<td align="center">
	        			<div class="app-stores">
							<a href="https://www.contractfantasybaseball.com/apps/ios.ipa"><img alt="Download the iOS App" src="https://www.contractfantasybaseball.com/apps/apple.png"></a>
							<a href="https://www.contractfantasybaseball.com/apps/android.apk"><img alt="Download the Android App" src="https://www.contractfantasybaseball.com/apps/google.png"></a>
						</div>
					</td>
				</tr>
				<tr>
					<td align="center">
			            <div id="form">
			                <%=HTMLHelper.displayTeamSelectBox(teamId, "./Contracts.jsp")%>
			            </div>
		            </td>
	            </tr>
			</table>
       		<div id ="contracts">
       			<center>
					<br>
					<!-- <font color="white"><b>Pre-Draft Budget Information</b></font>
					<%=budgetInfo%> -->
					<font color="white"><b>Contracts for <%=team%></b></font>
					<%=contracts%>
            	</center>
       		</div>
        </div>
    </body>
</html>