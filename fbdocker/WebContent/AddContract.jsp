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
    String team = "";
    String extension = "";
    String player = "";
    String price = "";
    String length = "";
    String type = "";
    String mlbTeam = "";
    int startYear = Worker.getYear();
    int val = 1;
    if(request.getParameter("team") != null) {
        team = request.getParameter("team");
        val = Integer.parseInt(team);
    }
    if(request.getParameter("player") != null) {
        player = request.getParameter("player");
    }
    if(request.getParameter("price") != null) {
        price = request.getParameter("price");
    }
    if(request.getParameter("length") != null) {
        length = request.getParameter("length");
    }
    if(request.getParameter("type") != null) {
        type = request.getParameter("type");
    }
    if(request.getParameter("mlbTeam") != null) {
        mlbTeam = request.getParameter("mlbTeam");
    }
    if(type.toUpperCase().startsWith("F")) {
        startYear -= 1;
    }
    team = Worker.getTeamNameById(val);
    if(request.getParameter("player") != null) {
        out.print(Worker.insertContract(val, player, mlbTeam, Integer.parseInt(price), Integer.parseInt(length), startYear, 'Y', type.charAt(0)));
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="Content-Style-Type" content="text/css" />
        <title>Add Player for <%=team%></title>
        <link href="./includes/css/narrow.css" rel="stylesheet" type="text/css" />
    </head>
    <body>
        <div id="main">
            <div id="form">
                <%=HTMLHelper.displayTeamSelectBox(val, "AddContract.jsp")%>
            </div>
            <div id="form">
                <table>
                    <form action="AddContract.jsp">
                        <tr>
                            <td align="right">Player:</td>
                            <td align="left"><input type="text" name="player"></td>
                        </tr>
                        <tr>
                            <td align="right">Team:</td>
                            <td align="left"><input type="text" name="mlbTeam"></td>
                        </tr>
                        <tr>
                            <td align="right">Price:</td>
                            <td align="left"><input type="text" name="price" value="1"></td>
                        </tr>
                        <tr>
                            <td align="right">Length:</td>
                            <td align="left"><input type="text" name="length" value="1"></td>
                        </tr>
                        <tr>
                            <td align="right">Type:</td>
                            <td align="left">
                                <input type="radio" name="type" value="F">&nbsp;Free Agent<br>
                                <input type="radio" name="type" value="N" checked>&nbsp;Draft Pick<br>
                            </td>
                        </tr>
                        <input type="hidden" name="team" value="<%=val%>"/>
                        <tr>
                            <td align="right"><input type="submit" value="Submit"></td>
                        </tr>
                    </form>
                </table>
            </div>
        </div>
    </body>
</html>