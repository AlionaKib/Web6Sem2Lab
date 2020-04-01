package Control;

import Exceptions.NoElementInBase;
import Exceptions.SameIndex;
import Exceptions.WrongAveragePoint;
import Model.Client.Student;
import Model.Client.StudentList;
import Model.Client.Teacher;
import com.sun.xml.internal.ws.util.StringUtils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class Connector {
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

    private static final String SELECT_SCRIPT_TEMPLATE = "select * from %s where %s = %s;";

    private final Connection connection;
    private final Statement statement;

    public Connector() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Locale.setDefault(Locale.ENGLISH);
            connection = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            System.out.println("Connection as "+USERNAME + " successful");
        } catch (SQLException | ClassNotFoundException e) {
            closeConnection();
            throw new SQLException();
        }
    }

    public void closeConnection() throws SQLException {
        statement.close();
        connection.close();
    }

    public boolean createSchema() throws SQLException {
        createGroupsTable();
        System.out.println("Table " + GROUPS_TABLE + " created");
        createStudentsTable();
        System.out.println("Table " + STUDENTS_TABLE + " created");
        createTeachersTable();
        System.out.println("Table " + TEACHER_TABLE + " created");
        createTeachersGroupsTable();
        System.out.println("Table " + TEACHERGROUP_TABLE + " created");
        return true;
    }

    private boolean createStudentsTable() throws SQLException {
        dropTable(STUDENTS_TABLE);

        String script = "create table "+ STUDENTS_TABLE +"(" +
                STUDENT_ID + " number(8,0) primary key not null," +
                "Name varchar(50) not null," +
                GROUP_ID+" number(8,0)," +
                "GraduateSubscript varchar(30)," +
                "averagePoint number (3,2) check (averagePoint>=0 and averagePoint<=5)," +
                "foreign key ("+GROUP_ID+") references "+ GROUPS_TABLE +" ("+GROUP_ID+")" +
                ")";
        return statement.execute(script);
    }

    private boolean createGroupsTable() throws SQLException {
        dropTable(GROUPS_TABLE);

        String script = "create table " + GROUPS_TABLE + "  (" +
                GROUP_ID +" NUMBER(8,0) primary key not null, " +
                "PRESIDENT  NUMBER(8,0)" +
                ")" ;
        return statement.execute(script);
    }

    private boolean createTeachersTable() throws SQLException {
        dropTable(TEACHER_TABLE);

        String script = "create table "+TEACHER_TABLE+" (" +
                TEACHER_ID +" number(8,0) primary key not null," +
                "Name varchar(50) not null," +
                "Login varchar(30) not null," +
                "Password varchar(30) not null" +
                ")";

        return statement.execute(script);
    }

    private boolean createTeachersGroupsTable() throws SQLException {
        dropTable(TEACHERGROUP_TABLE);

        String script = "create table " + TEACHERGROUP_TABLE + "  (" +
                GROUP_ID+" NUMBER(8,0) not null, " +
                TEACHER_ID +"  NUMBER(8,0) not null," +
                "foreign key ("+TEACHER_ID+") references "+TEACHER_TABLE+"("+TEACHER_ID+")," +
                "foreign key ("+GROUP_ID+") references "+ GROUPS_TABLE +"(" +GROUP_ID+")" +
                ")" ;

        return statement.execute(script);
    }

    private void dropTable(String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tablesResultSet = metaData.getTables(null, null, tableName, null);
        if (!tablesResultSet.next()) return;

        statement.execute(String.format("DROP Table %s cascade constraints", tableName));
        System.out.println("Table " + tableName + " droped");
    }

    private boolean addObjectIntoTable(String tableName, String ... params) throws SQLException {
        String paramsString = String.join(" , ", params);
        String str = String.format("insert into %s values(%s)", tableName, paramsString);
        return statement.execute(str);
    }

    private int deleteObjectFromTable(String tableName, String idName, String value) throws SQLException {
        String str = String.format("delete from %s where %s = %s;", tableName, idName, value);
        return statement.executeUpdate(str);
    }

    public void fillStudentsBaseFromFiles(String...files) throws SQLException,IOException {
        File file;
        FileReader fr;
        StudentList group;

        for (String iterFile : files){
            file = new File(iterFile);
            fr = new FileReader(file);
            group = new StudentList();
            group.read(fr);
            fr.close();
            addObjectIntoTable(GROUPS_TABLE, Integer.toString(group.getGroupID()), "''");
            for (Student stud: group.getStudentArrayList())
                addObjectIntoTable(STUDENTS_TABLE, Integer.toString(stud.getIdCardNumber()), "'"+stud.getName()+"'",Integer.toString(group.getGroupID()), "'"+stud.getGraduateSubscript()+"'", Double.toString(stud.getAveragePoint()));
        }

        System.out.println("Students base filled");
    }

    public void fillTeachersBaseFromFiles(String...files) throws SQLException,IOException {
        File file;
        FileReader fr;
        Teacher teacher;
        ArrayList<Integer> ints;

        for (String iterFile : files){
            file = new File(iterFile);
            fr = new FileReader(file);
            teacher = new Teacher();
            ints = teacher.read(fr);
            fr.close();
            addObjectIntoTable(TEACHER_TABLE, Integer.toString(teacher.getId()), "'"+teacher.getName()+"'", "'"+teacher.getLogin()+"'", "'"+teacher.getPassword()+"'");
            for(Integer group : ints){
                addObjectIntoTable(TEACHERGROUP_TABLE, group.toString(), Integer.toString(teacher.getId()));
            }
        }

        System.out.println("Teachers base filled");
    }

    public boolean addStudentAtBase(Student stud, int groupId) throws SQLException, NoElementInBase, SameIndex {
        ResultSet result = statement.executeQuery("select * from "+STUDENTS_TABLE+" where "+STUDENT_ID+"="+ stud.getIdCardNumber());
        if(result.next()) throw new SameIndex("There is student with same id cadr in base");

        result = statement.executeQuery("select * from "+GROUPS_TABLE+" where groupid="+ groupId);
        if(!result.next()) throw new NoElementInBase("Group id does not exist");

        return addObjectIntoTable(STUDENTS_TABLE, Integer.toString(stud.getIdCardNumber()), "'"+stud.getName()+"'",Integer.toString(groupId), "'"+stud.getGraduateSubscript()+"'", Double.toString(stud.getAveragePoint()));
    }

    public boolean addStudentAtBase(Student stud) throws SQLException, SameIndex {
        ResultSet result = statement.executeQuery("select * from "+STUDENTS_TABLE+" where "+STUDENT_ID+"="+ stud.getIdCardNumber());
        if(result.next()) throw new SameIndex("There is student with same id cadr in base");
        return addObjectIntoTable(STUDENTS_TABLE, Integer.toString(stud.getIdCardNumber()), "'"+stud.getName()+"'","null", "'"+stud.getGraduateSubscript()+"'", Double.toString(stud.getAveragePoint()));
    }

    public boolean addTeacherInBase(Teacher teacher) throws SQLException, NoElementInBase, SameIndex {
        ResultSet result = statement.executeQuery("select * from "+TEACHER_TABLE+" where "+TEACHER_ID+"="+ teacher.getId());
        if(result.next()) throw new SameIndex("There is teacher with same id in base");

        String select = "select * from "+ GROUPS_TABLE+ " where groupid in (";
        for(StudentList list : teacher.getStudentLists()){
            select = select+list.getGroupID()+',';
        }
        select = select.substring(0, select.length() - 1);
        select = select + ')';
        result = statement.executeQuery(select);
        if(!result.next()) throw new NoElementInBase("No such group id found in base");

        boolean a = addObjectIntoTable(TEACHER_TABLE, Integer.toString(teacher.getId()), "'"+teacher.getName()+"'", "'"+teacher.getLogin()+"'", "'"+teacher.getPassword()+"'");
        for(StudentList list : teacher.getStudentLists()){
            a = a && addGroupAtBase(list);
        }
        return a;
    }

    public boolean addGroupAtBase(StudentList studentList) throws SQLException, SameIndex {
        ResultSet result = statement.executeQuery("select * from "+GROUPS_TABLE+" where "+GROUP_ID+"="+ studentList.getGroupID());
        if(result.next()) throw new SameIndex("There is teacher with same id in base");

       return addObjectIntoTable(GROUPS_TABLE, Integer.toString(studentList.getGroupID()), "''");
    }

    public boolean addGroupAtBase(StudentList studentList, Student president) throws SQLException, NoElementInBase, SameIndex {
        ResultSet result = statement.executeQuery("select * from "+GROUPS_TABLE+" where GroupId="+ studentList.getGroupID());
        if(result.next()) throw new SameIndex("There is teacher with same id in base");

        result = statement.executeQuery("select * from "+STUDENTS_TABLE+" where idcardnumber="+president.getIdCardNumber());
        if(!result.next()) throw new NoElementInBase("Group id does not exist");

        return addObjectIntoTable(GROUPS_TABLE, Integer.toString(studentList.getGroupID()), Integer.toString(president.getIdCardNumber()));
    }

    public int deleteStudentFromBase(int iDCadrNumber) throws SQLException, NoElementInBase {
        ResultSet result = statement.executeQuery("select * from "+STUDENTS_TABLE+" where "+STUDENT_ID+"="+ iDCadrNumber);
        if(!result.next()) throw new NoElementInBase("There is no student with id cadr in base");
        return deleteObjectFromTable(STUDENTS_TABLE,STUDENT_ID, Integer.toString(iDCadrNumber));
    }

    public int deleteTeacherFromBase(int id) throws SQLException, NoElementInBase {
        ResultSet result = statement.executeQuery("select * from "+TEACHER_TABLE+" where "+TEACHER_ID+"="+ id);
        if(result.next()) throw new NoElementInBase("There is no teacher with id in base");
        return deleteObjectFromTable(TEACHER_TABLE,TEACHER_ID, Integer.toString(id));
    }

    public int deleteGroupFromBase(int groupId) throws SQLException, NoElementInBase {
        ResultSet result = statement.executeQuery("select * from "+GROUPS_TABLE+" where "+GROUP_ID+"="+  groupId);
        if(result.next()) throw new NoElementInBase("There is no group with id cadr in base");
        return deleteObjectFromTable(GROUPS_TABLE,GROUP_ID, Integer.toString(groupId));
    }

    public boolean checkTeacherExistence(String login, String password) throws SQLException {
        String select = String.format("select * from"+TEACHER_TABLE+" where login = %s and password = %s;", login, password);
        return statement.executeQuery(select).next();
    }

    public ArrayList<StudentList> getGroupsByTeacherId(int id) throws NoElementInBase, SQLException, IOException {
        /*ResultSet result = statement.executeQuery("select * from "+TEACHER_TABLE+" where "+TEACHER_ID+"="+ id);
        if(!result.next()) throw new NoElementInBase("There is no teacher with id in base");*/

        ResultSet resultTeach = statement.executeQuery("select "+GROUP_ID+", PRESIDENT from (select * from "+TEACHERGROUP_TABLE+" where "+TEACHER_ID+"="+ id+") inner join "+GROUPS_TABLE+" using("+GROUP_ID+")");
        ArrayList<StudentList> list = new ArrayList<StudentList>();
        StudentList group;
        while(resultTeach.next()){
            group = new StudentList();
            group.setGroupID(resultTeach.getInt(1));
            //group.setStudent(getStudentById(result2.getInt(2)));
            group.setStudentArrayList(getStudentsByGroupId(group.getGroupID()));
            list.add(group);
        }
        return list;
    }

    public ArrayList<Student> getStudentsByGroupId(int id) throws SQLException, NoElementInBase, IOException {
        /*ResultSet result = statement.executeQuery("select * from "+GROUPS_TABLE+" where "+GROUP_ID+"="+  id);
        if(!result.next()) throw new NoElementInBase("There is no group with id cadr in base");*/

        ResultSet resultGroup = statement.executeQuery("select "+STUDENT_ID+" from "+STUDENTS_TABLE+" inner join "+GROUPS_TABLE+" using ("+GROUP_ID+") where "+GROUP_ID+"="+  id);
        ArrayList<Student> list = new ArrayList<>();
        Student stud;
        while(resultGroup.next()){
            stud = getStudentById(resultGroup.getInt(1));
            list.add(stud);
        }
        return list;
    }

    public Student getStudentById(int id) throws NoElementInBase, SQLException {
       /* ResultSet result = statement.executeQuery("select * from "+STUDENTS_TABLE+" where "+STUDENT_ID+"="+ id);
        if(!result.next()) throw new NoElementInBase("There is no student with id cadr in base");*/

        ResultSet resultStud = statement.executeQuery("select * from "+STUDENTS_TABLE+" where "+STUDENT_ID+"="+ id);
        Student stud = new Student();
        try {
            if( resultStud.next()) {
                stud.setIdCardNumber( resultStud.getInt(1));
                stud.setName( resultStud.getString(2));
                stud.setGraduateSubscript( resultStud.getString(4));
                stud.setAveragePoint( resultStud.getDouble(5));
            }
        } catch (WrongAveragePoint | IOException wrongAveragePoint) {
            wrongAveragePoint.printStackTrace();
        }
        return stud;
    }
}
