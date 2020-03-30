package Control;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
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
        createStudentsTable();
        return true;
    }

    private boolean createStudentsTable() throws SQLException {
        dropTable(STUDENTS_TABLE);

        String script = "create table STUDENT (" +
                "idCadrNumber number(8,0) primary key not null," +
                "Name varchar(30) not null," +
                "GroupId number(8,0)," +
                "foreign key (GroupId) references studentsgroup(GroupId)" +
                ")";
        System.out.println("Table " + STUDENTS_TABLE + " created");
        return statement.execute(script);
    }

    private boolean createGroupsTable() throws SQLException {
        dropTable(GROUPS_TABLE);

        String script = "create table " + GROUPS_TABLE + "  (" +
                "GROUPID NUMBER(8,0) primary key not null, " +
                "PRESIDENT  NUMBER(8,0)" +
                ")" ;
        System.out.println("Table " + GROUPS_TABLE + " created");
        return statement.execute(script);
    }

    private boolean createTeachersTable() throws SQLException {
        dropTable(TEACHER_TABLE);

        String script = "create table TEACHER\n" +
                "(\n" +
                "\tuserID varchar(40) not null,\n" +
                "\tlogin varchar(50) not null,\n" +
                "\tpassword varchar(50) not null,\n" +
                "\tsurname varchar(50) not null,\n" +
                "\tname varchar(50) not null\n" +
                ");\n" +
                "\n" +
                "create unique index users_Login_uindex\n" +
                "\ton users (login);\n" +
                "\n" +
                "create unique index users_UserID_uindex\n" +
                "\ton users (userID);" +
                "\n" +
                "alter table users\n" +
                "\tadd constraint users_pk\n" +
                "\t\tprimary key (userID);";

        return statement.execute(script);
    }

    private boolean createTeachersGroupsTable() throws SQLException {
        dropTable(TEACHERGROUP_TABLE);

        String script = "create table TEACHERSGROUP\n" +
                "(\n" +
                "\tuserID varchar(40) not null,\n" +
                "\tlogin varchar(50) not null,\n" +
                "\tpassword varchar(50) not null,\n" +
                "\tsurname varchar(50) not null,\n" +
                "\tname varchar(50) not null\n" +
                ");\n" +
                "\n" +
                "create unique index users_Login_uindex\n" +
                "\ton users (login);\n" +
                "\n" +
                "create unique index users_UserID_uindex\n" +
                "\ton users (userID);" +
                "\n" +
                "alter table users\n" +
                "\tadd constraint users_pk\n" +
                "\t\tprimary key (userID);";

        return statement.execute(script);
    }

    private void dropTable(String tableName) {
        try {
            statement.execute(String.format("DROP Table %s", tableName));
            System.out.println("Table " + tableName + " droped");
        } catch (SQLException exception) {
           // System.out.println(exception.toString());
        }
    }
}
