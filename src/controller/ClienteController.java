package controller;

import model.Cliente;
import model.dao.Conexao;
import model.dao.CrudCliente;

import java.sql.Connection;
import java.util.ArrayList;

public class ClienteController {
    private CrudCliente crudCliente;
    private String tabela = "cliente";

    public ClienteController() {
    }

    public boolean conectaBD(String db) {
        try {
            Conexao conexao = new Conexao();
            conexao.connDB(db);
            Connection conn = conexao.getConn();

            if (conn == null) {
                return false;
            }

            crudCliente = new CrudCliente(conn);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String cadastrarCliente(Cliente novoCliente) {
        try {
            if (novoCliente.getNome() == null || novoCliente.getNome().isBlank()) {
                return "O nome do cliente e obrigatorio.";
            }
            if (novoCliente.getCpf() == null || novoCliente.getCpf().isBlank()) {
                return "O CPF do cliente e obrigatorio.";
            }
            return this.crudCliente.cadastrarCliente(this.tabela, novoCliente);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao cadastrar cliente!";
    }

    public ArrayList<Cliente> listarClientes() {
        try {
            return this.crudCliente.selecionarTodos(this.tabela);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Cliente buscarPorId(int id) {
        try {
            return this.crudCliente.selecionarPorId(this.tabela, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Cliente> buscarPorNome(String nome) {
        try {
            return this.crudCliente.buscarPorNome(this.tabela, nome);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Cliente> buscarPorTelefone(String telefone) {
        try {
            return this.crudCliente.buscarPorTelefone(this.tabela, telefone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String editarCliente(int id, Cliente dadosAtualizados) {
        try {
            if (dadosAtualizados.getNome() == null || dadosAtualizados.getNome().isBlank()) {
                return "O nome do cliente e obrigatorio.";
            }
            if (dadosAtualizados.getCpf() == null || dadosAtualizados.getCpf().isBlank()) {
                return "O CPF do cliente e obrigatorio.";
            }
            return this.crudCliente.atualizarCliente(this.tabela, id, dadosAtualizados);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao editar cliente!";
    }

    public String excluirCliente(int id) {
        try {
            return this.crudCliente.deletarCliente(this.tabela, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao excluir cliente!";
    }
}