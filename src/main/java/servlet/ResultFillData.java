package servlet;

import control.DBConnect;
import model.Student;
import model.StudentList;
import model.Teacher;
import model.exception.NoElementInBase;
import model.exception.WrongAveragePoint;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "resultdata")
public class ResultFillData extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession httpSession = request.getSession();
        Teacher user = (Teacher) httpSession.getAttribute("user");

        if (user !=null){
            DBConnect connect = new DBConnect();
            try {
                ArrayList<StudentList> groupsList = connect.getGroupsByTeacherId(user.getId());
                user.setStudentLists(groupsList);
                response.sendRedirect("result.jsp");
            } catch (NoElementInBase | WrongAveragePoint | SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
