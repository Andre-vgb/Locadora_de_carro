package model.dao;

import model.Usuario;

import java.sql.SQLException;
import java.util.ArrayList;

public interface IUsuarioDAO {

    // create
    String cadastrarUsuario(String tabela, Usuario novoUsuario) throws SQLException;

    // read
    ArrayList<Usuario> selecionarTodos(String tabela) throws SQLException;
    Usuario selecionarPorId(String tabela, int id) throws SQLException;
    boolean loginExiste(String tabela, String login) throws SQLException;
    Usuario validarLogin(String tabela, String login, String senha) throws SQLException;

    // update
    String atualizarUsuario(String tabela, int id, Usuario dadosAtualizados) throws SQLException;

    // delete
    String deletarUsuario(String tabela, int id) throws SQLException;
}