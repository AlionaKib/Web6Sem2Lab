import Control.Connector;

import java.sql.SQLException;

public class Test {
    public static void main(String[] args) {
        Connector dbConnector = null;
        try{
            dbConnector = new Connector();
            dbConnector.createSchema();
        }catch (SQLException e) {
            //System.out.println(e);
        }
    }
}
