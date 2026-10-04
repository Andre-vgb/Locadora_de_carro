package model;

public class Usuario {

    //atributos
    private int id;
    private String nome;
    private String sobrenome;
    private String login;
    private String senha;
    private String tipo; // "admin" ou "comum"

    //Construtor completo (mesma ideia Cliente.java)
    public Usuario(int id, String nome, String sobrenome, String login, String senha, String tipo) {
        this.id = id;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.login = login;
        this.senha = senha;
        this.tipo = tipo;
    }

    // Construtor sem id (mesma ideia Cliente.java)
    public Usuario(String nome, String sobrenome, String login, String senha, String tipo) {
        this.nome = nome;
        this.sobrenome = sobrenome;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
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
                ", nome='" + nome + '\'' +
                ", sobrenome='" + sobrenome + '\'' +
                ", login='" + login + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}