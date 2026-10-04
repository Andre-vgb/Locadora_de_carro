package model.dao;

import model.Usuario;

import java.util.ArrayList;

public interface IUsuarioDAO {

        //create
    String cadastrarUsuario(String tabela, Usuario novoUsuario);

        //read
    ArrayList<Usuario> selecionarTodos(String tabela);
    Usuario selecionarPorId(String tabela, int id);
    boolean loginExiste(String tabela, String login);
    Usuario validarLogin(String tabela, String login, String senha);

        //update
    String atualizarUsuario(String tabela, int id, Usuario dadosAtualizados);

        //delete
    String deletarUsuario(String tabela, int id);
}