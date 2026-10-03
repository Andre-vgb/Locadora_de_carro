package model.dao;

import model.Locacao;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class CrudLocacao implements ILocacaoDAO {
    private Statement s;

    public CrudLocacao(Statement s) {
        this.s = s;
    }

    // Create: insere uma nova locacao
    // data_devolucao normalmente comeca nula, pois o carro ainda nao foi devolvido
    @Override
    public String inserirLocacao(String tabela, Locacao novaLocacao){
        String valorDataDevolucao = (novaLocacao.getDataDevolucao() == null)
                ? "NULL"
                : "'" + novaLocacao.getDataDevolucao() + "'";

        String SQL = "INSERT INTO " + tabela + " (placa, id_cliente, data_locacao, data_devolucao) VALUES ('"
                + novaLocacao.getPlacaCarro() + "', " + novaLocacao.getIdCliente() + ", '"
                + novaLocacao.getDataLocacao() + "', " + valorDataDevolucao + ")";

        int linhasAfetadas = -1;
        try{
            linhasAfetadas = s.executeUpdate(SQL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Codigo retornado: " + linhasAfetadas;
    }

    // Read: retorna todas as locacoes cadastradas
    @Override
    public ArrayList<Locacao> selecionarTodos(String tabela){
        String SQL = "SELECT * FROM \"" + tabela + "\";";
        try {
            ResultSet rset = s.executeQuery(SQL);
            ArrayList<Locacao> lista = new ArrayList<>();
            while(rset.next()){
                lista.add(montarLocacao(rset));
            }
            return lista;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // Read: busca uma locacao especifica pela chave primaria (id)
    @Override
    public Locacao selecionarPorId(String tabela, int id){
        String SQL = "SELECT * FROM \"" + tabela + "\" WHERE id = " + id + ";";
        try {
            ResultSet rset = s.executeQuery(SQL);
            if(rset.next()){
                return montarLocacao(rset);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // Read: todas as locacoes de um cliente especifico
    @Override
    public ArrayList<Locacao> buscarPorCliente(String tabela, int idCliente){
        String SQL = "SELECT * FROM \"" + tabela + "\" WHERE id_cliente = " + idCliente + ";";
        try {
            ResultSet rset = s.executeQuery(SQL);
            ArrayList<Locacao> lista = new ArrayList<>();
            while(rset.next()){
                lista.add(montarLocacao(rset));
            }
            return lista;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // Read: todas as locacoes de um carro especifico (pela placa)
    @Override
    public ArrayList<Locacao> filtrarPorCarro(String tabela, String placaCarro){
        String SQL = "SELECT * FROM \"" + tabela + "\" WHERE placa = '" + placaCarro + "';";
        try {
            ResultSet rset = s.executeQuery(SQL);
            ArrayList<Locacao> lista = new ArrayList<>();
            while(rset.next()){
                lista.add(montarLocacao(rset));
            }
            return lista;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // Read: locacoes em aberto, ou seja, com data_devolucao ainda nula
    @Override
    public ArrayList<Locacao> selecionarEmAberto(String tabela){
        String SQL = "SELECT * FROM \"" + tabela + "\" WHERE data_devolucao IS NULL;";
        try {
            ResultSet rset = s.executeQuery(SQL);
            ArrayList<Locacao> lista = new ArrayList<>();
            while(rset.next()){
                lista.add(montarLocacao(rset));
            }
            return lista;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // Update: atualiza os dados gerais de uma locacao
    @Override
    public String atualizarLocacao(String tabela, int id, Locacao dadosAtualizados){
        String SQL = "UPDATE " + tabela + " SET placa='" + dadosAtualizados.getPlacaCarro()
                + "', id_cliente=" + dadosAtualizados.getIdCliente()
                + ", data_locacao='" + dadosAtualizados.getDataLocacao()
                + "' WHERE id = " + id + ";";

        int linhasAfetadas = 0;
        try{
            linhasAfetadas = s.executeUpdate(SQL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Linhas afetadas: " + linhasAfetadas;
    }

    // Update: atualiza so a data de devolucao (usado quando o carro e devolvido)
    // o calculo do valor total e a liberacao do carro ficam a cargo do controller
    @Override
    public String registrarDevolucao(String tabela, int id, Date dataDevolucao){
        String SQL = "UPDATE " + tabela + " SET data_devolucao='" + dataDevolucao
                + "' WHERE id = " + id + ";";

        int linhasAfetadas = 0;
        try{
            linhasAfetadas = s.executeUpdate(SQL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Linhas afetadas: " + linhasAfetadas;
    }

    // Delete: remove uma locacao do banco
    @Override
    public String deletarLocacao(String tabela, int id){
        String SQL = "DELETE FROM " + tabela + " WHERE id = " + id + ";";
        int codigoRetornado = -1;
        try{
            codigoRetornado = s.executeUpdate(SQL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Codigo retornado: " + codigoRetornado;
    }

    // metodo auxiliar interno, nao faz parte do CRUD (por isso nao tem @Override)
    // monta um objeto Locacao a partir de uma linha do ResultSet
    private Locacao montarLocacao(ResultSet rset) throws Exception {
        Locacao l = new Locacao();
        l.setId(rset.getInt("id"));
        l.setPlacaCarro(rset.getString("placa"));
        l.setIdCliente(rset.getInt("id_cliente"));
        l.setDataLocacao(rset.getDate("data_locacao"));
        l.setDataDevolucao(rset.getDate("data_devolucao"));
        return l;
    }
}