package model.dao;

import model.Cliente;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CrudCliente implements IClienteDAO {

    // conexao com o banco
    private Connection conn;

    public CrudCliente(Connection conn) {
        this.conn = conn;
    }

    // CREATE: insere um cliente novo na tabela
    @Override
    public String cadastrarCliente(String tabela, Cliente novoCliente) throws SQLException {
        String sql = "INSERT INTO " + tabela + " (nome, cpf, telefone, email) VALUES ('"
                + novoCliente.getNome() + "', '"
                + novoCliente.getCpf() + "', '"
                + novoCliente.getTelefone() + "', '"
                + novoCliente.getEmail() + "')";

        Statement s = conn.createStatement();
        s.executeUpdate(sql);
        s.close();
        return "Cliente cadastrado com sucesso!";
    }

    // READ: lista todos os clientes, em ordem de nome
    @Override
    public ArrayList<Cliente> selecionarTodos(String tabela) throws SQLException {
        ArrayList<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM " + tabela + " ORDER BY nome";

        Statement s = conn.createStatement();
        ResultSet rs = s.executeQuery(sql);
        while (rs.next()) {
            clientes.add(montarCliente(rs));
        }
        rs.close();
        s.close();
        return clientes;
    }

    // READ: busca um cliente pelo id
    @Override
    public Cliente selecionarPorId(String tabela, int id) throws SQLException {
        String sql = "SELECT * FROM " + tabela + " WHERE id = " + id;

        Statement s = conn.createStatement();
        ResultSet rs = s.executeQuery(sql);
        Cliente cliente = null;
        if (rs.next()) {
            cliente = montarCliente(rs);
        }
        rs.close();
        s.close();
        return cliente;
    }

    // READ: busca clientes cujo nome contem o texto digitado
    @Override
    public ArrayList<Cliente> buscarPorNome(String tabela, String nome) throws SQLException {
        ArrayList<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM " + tabela + " WHERE nome ILIKE '%" + nome + "%' ORDER BY nome";

        Statement s = conn.createStatement();
        ResultSet rs = s.executeQuery(sql);
        while (rs.next()) {
            clientes.add(montarCliente(rs));
        }
        rs.close();
        s.close();
        return clientes;
    }

    // READ: busca clientes pelo telefone
    @Override
    public ArrayList<Cliente> buscarPorTelefone(String tabela, String telefone) throws SQLException {
        ArrayList<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM " + tabela + " WHERE telefone = '" + telefone + "'";

        Statement s = conn.createStatement();
        ResultSet rs = s.executeQuery(sql);
        while (rs.next()) {
            clientes.add(montarCliente(rs));
        }
        rs.close();
        s.close();
        return clientes;
    }

    // UPDATE: altera os dados de um cliente pelo id
    @Override
    public String atualizarCliente(String tabela, int id, Cliente dados) throws SQLException {
        String sql = "UPDATE " + tabela + " SET nome = '" + dados.getNome()
                + "', cpf = '" + dados.getCpf()
                + "', telefone = '" + dados.getTelefone()
                + "', email = '" + dados.getEmail()
                + "' WHERE id = " + id;

        Statement s = conn.createStatement();
        int linhas = s.executeUpdate(sql);
        s.close();

        // linhas = 0 quer dizer que nenhum cliente tem esse id
        if (linhas == 0) {
            return "Cliente nao encontrado!";
        }
        return "Cliente atualizado com sucesso!";
    }

    // DELETE: apaga o cliente pelo id
    // se o cliente tiver locacao, o banco nao deixa apagar e da erro
    @Override
    public String deletarCliente(String tabela, int id) throws SQLException {
        Cliente c = selecionarPorId(tabela, id);
        if (c == null) {
            return "Cliente nao encontrado!";
        }

        String sql = "DELETE FROM " + tabela + " WHERE id = " + id;
        Statement s = conn.createStatement();
        s.executeUpdate(sql);
        s.close();
        return "Cliente " + c.getNome() + " excluido com sucesso!";
    }

    // metodo auxiliar: pega a linha atual do resultado e monta um Cliente
    private Cliente montarCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("cpf"),
                rs.getString("telefone"),
                rs.getString("email")
        );
    }
}