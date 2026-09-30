package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexao {
    private Connection Conn;
    private Statement s;

    public Connection getConn() {
        return Conn;
    }

    public Statement getS() {
        return s;
    }

    public void connDB(String db){
        try {
            String URL = "jdbc:postgresql://localhost:5432/"+db;
            String user = "postgres";
            String pwd = "1234";

            /*Conectando ao Servidor de Banco de Dados*/
            Class.forName("org.postgresql.Driver");
            this.Conn = DriverManager.getConnection(URL, user, pwd);
            this.s = this.Conn.createStatement();

            System.out.println("BD Conectado");
        }catch(SQLException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
