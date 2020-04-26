package servlet;

import control.DBConnect;
import model.StudentList;
import model.Teacher;
import model.exception.NoElementInBase;
import model.exception.WrongAveragePoint;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "resultXML")
public class ResultXML extends HttpServlet {
    private String getUserDataXMLRepresentation(Teacher user) throws JAXBException, SQLException, ClassNotFoundException, WrongAveragePoint, IOException, NoElementInBase {
        user.setStudentLists(new DBConnect().getGroupsByTeacherId(user.getId()));

        StringWriter writer = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(Teacher.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(user, writer);

        return writer.toString();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession httpSession = req.getSession();
        Teacher user = (Teacher) httpSession.getAttribute("user");
        List<StudentList> studentLists = null;
        if (user==null){
            resp.setContentType("text/html");
            try (PrintWriter writer = resp.getWriter()) {
                writer.println(
                        "<head>\n" +
                                "    <meta charset=\"UTF-8\">\n" +
                                "    <link rel='stylesheet' href = 'styles.css'>\n" +
                                "    <title>Enter error</title>\n" +
                                "</head>\n" +
                                "\n" +
                                "<body class=\"back\">" +
                                "<form class=\"form-3 sc\" action=\"index.jsp\" method=\"post\">\n" +
                                "            <p class=\"info\">Login failed. Please enter.</p>\n" +
                                "            <input type=\"submit\" name=\"submitRes\" value=\"Return\">\n" +
                                "        </form>" +
                                "</body>"
                );
            }
        } else if (req.getParameter("return") != null) {
            httpSession.removeAttribute("user");
            resp.sendRedirect("index.jsp");
        } else if (req.getParameter("viewxml") != null) {
            resp.setContentType("application/xml");
            try (PrintWriter writer = resp.getWriter()) {
                writer.println(getUserDataXMLRepresentation(user)/*"<h2>Here should be the response from ResultServlet</h2>"*/);
            } catch (SQLException | ClassNotFoundException | JAXBException | NoElementInBase | WrongAveragePoint e) {
                e.printStackTrace();
            }
        }
    }
}
