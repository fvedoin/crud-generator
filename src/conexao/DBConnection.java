package conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
public abstract class DBConnection implements Connection {
    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/trabalho01", "root", "");
        } catch (SQLException e){
            System.out.println("Erro - conexão"+e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Erro - Driver"+e.getMessage());
        }
        return con;
    }
}
