//a ideia desse controller é conferir tudo antes de chegar no CRUD, se algo der errado ele para aq
// e nao deixa nem chegar ao banco (que é oq o CRUD comanda)
package controller;

import model.Usuario;
import model.dao.Conexao;
import model.dao.CrudUsuario;

import java.sql.Connection;
import java.util.ArrayList;

public class UsuarioController {
    private CrudUsuario crudUsuario;
    private String tabela = "usuario";

    public UsuarioController() {
    }

    public boolean conectaBD(String db) {
        try {
            Conexao conexao = new Conexao();
            conexao.connDB(db);
            Connection conn = conexao.getConn();

            if (conn == null) {
                return false;
            }

            crudUsuario = new CrudUsuario(conn);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

        //uma lista de conferencia (Se aprovar tudo ele passa pro CRUD)
    public String cadastrarUsuario(Usuario novoUsuario, String confirmacaoSenha) {
        try {
            if (novoUsuario.getNome() == null || novoUsuario.getNome().isBlank()) {
                return "O nome e obrigatorio.";
            }
            if (novoUsuario.getSobrenome() == null || novoUsuario.getSobrenome().isBlank()) {
                return "O sobrenome e obrigatorio.";
            }
            if (novoUsuario.getLogin() == null || novoUsuario.getLogin().isBlank()) {
                return "O login e obrigatorio.";
            }
            if (novoUsuario.getSenha() == null || novoUsuario.getSenha().isBlank()) {
                return "A senha e obrigatoria.";
            }
            if (!novoUsuario.getSenha().equals(confirmacaoSenha)) {
                return "As senhas nao conferem.";
            }
            if (!"admin".equals(novoUsuario.getTipo()) && !"comum".equals(novoUsuario.getTipo())) {
                return "O tipo do usuario deve ser 'admin' ou 'comum'.";
            }
            if (this.crudUsuario.loginExiste(this.tabela, novoUsuario.getLogin())) {
                return "Esse login ja esta em uso. Escolha outro.";
            }
            return this.crudUsuario.cadastrarUsuario(this.tabela, novoUsuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao cadastrar usuario!";
    }


    //confere se a pessoa pode entrar (LOGIN)
    public Usuario fazerLogin(String login, String senha, String tipoEsperado) {
        try {
            if (login == null || login.isBlank() || senha == null || senha.isBlank()) {
                return null;
            }

            Usuario usuario = this.crudUsuario.validarLogin(this.tabela, login, senha);

            if (usuario == null) {
                return null;
            }
            if (!usuario.getTipo().equals(tipoEsperado)) {
                return null;
            }
            return usuario;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //SO ADMIN PODE VER ESSA LISTA, SE NAO FOR ELE DEVOLVE UMA LISTA VAZIA
    public ArrayList<Usuario> listarUsuarios(boolean usuarioEhAdministrador) {
        if (!usuarioEhAdministrador) {
            return new ArrayList<>();
        }
        try {
            return this.crudUsuario.selecionarTodos(this.tabela);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //nao confere nada, apenas passa para o usuario
    public Usuario buscarPorId(int id) {
        try {
            return this.crudUsuario.selecionarPorId(this.tabela, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //confere antes de atualizar o usuario, se der certo chama o atualizar do CRUD (se nao der ele avisa)
    public String editarUsuario(int id, Usuario dadosAtualizados) {
        try {
            if (dadosAtualizados.getLogin() == null || dadosAtualizados.getLogin().isBlank()) {
                return "O login e obrigatorio.";
            }
            if (dadosAtualizados.getSenha() == null || dadosAtualizados.getSenha().isBlank()) {
                return "A senha e obrigatoria.";
            }
            if (!"admin".equals(dadosAtualizados.getTipo()) && !"comum".equals(dadosAtualizados.getTipo())) {
                return "O tipo do usuario deve ser 'admin' ou 'comum'.";
            }
            return this.crudUsuario.atualizarUsuario(this.tabela, id, dadosAtualizados);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao editar usuario!";
    }

    //SO ADMIN PODE EXCLUIR (SE FOR CHAMA O DELETAR DO CRUD)
    public String excluirUsuario(int id, boolean usuarioEhAdministrador) {
        if (!usuarioEhAdministrador) {
            return "Apenas administradores podem excluir usuarios.";
        }
        try {
            return this.crudUsuario.deletarUsuario(this.tabela, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao excluir usuario!";
    }
}