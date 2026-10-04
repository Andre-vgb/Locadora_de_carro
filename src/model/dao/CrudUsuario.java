package model.dao;

import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CrudUsuario implements IUsuarioDAO {

    //Atributo da conexao
    private Connection conn;

    //construtor
    public CrudUsuario(Connection conn) {
        this.conn = conn;
    }

    //Aqui vai os comandos para o banco (?,?,? é os espaços a serem preenchidos apos colocar os dados do usuario)
    @Override
    public String cadastrarUsuario(String tabela, Usuario novoUsuario) {
        String sql = "INSERT INTO " + tabela + " (login, senha, tipo) VALUES (?, ?, ?)";

        // pega o sql e prepara para ser enviado, gerando o ps.(o try faz o ps fechar sozinho)
        //para que nao fique um recurso aberto no banco
        //try é o bloco que tenta executar e se der erro no banco o java pula pro catch
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, novoUsuario.getLogin());
            ps.setString(2, novoUsuario.getSenha());
            ps.setString(3, novoUsuario.getTipo());

            ps.executeUpdate();
            return "Usuario cadastrado com sucesso!";

            // se qualquer coisa der errado o java vem pra ca
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                return "Erro: esse login ja esta em uso.";
            }
            return "Erro ao cadastrar usuario: " + e.getMessage();
        }
    }

    //lista todos os usuarios do banco em ordem alfabetica
    @Override
    public ArrayList<Usuario> selecionarTodos(String tabela) {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM " + tabela + " ORDER BY login";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {    // executeQuery é usado no SELECT e traz os dados para (rs)

            while (rs.next()) {     //pula pra proxima linha e quando acaba o while para
                usuarios.add(montarUsuario(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar usuarios: " + e.getMessage());
        }

        return usuarios;
    }

    @Override
    public Usuario selecionarPorId(String tabela, int id) {
        String sql = "SELECT * FROM " + tabela + " WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return montarUsuario(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuario por id: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean loginExiste(String tabela, String login) {
        String sql = "SELECT 1 FROM " + tabela + " WHERE login = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("Erro ao verificar login: " + e.getMessage());
        }

        return false;
    }

    @Override
    public Usuario validarLogin(String tabela, String login, String senha) {
        String sql = "SELECT * FROM " + tabela + " WHERE login = ? AND senha = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, senha);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return montarUsuario(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao validar login: " + e.getMessage());
        }

        return null;
    }

    @Override
    public String atualizarUsuario(String tabela, int id, Usuario dadosAtualizados) {
        String sql = "UPDATE " + tabela + " SET login = ?, senha = ?, tipo = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dadosAtualizados.getLogin());
            ps.setString(2, dadosAtualizados.getSenha());
            ps.setString(3, dadosAtualizados.getTipo());
            ps.setInt(4, id);

            int linhasAfetadas = ps.executeUpdate();
            if (linhasAfetadas == 0) {
                return "Usuario nao encontrado!";
            }
            return "Usuario atualizado com sucesso!";

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                return "Erro: esse login ja esta em uso.";
            }
            return "Erro ao atualizar usuario: " + e.getMessage();
        }
    }

    @Override
    public String deletarUsuario(String tabela, int id) {
        Usuario u = selecionarPorId(tabela, id);
        if (u == null) {
            return "Usuario nao encontrado!";
        }

        String sql = "DELETE FROM " + tabela + " WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return "Usuario " + u.getLogin() + " excluido com sucesso!";

        } catch (SQLException e) {
            return "Erro ao excluir usuario: " + e.getMessage();
        }
    }

    private Usuario montarUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("login"),
                rs.getString("senha"),
                rs.getString("tipo")
        );
    }
}