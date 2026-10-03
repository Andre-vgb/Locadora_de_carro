package model.dao;

public class Carro {

    private String placa;
    private String marca;
    private String modelo;
    private String cor;
    private String status;
    private double valorDiaria;
    private int ano;
    private String caminhoImagem;

    public Carro(int ano, String cor, String marca, String modelo, String placa, String status, double valor_diaria, String caminhoImagem) {
        this.ano = ano;
        this.cor = cor;
        this.marca = marca;
        this.modelo = modelo;
        this.placa = placa;
        this.status = status;
        this.valorDiaria = valor_diaria;
        this.caminhoImagem = caminhoImagem;
    }

    public Carro(){

    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getValorDiaria() {
        return valorDiaria;
    }

    public void setValorDiaria(double valorDiaria) {
        this.valorDiaria = valorDiaria;
    }

    public String getCaminhoImagem() {
        return caminhoImagem;
    }

    public void setCaminhoImagem(String caminhoImagem) {
        this.caminhoImagem = caminhoImagem;
    }
    @Override
    public String toString() {
        return "Carro{" +
                "placa='" + placa + '\'' +
                ", modelo='" + modelo + '\'' +
                ", ano=" + ano +
                ", cor='" + cor + '\'' +
                ", status='" + status + '\'' +
                ", valorDiaria=" + valorDiaria +
                ", caminhoImagem='" + caminhoImagem + '\'' +
                '}';
    }
}
