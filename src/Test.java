import Control.Connector;
import Exceptions.NoElementInBase;
import Exceptions.SameIndex;
import Exceptions.WrongAveragePoint;
import Model.Client.Student;
import Model.Client.StudentList;
import Model.Client.Teacher;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) {
        //CreateFiles();
        //ReadStudentsFiles("Group1.txt","Group2.txt","Group3.txt","Group4.txt","Group5.txt","Group6.txt","Group7.txt","Group8.txt","Group9.txt","Group10.txt");
        //ReadTeachersFiles("Teacher1.txt","Teacher2.txt","Teacher3.txt","Teacher4.txt","Teacher5.txt");

        Connector dbConnector = null;
        try{
            dbConnector = new Connector();
            dbConnector.createSchema();
            dbConnector.fillStudentsBaseFromFiles("Group1.txt","Group2.txt","Group3.txt","Group4.txt","Group5.txt","Group6.txt","Group7.txt","Group8.txt","Group9.txt","Group10.txt");
            dbConnector.fillTeachersBaseFromFiles("Teacher1.txt","Teacher2.txt","Teacher3.txt","Teacher4.txt","Teacher5.txt");
            dbConnector.addStudentAtBase(new Student("Kibitkina A S",101,"GragKib", 4.5), 6);
            Teacher teach = new Teacher();
            teach.setStudentLists(dbConnector.getGroupsByTeacherId(2));
            teach.setId(2);
            System.out.println(teach.getGroupsCount());
        }catch (SQLException | IOException | WrongAveragePoint e) {
            e.printStackTrace();
        } catch (SameIndex sameIndex) {
            sameIndex.printStackTrace();
        } catch (NoElementInBase noElementInBase) {
            noElementInBase.printStackTrace();
        }
    }
    public static void  CreateFiles(){
        File file;
        FileWriter fw;
        StudentList group ;
        StudentList group2 = null;
        Teacher teacher;
        try {
            int c=0;
            for(int n=0; n<5; ++n) {
                teacher = new Teacher("Teachername"+n+" Surname"+n+" Middlename"+n,"Login"+n,"Password"+n);
                for (int i = 0; i < 2; ++i, ++c) {
                    group = new StudentList();
                    for (int j = 0; j < 8; ++j) {
                        group.setStudent(new Student("Name" + c + "" + j + " Surname" + c + "" + j + " Middlename" + c + "" + j, 10 * c + j, "Graduate" + c + "" + j,
                                new BigDecimal(Math.random() * 5).setScale(2, RoundingMode.HALF_UP).doubleValue()));
                    }
                    file = new File("Group" + group.getGroupID() + ".txt");
                    fw = new FileWriter(file);
                    group.write(fw);
                    fw.close();

                    if(i==1) group2 = group;
                    if(i==0 && (n+1)%2==0) teacher.setStudentGroup(group2);
                    teacher.setStudentGroup(group);
                }

                file = new File("Teacher" + teacher.getId() + ".txt");
                fw = new FileWriter(file);
                teacher.write(fw);
                fw.close();
            }
        }catch (WrongAveragePoint | IOException e) {
            e.printStackTrace();
        }
    }

    public static void  ReadStudentsFiles(String ... students){
        File file;
        FileReader fr;
        StudentList studentList;
        try {
            for (int i = 0; i < students.length; ++i) {
                file = new File(students[i]);
                fr = new FileReader(file);
                studentList = new StudentList();
                studentList.read(fr);
                System.out.println(studentList.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void  ReadTeachersFiles(String ... teachers){
        File file;
        FileReader fr;
        Teacher teacher;
        try {
            for (int i = 0; i < teachers.length; ++i) {
                file = new File(teachers[i]);
                fr = new FileReader(file);
                teacher = new Teacher();
                teacher.read(fr);
                System.out.println(teacher.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
