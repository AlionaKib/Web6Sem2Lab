package Control;

import Exceptions.NoElementInBase;
import Exceptions.SameIndex;
import Exceptions.WrongAveragePoint;
import Model.Client.Student;
import Model.Client.StudentList;
import Model.Client.Teacher;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;


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

    private final Connection connection;

    public Connector() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Locale.setDefault(Locale.ENGLISH);
            connection = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
            connection.setAutoCommit(false);
            System.out.println("Connection as "+USERNAME + " successful");
        } catch (SQLException | ClassNotFoundException e) {
            closeConnection();
            throw new SQLException();
        }
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }

    public void createSchema() throws SQLException {
        createTables();
        System.out.println("Tables " + GROUPS_TABLE +" and "+STUDENTS_TABLE+" and "+TEACHERGROUP_TABLE+" and "+TEACHER_TABLE+ " created");
    }


    private boolean executeScript(String script) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(script);
        boolean resultExecution = preparedStatement.execute();
        preparedStatement.close();
        connection.commit();

        return resultExecution;
    }


    private void createTables() throws SQLException {
        dropTable(GROUPS_TABLE);
        dropTable(STUDENTS_TABLE);
        dropTable(TEACHER_TABLE);
        dropTable(TEACHERGROUP_TABLE);

        String scriptGroups = "create table " + GROUPS_TABLE + "  (" +
                GROUP_ID +" NUMBER(8,0) primary key not null, " +
                "PRESIDENT  NUMBER(8,0)" +
                ")";
        String scriptStudents = "create table "+ STUDENTS_TABLE +"(" +
                STUDENT_ID + " number(8,0) primary key not null," +
                "Name varchar(50) not null," +
                GROUP_ID+" number(8,0)," +
                "GraduateSubscript varchar(30)," +
                "averagePoint number (3,2) check (averagePoint>=0 and averagePoint<=5)," +
                "foreign key ("+GROUP_ID+") references "+ GROUPS_TABLE +" ("+GROUP_ID+")" +
                ")";
        String scriptTeachers = "create table "+TEACHER_TABLE+" (" +
                TEACHER_ID +" number(8,0) primary key not null," +
                "Name varchar(50) not null," +
                "Login varchar(30) not null unique," +
                "Password varchar(30) not null" +
                ")";
        String scriptTeacherGroups = "create table " + TEACHERGROUP_TABLE + "  (" +
                GROUP_ID+" NUMBER(8,0) not null, " +
                TEACHER_ID +"  NUMBER(8,0) not null," +
                "foreign key ("+TEACHER_ID+") references "+TEACHER_TABLE+"("+TEACHER_ID+")," +
                "foreign key ("+GROUP_ID+") references "+ GROUPS_TABLE +"(" +GROUP_ID+")" +
                ")" ;
        Statement statement = connection.createStatement();
        statement.addBatch(scriptGroups);
        statement.addBatch(scriptStudents);
        statement.addBatch(scriptTeachers);
        statement.addBatch(scriptTeacherGroups);

        statement.executeBatch();
        statement.close();
        connection.commit();
    }


    private boolean dropTable(String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tablesResultSet = metaData.getTables(null, null, tableName, null);
        if (!tablesResultSet.next()) return true;

        return executeScript(String.format("DROP Table %s cascade constraints", tableName));
    }

    private String getParams(String ... params){
        return String.join(" , ", params);
    }

    private boolean addObjectIntoTable(String tableName, String ... params) throws SQLException {
        String paramsString = String.join(" , ", params);

        return executeScript(String.format("insert into %s values(%s)", tableName, paramsString));
    }

    private int deleteObjectFromTable(String tableName, String idName, String value) throws SQLException, NoElementInBase {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tablesResultSet = metaData.getTables(null, null, tableName, null);
        if (!tablesResultSet.next()) throw new NoElementInBase("No such table found in base");

        PreparedStatement preparedStatement = connection.prepareStatement(String.format("delete from %s where %s = %s", tableName, idName, value));
        int resultExecutionInt = preparedStatement.executeUpdate();
        preparedStatement.close();
        return resultExecutionInt;
    }

    public void fillStudentsBaseFromFiles(String...files) throws SQLException,IOException {
        File file;
        FileReader fr;
        StudentList group;

        PreparedStatement preparedStatement = connection.prepareStatement("insert into "+GROUPS_TABLE+" values(?,?)");
        PreparedStatement preparedStatement1 = connection.prepareStatement("insert into "+STUDENTS_TABLE+" values(?,?,?,?,?)");
        for (String iterFile : files) {
            file = new File(iterFile);
            fr = new FileReader(file);
            group = new StudentList();
            group.read(fr);
            fr.close();
            preparedStatement.setInt(1,group.getGroupID());
            preparedStatement.setInt(2,0);
            preparedStatement.addBatch();
            for (Student stud : group.getStudentArrayList()) {
                preparedStatement1.setInt(1,stud.getIdCardNumber());
                preparedStatement1.setString(2,stud.getName());
                preparedStatement1.setInt(3,group.getGroupID());
                preparedStatement1.setString(4, stud.getGraduateSubscript());
                preparedStatement1.setDouble(5,stud.getAveragePoint());
                 preparedStatement1.addBatch();
            }
        }

        preparedStatement.executeBatch();
        preparedStatement.close();
        preparedStatement1.executeBatch();
        preparedStatement1.close();
        connection.commit();
        System.out.println("Students base filled");
    }

    public void fillTeachersBaseFromFiles(String...files) throws SQLException,IOException {
        File file;
        FileReader fr;
        Teacher teacher;
        ArrayList<Integer> ints;

        PreparedStatement preparedStatement = connection.prepareStatement("insert into "+TEACHER_TABLE+" values(?,?,?,?)");
        PreparedStatement preparedStatement1 = connection.prepareStatement("insert into "+TEACHERGROUP_TABLE+" values(?,?)");
        for (String iterFile : files){
            file = new File(iterFile);
            fr = new FileReader(file);
            teacher = new Teacher();
            ints = teacher.read(fr);
            fr.close();
            preparedStatement.setInt(1,teacher.getId());
            preparedStatement.setString(2,teacher.getName());
            preparedStatement.setString(3,teacher.getLogin());
            preparedStatement.setString(4,teacher.getPassword());
            preparedStatement.addBatch();
            //addObjectIntoTable(TEACHER_TABLE, Integer.toString(teacher.getId()), "'"+teacher.getName()+"'", "'"+teacher.getLogin()+"'", "'"+teacher.getPassword()+"'");
            for(Integer group : ints){
                preparedStatement1.setInt(1,group);
                preparedStatement1.setInt(2,teacher.getId());
                preparedStatement1.addBatch();
                //addObjectIntoTable(TEACHERGROUP_TABLE, group.toString(), Integer.toString(teacher.getId()));
            }
        }

        preparedStatement.executeBatch();
        preparedStatement.close();
        preparedStatement1.executeBatch();
        preparedStatement1.close();
        connection.commit();
        System.out.println("Teachers base filled");
    }

    public boolean addStudentAtBase(Student stud, int groupId) throws SQLException, NoElementInBase, SameIndex {
        return addObjectIntoTable(STUDENTS_TABLE, Integer.toString(stud.getIdCardNumber()), "'"+stud.getName()+"'",Integer.toString(groupId), "'"+stud.getGraduateSubscript()+"'", Double.toString(stud.getAveragePoint()));
    }

    public boolean addStudentAtBase(Student stud) throws SQLException, SameIndex {
        return addObjectIntoTable(STUDENTS_TABLE, Integer.toString(stud.getIdCardNumber()), "'"+stud.getName()+"'","null", "'"+stud.getGraduateSubscript()+"'", Double.toString(stud.getAveragePoint()));
    }

    public void addTeacherAtBase(Teacher teacher) throws SQLException, NoElementInBase, SameIndex {
        boolean a = addObjectIntoTable(TEACHER_TABLE, Integer.toString(teacher.getId()), "'"+teacher.getName()+"'", "'"+teacher.getLogin()+"'", "'"+teacher.getPassword()+"'");
    }

    public boolean addGroupAtBase(StudentList studentList) throws SQLException, SameIndex {
        return addObjectIntoTable(GROUPS_TABLE, Integer.toString(studentList.getGroupID()), "''");
    }

    public boolean addGroupAtBase(StudentList studentList, Student president) throws SQLException, NoElementInBase, SameIndex {
        return addObjectIntoTable(GROUPS_TABLE, Integer.toString(studentList.getGroupID()), Integer.toString(president.getIdCardNumber()));
    }

    public int deleteStudentFromBase(int iDCadrNumber) throws SQLException, NoElementInBase {
        return deleteObjectFromTable(STUDENTS_TABLE,STUDENT_ID, Integer.toString(iDCadrNumber));
    }

    public int deleteTeacherFromBase(int id) throws SQLException, NoElementInBase {
        return deleteObjectFromTable(TEACHER_TABLE,TEACHER_ID, Integer.toString(id));
    }

    public int deleteGroupFromBase(int groupId) throws SQLException, NoElementInBase {
        return deleteObjectFromTable(GROUPS_TABLE,GROUP_ID, Integer.toString(groupId));
    }

    public boolean checkTeacherExistence(String login, String password) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(String.format("select * from"+TEACHER_TABLE+" where login = %s and password = %s;", login, password));
        ResultSet result = preparedStatement.executeQuery();
        if(result.next()) return true;
        preparedStatement.close();
        return false;
    }

    public ArrayList<StudentList> getGroupsByTeacherId(int id) throws NoElementInBase, SQLException, IOException, WrongAveragePoint {
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
        return list;
    }

    public ArrayList<Student> getStudentsByGroupId(int id) throws SQLException, NoElementInBase, IOException, WrongAveragePoint {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from "+STUDENTS_TABLE+" where "+GROUP_ID+" = "+id);
        ResultSet resultGroup = preparedStatement.executeQuery();

        ArrayList<Student> list = new ArrayList<>();
        while(resultGroup.next()){
            list.add(new Student(resultGroup.getString(2), resultGroup.getInt(1), resultGroup.getString(4), resultGroup.getDouble(5)));
        }
        preparedStatement.close();
        return list;
    }

    public Student getStudentById(int id) throws NoElementInBase, SQLException, IOException, WrongAveragePoint {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from "+STUDENTS_TABLE+" where "+STUDENT_ID+"="+ id);
        ResultSet resultStud = preparedStatement.executeQuery();

        if(!resultStud.next()) throw new NoElementInBase("There is no student with id in base");

        Student stud = new Student();
        stud.setIdCardNumber( resultStud.getInt(1));
        stud.setName( resultStud.getString(2));
        stud.setGraduateSubscript( resultStud.getString(4));
        stud.setAveragePoint( resultStud.getDouble(5));

        preparedStatement.close();
        return stud;
    }

    public Teacher getTeacherById(int id) throws NoElementInBase, SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from "+TEACHER_TABLE+" where "+TEACHER_ID+"="+ id);
        ResultSet resultTeach = preparedStatement.executeQuery();

        if(!resultTeach.next()) throw new NoElementInBase("There is no teacher with id in base");

        Teacher teacher = new Teacher();
        teacher.setId(id);
        teacher.setName(resultTeach.getString(2));
        teacher.setLogin(resultTeach.getString(3));
        teacher.setPassword(resultTeach.getString(4));

        preparedStatement.close();
        return teacher;
    }

    public ArrayList<Teacher> getTeachersByStudentId(int id) throws NoElementInBase, SQLException, IOException {
        PreparedStatement preparedStatement = connection.prepareStatement("select "+TEACHER_ID+", NAME, LOGIN, PASSWORD from "+TEACHER_TABLE+" left join "+TEACHERGROUP_TABLE+" using ("+TEACHER_ID+") where "+GROUP_ID+" in (select "+GROUP_ID+" from "+STUDENTS_TABLE+" where "+STUDENT_ID+"="+id+')');
        ResultSet resultTeacher = preparedStatement.executeQuery();

        ArrayList<Teacher> list = new ArrayList<>();
        Teacher teacher;
        while(resultTeacher.next()){
            teacher = new Teacher(resultTeacher.getString(2),resultTeacher.getString(3),resultTeacher.getString(4));
            teacher.setId(resultTeacher.getInt(1));
            list.add(teacher);
        }
        preparedStatement.close();
        return list;
    }
}