package Model.Client;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

public class StudentList implements Serializable {
    static int UNIQUE_ID = 0;
    private int groupID = ++UNIQUE_ID;
    private ArrayList<Student> studentArrayList;

    public StudentList() {
        studentArrayList = new ArrayList<Student>();
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public void setStudentArrayList(ArrayList<Student> studentArrayList) {
        this.studentArrayList = studentArrayList;
    }

    public ArrayList<Student> getStudentArrayList(){
        return this.studentArrayList;
    }

    public int getStudentsCount() {
        return studentArrayList.size();
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
