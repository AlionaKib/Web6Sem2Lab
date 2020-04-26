<%@ page import="model.Teacher" %>
<%@ page import="model.StudentList" %>
<%@ page import="control.DBConnect" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.Student" %><%--
  Created by IntelliJ IDEA.
  User: nva71_000
  Date: 25.04.2020
  Time: 22:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    HttpSession httpSession = request.getSession();
    Teacher teacher = (Teacher) httpSession.getAttribute("user");
    boolean userExist = true;
    if(teacher == null) {
        userExist = false;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link rel='stylesheet' href = 'styles.css'>
    <title>Personal cabinet</title>
</head>

<body class="back">
    <%if (userExist) {%>
        <form class="form-3 sc" action="result.xml" method="get">
            <p class="info">Welcome, <%=teacher.getName()%></p>
            <input type="submit" name="return" value="Return">
            <input type="submit" name="viewxml" value="View XML page">
            <%for (StudentList group:teacher.getStudentLists()) {
                ArrayList<Student> students = group.getStudentArrayList();
                %>
                <table class="table" border="2">
                    <caption class="info">Group <%=group.getGroupID()%></caption>
                        <tr>
                            <th>Student ID</th>
                            <th>Name</th>
                            <th>Average point</th>
                        </tr>
                        <% for (Student student:students) {%>
                            <tr>
                              <th><%=student.getIdCardNumber()%></th>
                              <th><%=student.getName()%></th>
                              <th><%=student.getAveragePoint()%></th>
                            </tr>
                        <%}%>
                </table>
                <br/>
                <hr/>
            <% } %>
        </form>
    <%} else {%>
        <form class="form-3 sc" action="index.jsp" method="post">
            <p class="info">Login failed. Please enter.</p>
            <input type="submit" name="submitRes" value="Return">
        </form>
    <%}%>
</body>

</html>
