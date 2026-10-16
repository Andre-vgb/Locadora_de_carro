package model.dao;

import model.Usuario;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CrudUsuario implements IUsuarioDAO {

    // conexao com o banco
    private Connection conn;

    public CrudUsuario(Connection conn) {
        this.conn = conn;
    }

    // CREATE: insere um usuario novo na tabela
    @Override
    public String cadastrarUsuario(String tabela, Usuario novoUsuario) throws SQLException {
        String sql = "INSERT INTO " + tabela + " (nome, sobrenome, login, senha, tipo) VALUES ('"
                + novoUsuario.getNome() + "', '"
                + novoUsuario.getSobrenome() + "', '"
                + novoUsuario.getLogin() + "', '"
                + novoUsuario.getSenha() + "', '"
                + novoUsuario.getTipo() + "')";

        Statement s = conn.createStatement();
        s.executeUpdate(sql);
        s.close();
        return "Usuario cadastrado com sucesso!";
    }

    // READ: lista todos os usuarios
    @Override
    public ArrayList<Usuario> selecionarTodos(String tabela) throws SQLException {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM " + tabela + " ORDER BY login";

        Statement s = conn.createStatement();
        ResultSet rs = s.executeQuery(sql);
        while (rs.next()) {
            usuarios.add(montarUsuario(rs));
        }
        rs.close();
        s.close();
        return usuarios;
    }

    // READ: busca um usuario pelo id
    @Override
    public Usuario selecionarPorId(String tabela, int id) throws SQLException {
        String sql = "SELECT * FROM " + tabela + " WHERE id = " + id;

        Statement s = conn.createStatement();
        ResultSet rs = s.executeQuery(sql);
        Usuario usuario = null;
        if (rs.next()) {
            usuario = montarUsuario(rs);
        }
        rs.close();
        s.close();
        return usuario;
    }

    // READ: confere se ja existe um usuario com esse login
    @Override
    public boolean loginExiste(String tabela, String login) throws SQLException {
        String sql = "SELECT * FROM " + tabela + " WHERE login = '" + login + "'";

        Statement s = conn.createStatement();
        ResultSet rs = s.executeQuery(sql);
        boolean existe = rs.next();
        rs.close();
        s.close();
        return existe;
    }

    // READ: confere se o login e a senha estao certos
    @Override
    public Usuario validarLogin(String tabela, String login, String senha) throws SQLException {
        String sql = "SELECT * FROM " + tabela + " WHERE login = '" + login
                + "' AND senha = '" + senha + "'";

        Statement s = conn.createStatement();
        ResultSet rs = s.executeQuery(sql);
        Usuario usuario = null;
        if (rs.next()) {
            usuario = montarUsuario(rs);
        }
        rs.close();
        s.close();
        return usuario;
    }

    // UPDATE: altera os dados de um usuario pelo id
    @Override
    public String atualizarUsuario(String tabela, int id, Usuario dados) throws SQLException {
        String sql = "UPDATE " + tabela + " SET nome = '" + dados.getNome()
                + "', sobrenome = '" + dados.getSobrenome()
                + "', login = '" + dados.getLogin()
                + "', senha = '" + dados.getSenha()
                + "', tipo = '" + dados.getTipo()
                + "' WHERE id = " + id;

        Statement s = conn.createStatement();
        int linhas = s.executeUpdate(sql);
        s.close();

        // linhas = 0 quer dizer que nenhum usuario tem esse id
        if (linhas == 0) {
            return "Usuario nao encontrado!";
        }
        return "Usuario atualizado com sucesso!";
    }

    // DELETE: apaga o usuario pelo id
    @Override
    public String deletarUsuario(String tabela, int id) throws SQLException {
        Usuario u = selecionarPorId(tabela, id);
        if (u == null) {
            return "Usuario nao encontrado!";
        }

        String sql = "DELETE FROM " + tabela + " WHERE id = " + id;
        Statement s = conn.createStatement();
        s.executeUpdate(sql);
        s.close();
        return "Usuario " + u.getLogin() + " excluido com sucesso!";
    }

    // metodo auxiliar: pega a linha atual do resultado e monta um Usuario
    private Usuario montarUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("sobrenome"),
                rs.getString("login"),
                rs.getString("senha"),
                rs.getString("tipo")
        );
    }
}