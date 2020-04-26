<%@ page import="control.DBConnect" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="model.Teacher" %>
<%--
  Created by IntelliJ IDEA.
  User: nva71_000
  Date: 24.04.2020
  Time: 17:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        String submit = null;
        submit = request.getParameter("submit");
        boolean loginNotEmpty = true;
        boolean passwordNotEmpty = true;
        boolean userNotFound = false;
        if (submit != null) {
            if (login.equals("")) loginNotEmpty = false;
            if (password.equals("")) passwordNotEmpty = false;
            if (loginNotEmpty && passwordNotEmpty) {
                Teacher user = new DBConnect().checkTeacherExistence(login,password);
                if (user != null) {
                    HttpSession httpSession = request.getSession();
                    httpSession.setAttribute("user", user);
                    response.sendRedirect("result.html");
                } else userNotFound = true;
            }
        }
%>

<!DOCTYPE html>
<html>
<head>
        <meta charset="UTF-8">
        <link rel='stylesheet' href = 'styles.css'>
        <title>Enter</title>
</head>

<body>
<div class="back">
        <form class="form-3" action="index.jsp" method="post">
            <p class="info">Enter username and password</p>
            <p>
                <label for="login">Login</label>
            </p>
                <% if (!loginNotEmpty) { %>
                    <p class="alarm">
                        <input type="text" name="login" id="login1" placeholder="This field is required!">
                    </p>
                <% } else if (userNotFound){ %>
                    <p class="alarm">
                        <input type="text" name="login" id="login2" placeholder="No username and password combination found">
                    </p>
                <% } else { %>
                    <input type="text" name="login" id="login" placeholder="Login">
                <% } %>
            <p>
                <label for="password">Password</label>
            </p>
                <% if (!passwordNotEmpty) { %>
                    <p class="alarm">
                        <input type="password" name="password" id="password1" placeholder="This field is required!">
                    </p>
                <% } else if (userNotFound){ %>
                    <p class="alarm">
                        <input type="password" name="password" id="password2" placeholder="">
                    </p>
                <% } else { %>
                    <input type="password" name="password" id="password" placeholder="Password">
                <% } %>
            <p>
                <input type="submit" name="submit" value="Enter">
            </p>
        </form>
</div>
</body>

</html>
