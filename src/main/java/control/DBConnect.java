package control;

import model.Student;
import model.StudentList;
import model.Teacher;
import model.exception.NoElementInBase;
import model.exception.WrongAveragePoint;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;

public class DBConnect {
    private static final String CONNECTION_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USERNAME = "kibitkina";
    private static final String PASSWORD = "11";

    private static final String STUDENTS_TABLE = "STUDENT";
    private static final String GROUPS_TABLE = "STUDENTSGROUP";
    private static final String TEACHER_TABLE = "TEACHER";
    private static final String TEACHERGROUP_TABLE = "TEACHERSGROUP";
    private static final String STUDENT_ID = "IDCARDNUMBER";
    private static final String GROUP_ID = "GROUPID";
    private static final String TEACHER_ID = "ID";

    private Connection connect() throws ClassNotFoundException, SQLException {
        Class.forName("oracle.jdbc.OracleDriver");
        Locale.setDefault(Locale.ENGLISH);
        return DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
    }

    public Teacher checkTeacherExistence(String login, String password) throws SQLException, ClassNotFoundException{
        Connection connection = this.connect();
        connection.setAutoCommit(false);
        System.out.println("Connection as "+USERNAME + " successful");

        PreparedStatement preparedStatement = connection.prepareStatement(String.format("select * from "+TEACHER_TABLE+" where login = '%s' and password = '%s'", login, password));
        ResultSet resultTeach = preparedStatement.executeQuery();
        if(!resultTeach.next()) return null;

        Teacher teacher = new Teacher();
        teacher.setId(resultTeach.getInt(1));
        teacher.setName(resultTeach.getString(2));
        teacher.setLogin(resultTeach.getString(3));
        teacher.setPassword(resultTeach.getString(4));

        preparedStatement.close();
        return teacher;
    }

    public ArrayList<StudentList> getGroupsByTeacherId(int id) throws NoElementInBase, SQLException, IOException, WrongAveragePoint, ClassNotFoundException {
        Connection connection = this.connect();
        connection.setAutoCommit(false);
        System.out.println("Connection as "+USERNAME + " successful");

        PreparedStatement preparedStatement = connection.prepareStatement("select * from (select * from "+TEACHERGROUP_TABLE+" where "+TEACHER_ID+" = "+id+") left join "+STUDENTS_TABLE+" using ("+GROUP_ID+")");
        ResultSet resultTeach = preparedStatement.executeQuery();

        ArrayList<StudentList> list = new ArrayList<StudentList>();
        StudentList group;
        int n;
        int k;
        while(resultTeach.next()){
            group = new StudentList();
            n=resultTeach.getInt(1);
            group.setGroupID(n);
            if(resultTeach.getInt(3)!=0)
                group.setStudent(new Student(resultTeach.getString(4), resultTeach.getInt(3), resultTeach.getString(5), resultTeach.getDouble(6)));
            while(resultTeach.next() && resultTeach.getInt(1)==n)
                if(resultTeach.getInt(3)!=0)
                    group.setStudent(new Student(resultTeach.getString(4), resultTeach.getInt(3), resultTeach.getString(5), resultTeach.getDouble(6)));
            list.add(group);
        }
        preparedStatement.close();
        connection.close();

        return list;
    }

    public ArrayList<Student> getStudentsByGroupId(int id) throws SQLException, NoElementInBase, IOException, WrongAveragePoint, ClassNotFoundException {
        Connection connection = this.connect();
        connection.setAutoCommit(false);
        System.out.println("Connection as "+USERNAME + " successful");

        PreparedStatement preparedStatement = connection.prepareStatement("select * from "+STUDENTS_TABLE+" where "+GROUP_ID+" = "+id);
        ResultSet resultGroup = preparedStatement.executeQuery();

        ArrayList<Student> list = new ArrayList<>();
        while(resultGroup.next()){
            list.add(new Student(resultGroup.getString(2), resultGroup.getInt(1), resultGroup.getString(4), resultGroup.getDouble(5)));
        }
        preparedStatement.close();
        connection.close();
        return list;
    }
}
