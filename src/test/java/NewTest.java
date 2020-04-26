import control.DBConnect;
import model.exception.NoElementInBase;

import java.sql.SQLException;

public class NewTest {
    public static void main(String[] args) {
        try {
            System.out.println(new DBConnect().checkTeacherExistence("Login0","Password0"));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
