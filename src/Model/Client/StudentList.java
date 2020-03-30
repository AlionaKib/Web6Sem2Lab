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

    public void writeStudent (Writer out){
        for (Student stud: studentArrayList) {
            stud.writeStudent(out);
        }
        //System.out.println("Writing done");
    }

    public void readStudent (Reader in){   //не фурычит
        StreamTokenizer in1 = new StreamTokenizer(in);
        Student stud;
        try {
        while (in1.nextToken() != StreamTokenizer.TT_EOF){
            stud = new Student();
            stud.readStudent(in1);
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
