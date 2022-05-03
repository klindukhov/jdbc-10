package assignment10;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    private static Connection connection;

    public static Connection getConnection(){
        if(connection == null) {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
                        "postgres", "Password");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }



}
