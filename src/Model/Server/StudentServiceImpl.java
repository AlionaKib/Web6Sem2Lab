package Model.Server;

import Model.Remote.StudentService;
import Model.Client.StudentList;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class StudentServiceImpl extends UnicastRemoteObject implements StudentService {

    protected StudentServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public StudentList sortStudentList(StudentList studentList) throws RemoteException {
        studentList.sortStudentList();
        return studentList;
    }

    public static void main (String[] args) throws Exception
    {
        String localhost    = "127.0.0.1";
        String RMI_HOSTNAME = "java.rmi.server.hostname";
        try {
            System.setProperty(RMI_HOSTNAME, localhost);
            // Создание удаленного RMI объекта
            StudentService service = new StudentServiceImpl();

            // Определение имени удаленного RMI объекта
            String serviceName = "StudentService";

            System.out.println("Initializing " + serviceName);

            /*
             * Регистрация удаленного RMI объекта BillingService
             * в реестре rmiregistry
             */
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind(serviceName, service);
            System.out.println("Start " + serviceName);
        } catch (RemoteException e) {
            System.err.println("RemoteException : "+e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Exception : " + e.getMessage());
            System.exit(2);
        }
    }
}
