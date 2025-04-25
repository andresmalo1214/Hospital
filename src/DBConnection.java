import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/Hospital";
    private static final String USER = "root"; // Cambia este valor si tu usuario es diferente
    private static final String PASSWORD = "1234"; // Cambia por tu contraseña de MySQL

    public static Connection getConnection() throws SQLException {
        try {
            // Establecer conexión con la base de datos
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            throw e;
        }
    }
}


