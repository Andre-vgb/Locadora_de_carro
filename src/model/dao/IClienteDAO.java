package model.dao;

import model.Cliente;

import java.sql.SQLException;
import java.util.ArrayList;

public interface IClienteDAO {

    // create
    String cadastrarCliente(String tabela, Cliente novoCliente) throws SQLException;

    // read
    ArrayList<Cliente> selecionarTodos(String tabela) throws SQLException;
    Cliente selecionarPorId(String tabela, int id) throws SQLException;
    ArrayList<Cliente> buscarPorNome(String tabela, String nome) throws SQLException;
    ArrayList<Cliente> buscarPorTelefone(String tabela, String telefone) throws SQLException;

    // update
    String atualizarCliente(String tabela, int id, Cliente dadosAtualizados) throws SQLException;

    // delete
    String deletarCliente(String tabela, int id) throws SQLException;
}