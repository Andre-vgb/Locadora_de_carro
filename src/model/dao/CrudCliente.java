package model.dao;

import model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CrudCliente {

    //Atributo da conexao
    private Connection conn;

    //construtor
    public CrudCliente(Connection conn) {
        this.conn = conn;
    }

    //Aqui vai os comandos para o banco (?,?,?,? é os espaços a serem preenchidos apos colocar os dados do cliente)
    public boolean cadastrarCliente(String tabela, Cliente cliente) {
        String sql = "INSERT INTO " + tabela + " (nome, cpf, telefone, email) VALUES (?, ?, ?, ?)";


        // pega o sql e prepara para ser enviado, gerando o ps.(o try faz o ps fechar sozinho)
        //para que nao fique um recurso aberto no banco
        //try é o bloco que tenta executar e se der erro no banco o java pula pro catch
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getCpf());
            ps.setString(3, cliente.getTelefone());
            ps.setString(4, cliente.getEmail());

            ps.executeUpdate();
            return true;

            // se qualquer coisa der errado o java vem pra ca
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar cliente: " + e.getMessage());
            return false;
        }
    }

        //lista todos os clientes do banco em ordem alfabetica
    public List<Cliente> selecionarTodos(String tabela) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM " + tabela + " ORDER BY nome";


        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {    // executeQuery é usado no SELECT e traz os dados para (rs)

            while (rs.next()) {     //pula pra proxima linha e quando acaba o while para
                Cliente c = new Cliente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("telefone"),
                        rs.getString("email")
                );
                clientes.add(c);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar clientes: " + e.getMessage());
        }

        return clientes;
    }

    // falta, buscarPorNome, buscarPorTelefone, atualizarCliente, deletarCliente
}