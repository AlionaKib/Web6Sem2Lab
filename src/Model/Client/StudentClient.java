package Model.Client;

import Exceptions.WrongAveragePoint;
import Model.Client.Student;
import Model.Client.StudentList;
import Model.Remote.StudentService;

import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class StudentClient {

    String localhost    = "127.0.0.1";
    String RMI_HOSTNAME = "java.rmi.server.hostname";
    String SERVICE_PATH = "rmi://localhost/StudentService";

    StudentList sl;

    public StudentClient()
    {
        try {
            System.setProperty(RMI_HOSTNAME, localhost);
            // URL удаленного объекта
            String objectName = SERVICE_PATH;

            StudentService ss;
            ss = (StudentService) Naming.lookup(objectName);

            Scanner sc = new Scanner(System.in);
            System.out.println("Введите имя исходного файла");
            String fileName = sc.nextLine();

            File file = new File(fileName);

            FileReader fr = new FileReader(file);
            this.sl = new StudentList();
            this.sl.readStudent(fr);
            //System.out.println(this.sl.toString());
            StudentList sl2 = ss.sortStudentList(this.sl);
            //System.out.println(sl2.toString());
            this.sl = sl2;

            System.out.println("Введите имя нового файла");
            fileName = sc.nextLine();
            file = new File(fileName);

            FileWriter fw = new FileWriter(file);
            sl.writeStudent(fw);
            fw.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            System.err.println("NotBoundException : " +
                    e.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /*public static void main(String[] args) {
            new StudentClient();
            System.exit(0);
    }*/
}
