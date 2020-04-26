package model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

@XmlType
@XmlRootElement(name = "group")
public class StudentList implements Serializable {
    static int UNIQUE_ID = 0;
    private int groupID = ++UNIQUE_ID;
    private  Student president;
    private ArrayList<Student> studentArrayList;

    public StudentList() {
        studentArrayList = new ArrayList<Student>();
    }


    @XmlElement(name = "group-id")
    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public void setStudentArrayList(ArrayList<Student> studentArrayList) {
        this.studentArrayList = studentArrayList;
    }

    @XmlElementWrapper(nillable = true, name = "group-students")
    @XmlElement(name = "student")
    public ArrayList<Student> getStudentArrayList(){
        return this.studentArrayList;
    }

    public int getStudentsCount() {
        return studentArrayList.size();
    }

    public Student getPresident() {
        return president;
    }

    public void setPresident(Student president) {
        this.president = president;
    }

    public void setStudent (Student student, int number){
        studentArrayList.add(number, student);
    }

    public void setStudent (Student student){
        studentArrayList.add(student);
    }

    public void deleteStudent(int number){
        studentArrayList.remove(number);
    }

    public void deleteStudent(Student student){
        studentArrayList.remove(student);
    }

    public void write (Writer out) {
        PrintWriter p = new PrintWriter(out);
        p.println(groupID);
        for (Student stud: studentArrayList) {
            stud.write(out);
        }
        //System.out.println("Writing done");
    }

    public void read (Reader in){   //не фурычит
        StreamTokenizer in1 = new StreamTokenizer(in);
        Student stud;
        try {
            in1.nextToken();
            groupID = (int)in1.nval;
        while (in1.nextToken() != StreamTokenizer.TT_EOF){
            stud = new Student();
            stud.read(in1);
            studentArrayList.add(stud);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sortStudentList(){
        studentArrayList.sort(new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                return s1.getName().compareTo(s2.getName());
            }
        });
    }

    @Override
    public String toString() {
        return "StudentList{" +
                "groupID=" + groupID +
                ", studentArrayList=" + studentArrayList +
                '}';
    }
}
