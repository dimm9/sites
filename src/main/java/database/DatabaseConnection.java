package database;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void connect(Path path){
        try{
            connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", path.toString()));
            System.out.println("Connected");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    //disconnect == connection.close
    public void disconnect() throws SQLException {
        if(!connection.isClosed() && connection != null){
            connection.close();
        }
        System.out.println("Disconnected");
    }
}
