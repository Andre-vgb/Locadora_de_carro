package model;

public class Usuario {

            //atributos
    private int id;
    private String login;
    private String senha;
    private String tipo; // "admin" ou "comum"

            //Construtor completo (mesma ideia Cliente.java)
    public Usuario(int id, String login, String senha, String tipo) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.tipo = tipo;
    }

    // Construtor sem id (mesma ideia Cliente.java)
    public Usuario(String login, String senha, String tipo) {
        this.login = login;
        this.senha = senha;
        this.tipo = tipo;
    }

    // Construtor vazio
    public Usuario() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    // A senha fica de fora pra nao aparecer em print ou log
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}