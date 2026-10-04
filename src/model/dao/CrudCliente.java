package model.dao;

import model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CrudCliente implements IClienteDAO {

    //Atributo da conexao
    private Connection conn;

    //construtor
    public CrudCliente(Connection conn) {
        this.conn = conn;
    }

    //Aqui vai os comandos para o banco (?,?,?,? é os espaços a serem preenchidos apos colocar os dados do cliente)
    @Override
    public String cadastrarCliente(String tabela, Cliente novoCliente) {
        String sql = "INSERT INTO " + tabela + " (nome, cpf, telefone, email) VALUES (?, ?, ?, ?)";

        // pega o sql e prepara para ser enviado, gerando o ps.(o try faz o ps fechar sozinho)
        //para que nao fique um recurso aberto no banco
        //try é o bloco que tenta executar e se der erro no banco o java pula pro catch
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, novoCliente.getNome());
            ps.setString(2, novoCliente.getCpf());
            ps.setString(3, novoCliente.getTelefone());
            ps.setString(4, novoCliente.getEmail());

            ps.executeUpdate();
            return "Cliente cadastrado com sucesso!";

            // se qualquer coisa der errado o java vem pra ca
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                return "Erro: ja existe um cliente com esse CPF.";
            }
            return "Erro ao cadastrar cliente: " + e.getMessage();
        }
    }

    //lista todos os clientes do banco em ordem alfabetica
    @Override
    public ArrayList<Cliente> selecionarTodos(String tabela) {
        ArrayList<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM " + tabela + " ORDER BY nome";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {    // executeQuery é usado no SELECT e traz os dados para (rs)

            while (rs.next()) {     //pula pra proxima linha e quando acaba o while para
                clientes.add(montarCliente(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar clientes: " + e.getMessage());
        }

        return clientes;
    }

    @Override
    public Cliente selecionarPorId(String tabela, int id) {
        String sql = "SELECT * FROM " + tabela + " WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return montarCliente(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar cliente por id: " + e.getMessage());
        }

        return null;
    }

    @Override
    public ArrayList<Cliente> buscarPorNome(String tabela, String nome) {
        ArrayList<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM " + tabela + " WHERE nome ILIKE ? ORDER BY nome";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nome + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clientes.add(montarCliente(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar cliente por nome: " + e.getMessage());
        }

        return clientes;
    }

    @Override
    public ArrayList<Cliente> buscarPorTelefone(String tabela, String telefone) {
        ArrayList<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM " + tabela + " WHERE telefone = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, telefone);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clientes.add(montarCliente(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar cliente por telefone: " + e.getMessage());
        }

        return clientes;
    }

    @Override
    public String atualizarCliente(String tabela, int id, Cliente dadosAtualizados) {
        String sql = "UPDATE " + tabela + " SET nome = ?, cpf = ?, telefone = ?, email = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dadosAtualizados.getNome());
            ps.setString(2, dadosAtualizados.getCpf());
            ps.setString(3, dadosAtualizados.getTelefone());
            ps.setString(4, dadosAtualizados.getEmail());
            ps.setInt(5, id);

            int linhasAfetadas = ps.executeUpdate();
            if (linhasAfetadas == 0) {
                return "Cliente nao encontrado!";
            }
            return "Cliente atualizado com sucesso!";

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                return "Erro: ja existe outro cliente com esse CPF.";
            }
            return "Erro ao atualizar cliente: " + e.getMessage();
        }
    }

    @Override
    public String deletarCliente(String tabela, int id) {
        Cliente c = selecionarPorId(tabela, id);
        if (c == null) {
            return "Cliente nao encontrado!";
        }

        String sql = "DELETE FROM " + tabela + " WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return "Cliente " + c.getNome() + " excluido com sucesso!";

        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                return "Nao e possivel excluir: o cliente possui locacoes registradas.";
            }
            return "Erro ao excluir cliente: " + e.getMessage();
        }
    }

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