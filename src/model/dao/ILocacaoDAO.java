package model.dao;

import model.Locacao;

import java.util.ArrayList;

public interface ILocacaoDAO {

    // Create
    String inserirLocacao(String tabela, Locacao novaLocacao);

    // Read
    ArrayList<Locacao> selecionarTodos(String tabela);
    Locacao selecionarPorId(String tabela, int id);
    ArrayList<Locacao> buscarPorCliente(String tabela, int idCliente);
    ArrayList<Locacao> filtrarPorCarro(String tabela, String placaCarro);
    ArrayList<Locacao> selecionarEmAberto(String tabela);

    // Update
    String atualizarLocacao(String tabela, int id, Locacao dadosAtualizados);
    String registrarDevolucao(String tabela, int id, java.sql.Date dataDevolucao);

    // Delete
    String deletarLocacao(String tabela, int id);
}
