package Model.Remote;

import Model.Client.StudentList;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface StudentService extends Remote {

    public StudentList sortStudentList(StudentList sl) throws RemoteException;

}
