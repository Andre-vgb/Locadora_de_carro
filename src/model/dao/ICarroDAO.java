package model.dao;

import model.Carro;

import java.util.ArrayList;

public interface ICarroDAO {

    // Create
    String inserirCarro(String tabela, Carro novoCarro);

    // Read
    ArrayList<Carro> selecionarTodos(String tabela);
    Carro selecionarPlaca(String tabela, String placa);
    ArrayList<Carro> buscarPorModelo(String tabela, String modelo);
    ArrayList<Carro> filtrarPorStatus(String tabela, String status);

    // Update
    String atualizarCarro(String tabela, String placa, Carro dadosAtualizados);
    String atualizarStatus(String tabela, String placa, String novoStatus);
    String atualizarImagem(String tabela, String placa, String caminhoImagem);

    // Delete
    String deletarCarro(String tabela, String placa);
}