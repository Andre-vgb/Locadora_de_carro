package view;//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import model.dao.Conexao;

public class Main {
    public static void main(String[] args) {
        Conexao conn = new Conexao();
        conn.connDB("locadora");
    }
}