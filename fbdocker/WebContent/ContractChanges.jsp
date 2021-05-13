<%-- 
    Document   : UserStats
    Created on : Apr 26, 2011, 10:27:51 AM
    Author     : BWatson
--%>
<%@page import="java.util.Enumeration"%>
<%@page import="com.watson.fantasybaseball.Worker"%>
<%@page import="java.io.File"%>
<%@page import="com.watson.html.HTMLHelper"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page session= "true" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%
    int team = 1;
    int newTeamId;
    int playerId;
    String activeContracts = "";
    String inactiveContracts = "";
    String msg = "";
    if(request.getParameter("team") != null) {
        team = Integer.parseInt(request.getParameter("team"));
    }
    if(request.getParameter("moveContract") != null) {
        String[] split = request.getParameter("moveContract").split(",");
        playerId = Integer.parseInt(split[0]);
        if(split[1].trim().matches("ACTIVATE")) {
            Worker.activatePlayer(playerId, team);
        } else if(split[1].trim().matches("DROP")) {
            Worker.deactivatePlayer(playerId, team);
        } else if(split[1].trim().matches("DELETE")) {
            Worker.deleteContract(playerId, team, 'N');
        } else {
            newTeamId = Integer.parseInt(split[1]);
            msg = Worker.moveContract(team, newTeamId, playerId);
        }
    }
    activeContracts = HTMLHelper.displayPlayers(team, true, 'A');
    inactiveContracts = HTMLHelper.displayPlayers(team, true, 'I');
    String teamName = Worker.getTeamNameById(team);
%>
<br>
<%=msg%>

<html>
    <head>
        <!-- <script type="text/javascript" id="ca_eum_ba" src="./includes/BAExt-artifact.js" data-profileUrl="https://dxc.dxi-na1.saas.broadcom.com/api/1/urn:ca:tenantId:5F2BA9BA-2E5A-4288-91D8-776C4238EBEF/urn:ca:appId:FantasyBaseball/profile?agent=browser" data-tenantID="5F2BA9BA-2E5A-4288-91D8-776C4238EBEF" data-appID="FantasyBaseball" data-appVersion="1.0" data-use-axa-appname="true" data-appKey="df7f1ae0-cab2-11ea-ab2d-49afc692c551" async ></script> -->
        <script type="text/javascript" id="ca_eum_ba" src="https://dxc.dxi-na1.saas.broadcom.com/api/1/urn:ca:tenantId:5F2BA9BA-2E5A-4288-91D8-776C4238EBEF/urn:ca:appId:FantasyBaseball/bajs?agent=browser" data-profileUrl="https://dxc.dxi-na1.saas.broadcom.com/api/1/urn:ca:tenantId:5F2BA9BA-2E5A-4288-91D8-776C4238EBEF/urn:ca:appId:FantasyBaseball/profile?agent=browser" data-tenantID="5F2BA9BA-2E5A-4288-91D8-776C4238EBEF" data-appID="FantasyBaseball" data-appVersion="1.0" data-use-axa-appname="true" data-appKey="df7f1ae0-cab2-11ea-ab2d-49afc692c551" async ></script>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
		<meta http-equiv="Content-Style-Type" content="text/css" />
        <title>Contracts for <%=teamName%></title>
        <link href="./includes/css/table.css" rel="stylesheet" type="text/css" />
        <link rel="icon" href="https://www.contractfantasybaseball.com/favicon.ico" type="image/x-icon">
        <link rel="shortcut icon" href="./favicon.ico" type="image/x-icon">
        <script type="text/javascript">
	        function standby(id) {
	            document.getElementById(id).src = 'https://s.yimg.com/iu/api/res/1.2/TcM85WhJ.fAOHWf2QKLjIw--~C/YXBwaWQ9eXNwb3J0cztjaD0yMDA7Y3I9MTtjdz0xNTM7ZHg9NzQ7ZHk9MDtmaT11bGNyb3A7aD02MDtxPTEwMDt3PTQ2/https://s.yimg.com/dh/ap/default/140828/silhouette@2x.png'
	        }
        </script>
    </head>
    <body>
        <div id="main">
            <div id="form">
                <%=HTMLHelper.displayTeamSelectBox(team, "./ContractChanges.jsp")%>
            </div>
            <center>
                <br>
                <font color="white"><b>Active Contracts</b></font>
                <%=activeContracts%>
                <font color="white"><b>Inactive Contracts</b></font>
                <%=inactiveContracts%>
            </center>
        </div>
    </body>
</html>