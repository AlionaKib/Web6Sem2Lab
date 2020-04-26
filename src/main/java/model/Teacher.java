package model;

import javax.xml.bind.annotation.*;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

@XmlType
@XmlRootElement(name = "teacher")
public class Teacher {
    static int UNIQUE_ID = 0;
    private int id = ++UNIQUE_ID;
    private String name;
    private String login;
    private String password;
    private ArrayList<StudentList> studentLists;

    public Teacher() {
        studentLists = new ArrayList<StudentList>();
    }

    public Teacher(String name, String login, String password) throws IOException{
        if(wordsCount(name)==3)
            this.name = name;
        else
            throw new IOException("Wrong name");

        this.login = login;
        this.password = password;
        studentLists = new ArrayList<StudentList>();
    }

    private static int wordsCount(String str) {
        StringTokenizer ins = new StringTokenizer(str);
        int cnt = 0;
        while (ins.hasMoreTokens()){
            ins.nextToken();
            ++cnt;
        }
        return cnt;
    }

    public static void setUniqueId(int uniqueId) {
        UNIQUE_ID = uniqueId;
    }

    public void setStudentLists(ArrayList<StudentList> studentLists) {
        this.studentLists = studentLists;
    }

    public int getGroupsCount() {
        return studentLists.size();
    }

    public void setStudentGroup (StudentList studentList, int number){
        studentLists.add(number, studentList);
    }

    public void setStudentGroup (StudentList studentList){
        studentLists.add(studentList);
    }

    public void deleteStudentGroup(int number){
        studentLists.remove(number);
    }

    public void deleteStudentGroup(StudentList studentList){
        studentLists.remove(studentList);
    }

    @XmlElement(name = "teacher-id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement(name = "teacher-name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "teacher-login")
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @XmlTransient
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void write (Writer out) {
        PrintWriter p = new PrintWriter(out);
        p.println(id);
        p.println(name);
        p.println(login);
        p.println(password);
        for (StudentList group: studentLists) {
            p.print(group.getGroupID()+" ");
        }
        //System.out.println("Writing done");
    }

    public ArrayList<Integer> read (Reader in){
        StreamTokenizer in1= new StreamTokenizer(in);
        String name = "";
        ArrayList<Integer> ints = new ArrayList<Integer>();
        try {
            in1.nextToken();
            this.id = (int)in1.nval;
            in1.nextToken();
            for (int i=0; i<3; ++i) {
                name = name + in1.sval + " ";
                in1.nextToken();
            }
            setName(name);
            setLogin(in1.sval);
            in1.nextToken();
            setPassword(in1.sval);
            while (in1.nextToken() != StreamTokenizer.TT_EOF){
                ints.add(new Integer((int)in1.nval));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ints;
    }

    @XmlElementWrapper(nillable = true, name = "teacher-groups")
    @XmlElement(name = "group")
    public ArrayList<StudentList> getStudentLists() {
        return studentLists;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                +'}';
    }
}
