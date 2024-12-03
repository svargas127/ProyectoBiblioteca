import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    private static final String URL = "jdbc:mysql://localhost:3307/biblioteca";
    private static final String USER = "root"; // Cambiar si es necesario
    private static final String PASSWORD = ""; // Cambiar si es necesario

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}