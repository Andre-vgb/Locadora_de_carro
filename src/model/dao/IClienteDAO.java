package model.dao;

import model.Cliente;

import java.util.ArrayList;

public interface IClienteDAO {

    // Create
    String cadastrarCliente(String tabela, Cliente novoCliente);

    // Read
    ArrayList<Cliente> selecionarTodos(String tabela);
    Cliente selecionarPorId(String tabela, int id);
    ArrayList<Cliente> buscarPorNome(String tabela, String nome);
    ArrayList<Cliente> buscarPorTelefone(String tabela, String telefone);

    // Update
    String atualizarCliente(String tabela, int id, Cliente dadosAtualizados);

    // Delete
    String deletarCliente(String tabela, int id);
}