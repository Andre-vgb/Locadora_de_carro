package model.dao;

import model.Locacao;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

// Implementacao do DAO da Locacao (CRUD): e aqui que o SQL da tabela locacao e executado.
// Implementa a interface ILocacaoDAO, entao precisa ter todos os metodos que ela declara.
// Esta classe so mexe na tabela locacao. Regras que envolvem outras tabelas
// (mudar status do carro, calcular valor total) ficam no LocacaoController.
public class CrudLocacao implements ILocacaoDAO {

    // Objeto usado para enviar os comandos SQL ao banco (vem da classe Conexao)
    private Statement s;

    // Recebe o Statement ja criado pela Conexao (via controller)
    public CrudLocacao(Statement s) {
        this.s = s;
    }

    // Create: insere uma nova locacao (momento em que o cliente leva o carro).
    // O id nao entra no INSERT porque e gerado sozinho pelo banco (SERIAL).
    // data_devolucao normalmente comeca nula, pois o carro ainda nao foi devolvido.
    // Por isso, se vier null, escreve a palavra NULL no SQL (sem aspas),
    // senao escreve a data entre aspas simples, no formato yyyy-MM-dd.
    @Override
    public String inserirLocacao(String tabela, Locacao novaLocacao){
        String valorDataDevolucao = (novaLocacao.getDataDevolucao() == null)
                ? "NULL"
                : "'" + novaLocacao.getDataDevolucao() + "'";

        // Textos e datas vao entre aspas simples, numeros (id_cliente) nao
        String SQL = "INSERT INTO " + tabela + " (placa, id_cliente, data_locacao, data_devolucao) VALUES ('"
                + novaLocacao.getPlacaCarro() + "', " + novaLocacao.getIdCliente() + ", '"
                + novaLocacao.getDataLocacao() + "', " + valorDataDevolucao + ")";

        int linhasAfetadas = -1;
        try{
            // executeUpdate e usado para INSERT, UPDATE e DELETE (retorna quantas linhas mudaram)
            linhasAfetadas = s.executeUpdate(SQL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // "Codigo retornado: 1" significa que deu certo, -1 significa que deu erro
        return "Codigo retornado: " + linhasAfetadas;
    }

    // Read: retorna todas as locacoes cadastradas (abertas e encerradas).
    // O ResultSet e um "ponteiro" que anda linha por linha no resultado do SELECT.
    // Para cada linha, monta um objeto Locacao e coloca na lista.
    // Retorna null se der erro.
    @Override
    public ArrayList<Locacao> selecionarTodos(String tabela){
        String SQL = "SELECT * FROM \"" + tabela + "\";";
        try {
            // executeQuery e usado para SELECT (devolve um ResultSet)
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

    // Read (busca por chave primaria): procura uma unica locacao pelo id.
    // Como o id e a PK, vem no maximo uma linha, entao usa if em vez de while.
    // O id e numero, por isso nao tem aspas no SQL.
    // Retorna null se nao achar ou se der erro.
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

    // Read (filtro por cliente): todas as locacoes feitas por um cliente.
    // Serve para ver o historico do cliente.
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

    // Read (filtro por carro): todas as locacoes de um carro, pela placa.
    // Serve para ver o historico do carro. A placa e texto, por isso vai entre aspas simples.
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

    // Read (locacoes em aberto): as que ainda nao foram devolvidas.
    // Para checar valor nulo no SQL e preciso usar IS NULL (nao funciona com "= NULL").
    // Na pratica, sao os carros que estao com clientes neste momento.
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

    // Update: corrige os dados gerais de uma locacao (carro, cliente e data da locacao).
    // O WHERE garante que so a locacao com esse id seja alterada.
    // A data de devolucao nao muda aqui, ela tem o proprio metodo (registrarDevolucao).
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
        // "Linhas afetadas: 0" significa que nenhum id bateu (locacao nao existe)
        return "Linhas afetadas: " + linhasAfetadas;
    }

    // Update (de um unico campo especifico): grava so a data de devolucao,
    // usado quando o carro volta para a locadora.
    // O calculo do valor total e a liberacao do carro (status "disponivel")
    // ficam a cargo do LocacaoController, porque dependem de outra tabela.
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

    // Delete: remove uma locacao do banco pelo id.
    // Este metodo apaga sem perguntar nada. A regra de nao excluir locacao em aberto
    // fica no LocacaoController, que confere antes de chamar este metodo.
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

    // Metodo auxiliar interno, nao faz parte do CRUD nem da interface
    // (por isso e private e nao tem @Override).
    // Evita repetir em cada SELECT o bloco de "ler as colunas e montar uma Locacao".
    // Le a linha atual do ResultSet e devolve uma Locacao preenchida.
    // Os nomes entre aspas sao os nomes das colunas no banco.
    // Atencao: getDate devolve null quando data_devolucao esta vazia no banco,
    // o que e normal para uma locacao em aberto.
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