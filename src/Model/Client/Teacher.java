package Model.Client;

import java.io.Serializable;
import java.util.ArrayList;

public class Teacher{
    private int id;
    private String name;
    private String login;
    private String password;
    private ArrayList<StudentList> studentLists;

    public int getGroupsCount() {
        return studentLists.size();
    }

    public void setStudent (StudentList studentList, int number){
        studentLists.add(number, studentList);
    }

    public void setStudent (StudentList studentList){
        studentLists.add(studentList);
    }

    public void deleteStudent(int number){
        studentLists.remove(number);
    }

    public void deleteStudent(StudentList studentList){
        studentLists.remove(studentList);
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
