package auth;

import database.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountManager {
    DatabaseConnection connection;

    public AccountManager(DatabaseConnection connection) {
        this.connection = connection;
    }

    public void init(){
        String sqlInit = "CREATE TABLE IF NOT EXISTS accounts( " +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "login TEXT NOT NULL," +
                "password TEXT NOT NULL)";
        try {
            PreparedStatement statement = connection.getConnection().prepareStatement(sqlInit);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean register(String login, String password){
        String query = "INSERT INTO accounts (login, password) VALUES (?, ?)";
        try {
            PreparedStatement statement  = connection.getConnection().prepareStatement(query);
            statement.setString(1, login);
            String passwordHashed = BCrypt.hashpw(password, BCrypt.gensalt());
            statement.setString(2, passwordHashed);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean authenticate(String login, String password){
        String query = "SELECT password FROM accounts WHERE login = ?";
        try {
            PreparedStatement statement = connection.getConnection().prepareStatement(query);
            statement.setString(1, login);
            ResultSet results = statement.executeQuery();
            if(results.next()){
                String orgPassword = results.getString("password");
                return BCrypt.checkpw(password, orgPassword);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    public Account getAccount(String login){
        String query = "SELECT id, login FROM accounts WHERE login = ?";
        try {
            PreparedStatement statement = connection.getConnection().prepareStatement(query);
            statement.setString(1, login);
            ResultSet result = statement.getResultSet();
            if(result.next()){
                int id = result.getInt("id");
                String newLogin = result.getString("login");
                return new Account(id, newLogin);
            }else{
                return null;
            }
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
        }
        return null;
    }
}
