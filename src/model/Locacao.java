package model;

import java.sql.Date;

public class Locacao {

    private int id;
    private String placaCarro;
    private int idCliente;
    private Date dataLocacao;
    private Date dataDevolucao;

    public Locacao(int id, String placaCarro, int idCliente, Date dataLocacao, Date dataDevolucao) {
        this.id = id;
        this.placaCarro = placaCarro;
        this.idCliente = idCliente;
        this.dataLocacao = dataLocacao;
        this.dataDevolucao = dataDevolucao;
    }

    public Locacao(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlacaCarro() {
        return placaCarro;
    }

    public void setPlacaCarro(String placaCarro) {
        this.placaCarro = placaCarro;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public Date getDataLocacao() {
        return dataLocacao;
    }

    public void setDataLocacao(Date dataLocacao) {
        this.dataLocacao = dataLocacao;
    }

    public Date getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(Date dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }
}