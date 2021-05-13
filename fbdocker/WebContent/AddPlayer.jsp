<%-- 
    Document   : UserStats
    Created on : Apr 26, 2011, 10:27:51 AM
    Author     : BWatson
--%>
<%@page import="com.watson.fantasybaseball.Player"%>
<%@page import="java.util.Enumeration"%>
<%@page import="com.watson.fantasybaseball.Worker"%>
<%@page import="java.io.File"%>
<%@page import="com.watson.html.HTMLHelper"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page session= "true" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%
    String name = "";
    String team = "";
    String position = "";
    if(request.getParameter("team") != null) {
        team = request.getParameter("team");
    }
    if(request.getParameter("name") != null) {
        name = request.getParameter("name");
    }
    if(request.getParameter("position") != null) {
        position = request.getParameter("position");
    }
    if(request.getParameter("name") != null) {
        Worker.enterPlayer(new Player(name, team, position));
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<meta http-equiv="Content-Style-Type" content="text/css" />
        <title>Add Player for <%=team%></title>
        <link href="./includes/css/narrow.css" rel="stylesheet" type="text/css" />
    </head>
    <body>
        <div id="main">
            <div id="form">
                <table>
                    <form action="AddPlayer.jsp">
                        <tr>
                            <td>Player:</td>
                            <td><input type="text" name="name"></td>
                        </tr>
                        <tr>
                            <td>Team:</td>
                            <td><input type="text" name="team"></td>
                        </tr>
                        <tr>
                            <td>Position:</td>
                            <td><input type="text" name="position"></td>
                        </tr>
                        <tr>
                            <td><input type="submit" value="Submit"></td>
                        </tr>
                    </form>
                </table>
            </div>
        </div>
    </body>
</html>