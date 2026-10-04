package controller;

import model.Locacao;
import model.dao.CrudCarro;
import model.dao.CrudLocacao;
import model.Carro;
import model.dao.Conexao;

import java.sql.Date;
import java.sql.Statement;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class LocacaoController {

    // CRUD da locacao (a tabela principal deste controller)
    private CrudLocacao crudLocacao;

    // CRUD do carro: a locacao precisa consultar e mudar o status do carro,
    // por isso o controller tambem enxerga essa classe
    private CrudCarro crudCarro;

    private String tabela = "locacao";
    private String tabelaCarro = "carro";

    public LocacaoController() {
    }

    // Abre a conexao com o banco e cria os dois CRUDs com o MESMO Statement.
    // Retorna true se deu certo, para a view saber se pode continuar.
    public boolean conectaBD(String db){
        try {
            Conexao conn = new Conexao();
            conn.connDB(db);
            Statement s = conn.getS();
            crudLocacao = new CrudLocacao(s);
            crudCarro = new CrudCarro(s);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Registra uma nova locacao (momento em que o cliente leva o carro).
    // Regras: o carro precisa existir e estar "disponivel".
    // Depois de gravar a locacao, o carro passa para "alugado".
    public String registrarLocacao(Locacao novaLocacao){
        try{
            // 1) confere se o carro existe
            Carro carro = crudCarro.selecionarPlaca(tabelaCarro, novaLocacao.getPlacaCarro());
            if(carro == null){
                return "Carro nao encontrado!";
            }

            // 2) confere se o carro pode ser alugado
            if(!carro.getStatus().equals("disponivel")){
                return "Nao e possivel alugar: carro esta " + carro.getStatus() + ".";
            }

            // 3) grava a locacao no banco
            String resposta = crudLocacao.inserirLocacao(this.tabela, novaLocacao);

            // 4) so muda o status do carro se a locacao foi realmente gravada
            if(resposta.equals("Codigo retornado: 1")){
                crudCarro.atualizarStatus(tabelaCarro, novaLocacao.getPlacaCarro(), "alugado");
                return "Locacao registrada com sucesso!";
            }
            return "Erro ao registrar locacao! (" + resposta + ")";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao registrar locacao!";
    }

    // Registra a devolucao do carro (check-out).
    // Passos: grava a data de devolucao, calcula o valor total
    // (dias x valor da diaria) e libera o carro para voltar ao estoque.
    // Retorna um resumo em texto, que a view pode mostrar no relatorio.
    public String registrarDevolucao(int idLocacao, Date dataDevolucao){
        try{
            // 1) busca a locacao
            Locacao locacao = crudLocacao.selecionarPorId(this.tabela, idLocacao);
            if(locacao == null){
                return "Locacao nao encontrada!";
            }

            // 2) nao deixa devolver duas vezes
            if(locacao.getDataDevolucao() != null){
                return "Esta locacao ja foi devolvida em " + locacao.getDataDevolucao() + ".";
            }

            // 3) a devolucao nao pode ser antes da locacao
            if(dataDevolucao.before(locacao.getDataLocacao())){
                return "A data de devolucao nao pode ser anterior a data da locacao.";
            }

            // 4) busca o carro para pegar o valor da diaria
            Carro carro = crudCarro.selecionarPlaca(tabelaCarro, locacao.getPlacaCarro());
            if(carro == null){
                return "Carro da locacao nao encontrado!";
            }

            // 5) grava a data de devolucao
            String resposta = crudLocacao.registrarDevolucao(this.tabela, idLocacao, dataDevolucao);
            if(!resposta.equals("Linhas afetadas: 1")){
                return "Erro ao registrar devolucao! (" + resposta + ")";
            }

            // 6) devolve o carro ao estoque
            crudCarro.atualizarStatus(tabelaCarro, locacao.getPlacaCarro(), "disponivel");

            // 7) calcula o valor total
            double valorTotal = calcularValorTotal(locacao.getDataLocacao(), dataDevolucao, carro.getValorDiaria());
            long dias = calcularDias(locacao.getDataLocacao(), dataDevolucao);

            return "Devolucao registrada!\n"
                    + "Carro: " + carro.getModelo() + " (" + carro.getPlaca() + ")\n"
                    + "Dias alugados: " + dias + "\n"
                    + "Valor da diaria: R$ " + String.format("%.2f", carro.getValorDiaria()) + "\n"
                    + "Valor total: R$ " + String.format("%.2f", valorTotal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao registrar devolucao!";
    }

    // Lista todas as locacoes (abertas e encerradas)
    public ArrayList<Locacao> listarLocacoes(){
        try{
            return this.crudLocacao.selecionarTodos(this.tabela);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lista so as locacoes em aberto, ou seja, carros que estao com clientes agora
    public ArrayList<Locacao> listarEmAberto(){
        try{
            return this.crudLocacao.selecionarEmAberto(this.tabela);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Busca uma locacao pelo id
    public Locacao buscarPorId(int id){
        try{
            return this.crudLocacao.selecionarPorId(this.tabela, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Historico de locacoes de um cliente
    public ArrayList<Locacao> buscarPorCliente(int idCliente){
        try{
            return this.crudLocacao.buscarPorCliente(this.tabela, idCliente);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Historico de locacoes de um carro
    public ArrayList<Locacao> filtrarPorCarro(String placaCarro){
        try{
            return this.crudLocacao.filtrarPorCarro(this.tabela, placaCarro);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Edita os dados de uma locacao (correcao de cadastro).
    // So permite editar enquanto a locacao esta em aberto, para nao bagunçar o historico.
    public String editarLocacao(int id, Locacao dadosAtualizados){
        try{
            Locacao atual = crudLocacao.selecionarPorId(this.tabela, id);
            if(atual == null){
                return "Locacao nao encontrada!";
            }
            if(atual.getDataDevolucao() != null){
                return "Nao e possivel editar: locacao ja foi encerrada.";
            }
            return this.crudLocacao.atualizarLocacao(this.tabela, id, dadosAtualizados);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao editar locacao!";
    }

    // Exclui uma locacao.
    // Regra: nao exclui locacao em aberto, senao o carro ficaria "alugado" para sempre.
    // Primeiro registre a devolucao, depois exclua.
    public String excluirLocacao(int id){
        try{
            Locacao locacao = crudLocacao.selecionarPorId(this.tabela, id);
            if(locacao == null){
                return "Locacao nao encontrada!";
            }
            if(locacao.getDataDevolucao() == null){
                return "Nao e possivel excluir: locacao em aberto. Registre a devolucao primeiro.";
            }
            return this.crudLocacao.deletarLocacao(this.tabela, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao excluir locacao!";
    }

    // Metodos auxiliares (regra de calculo, nao acessam o banco)

    // Quantidade de dias entre a locacao e a devolucao.
    // Se devolveu no mesmo dia, cobra 1 diaria (minimo).
    private long calcularDias(Date dataLocacao, Date dataDevolucao){
        long dias = ChronoUnit.DAYS.between(dataLocacao.toLocalDate(), dataDevolucao.toLocalDate());
        return Math.max(dias, 1);
    }

    // Valor total = dias x valor da diaria
    private double calcularValorTotal(Date dataLocacao, Date dataDevolucao, double valorDiaria){
        return calcularDias(dataLocacao, dataDevolucao) * valorDiaria;
    }
}