package model.dao;

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

    public void connDB(){
        try {

            String user = "postgres";
            String pwd = "unifebe123@";
            String URL = "jdbc:postgresql://aws-1-us-west-2.pooler.supabase.com:6543/postgres?user=postgres.pzaclymyivfwhmafmleh&password=unifebe123@";



            /*Conectando ao Servidor de Banco de Dados*/
            Class.forName("org.postgresql.Driver");
            this.Conn = DriverManager.getConnection(URL, user, pwd);
            this.s = this.Conn.createStatement();

            System.out.println("Supabase conectado!!");

        }catch(SQLException e) {
            System.out.println("Erro ao conectar ao Supabase:");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "Driver PostgresSQL não encontrado.",e);
        }
    }

}
