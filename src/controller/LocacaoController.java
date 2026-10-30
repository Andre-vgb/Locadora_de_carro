package controller;

import model.Locacao;
import model.dao.CrudCarro;
import model.dao.CrudCliente;
import model.dao.CrudLocacao;
import model.Carro;
import model.dao.Conexao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

// Controller da Locacao: confere as regras ANTES de chegar no banco.
// Ele usa tres CRUDs (locacao, carro e cliente) porque uma locacao depende
// de um carro disponivel e de um cliente que exista.
public class LocacaoController {

    // CRUD da locacao (a tabela principal deste controller)
    private CrudLocacao crudLocacao;

    // CRUD do carro: a locacao precisa consultar e mudar o status do carro,
    // por isso o controller tambem enxerga essa classe
    private CrudCarro crudCarro;

    // CRUD do cliente: usado so para conferir se o cliente existe antes de alugar
    private CrudCliente crudCliente;

    private String tabela = "locacao";
    private String tabelaCarro = "carro";
    private String tabelaCliente = "cliente";

    public LocacaoController() {
    }

    // Abre a conexao com o banco e cria os CRUDs.
    // Locacao e carro usam o MESMO Statement; o cliente usa a Connection.
    // Retorna true se deu certo, para a view saber se pode continuar.
    // Se a conexao falhar, o Statement vem nulo e o metodo retorna false.
    public boolean conectaBD(){
        try {
            Conexao conn = new Conexao();
            conn.connDB();
            Statement s = conn.getS();
            Connection c = conn.getConn();
            if (s == null || c == null) {
                return false;
            }
            crudLocacao = new CrudLocacao(s);
            crudCarro = new CrudCarro(s);
            crudCliente = new CrudCliente(c);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Registra uma nova locacao (momento em que o cliente leva o carro).
    // Regras: dados completos, cliente existente e carro existente e "disponivel".
    // A data prevista de devolucao (quando o cliente combinou de entregar) e obrigatoria
    // e nao pode ser anterior a data da locacao.
    // A data de devolucao precisa vir vazia: ela so e preenchida na devolucao.
    // Depois de gravar a locacao, o carro passa para "alugado".
    public String registrarLocacao(Locacao novaLocacao){
        try{
            // 1) confere os dados que vieram da tela
            if (novaLocacao == null) {
                return "Os dados da locacao sao obrigatorios.";
            }
            if (novaLocacao.getPlacaCarro() == null || novaLocacao.getPlacaCarro().isBlank()) {
                return "A placa do carro e obrigatoria.";
            }
            if (novaLocacao.getIdCliente() <= 0) {
                return "O cliente e obrigatorio.";
            }
            if (novaLocacao.getDataLocacao() == null) {
                return "A data da locacao e obrigatoria.";
            }
            if (novaLocacao.getDataPrevistaDevolucao() == null) {
                return "A data prevista de devolucao e obrigatoria.";
            }
            if (novaLocacao.getDataPrevistaDevolucao().before(novaLocacao.getDataLocacao())) {
                return "A data prevista de devolucao nao pode ser anterior a data da locacao.";
            }
            if (novaLocacao.getDataDevolucao() != null) {
                return "Ao registrar a locacao a data de devolucao deve ficar vazia. Ela e informada na devolucao.";
            }

            // padroniza a placa (mesma regra do CarroController)
            novaLocacao.setPlacaCarro(novaLocacao.getPlacaCarro().trim().toUpperCase());

            // 2) confere se o cliente existe
            if (crudCliente.selecionarPorId(tabelaCliente, novaLocacao.getIdCliente()) == null) {
                return "Cliente nao encontrado!";
            }

            // 3) confere se o carro existe
            Carro carro = crudCarro.selecionarPlaca(tabelaCarro, novaLocacao.getPlacaCarro());
            if(carro == null){
                return "Carro nao encontrado!";
            }

            // 4) confere se o carro pode ser alugado
            if(!carro.getStatus().equals("disponivel")){
                return "Nao e possivel alugar: carro esta " + carro.getStatus() + ".";
            }

            // 5) grava a locacao no banco
            String resposta = crudLocacao.inserirLocacao(this.tabela, novaLocacao);
            if(!resposta.equals("Codigo retornado: 1")){
                return "Erro ao registrar locacao! (" + resposta + ")";
            }

            // 6) so muda o status do carro se a locacao foi realmente gravada
            String respostaStatus = crudCarro.atualizarStatus(tabelaCarro, novaLocacao.getPlacaCarro(), "alugado");
            if(!respostaStatus.equals("Linhas afetadas: 1")){
                // a locacao ja esta gravada, mas o carro continuou "disponivel":
                // avisa para o usuario corrigir o status manualmente
                return "Locacao gravada, mas nao foi possivel marcar o carro como alugado. "
                        + "Altere o status do carro " + novaLocacao.getPlacaCarro() + " manualmente.";
            }
            return "Locacao registrada com sucesso!";
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
            // 0) a data de devolucao e obrigatoria
            if (dataDevolucao == null) {
                return "A data de devolucao e obrigatoria.";
            }

            // 1) busca a locacao
            Locacao locacao = crudLocacao.selecionarPorId(this.tabela, idLocacao);
            if(locacao == null){
                return "Locacao nao encontrada!";
            }

            // 2) nao deixa devolver duas vezes
            if(locacao.getDataDevolucao() != null){
                return "Esta locacao ja foi devolvida em "
                        + locacao.getDataDevolucao().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".";
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
            String respostaStatus = crudCarro.atualizarStatus(tabelaCarro, locacao.getPlacaCarro(), "disponivel");
            String avisoStatus = "";
            if(!respostaStatus.equals("Linhas afetadas: 1")){
                avisoStatus = "\nAtencao: nao foi possivel liberar o carro. Altere o status dele manualmente.";
            }

            // 7) calcula o valor total
            double valorTotal = calcularValorTotal(locacao.getDataLocacao(), dataDevolucao, carro.getValorDiaria());
            long dias = calcularDias(locacao.getDataLocacao(), dataDevolucao);

            return "Devolucao registrada!\n"
                    + "Carro: " + carro.getModelo() + " (" + carro.getPlaca() + ")\n"
                    + "Dias alugados: " + dias + "\n"
                    + "Valor da diaria: R$ " + String.format("%.2f", carro.getValorDiaria()) + "\n"
                    + "Valor total: R$ " + String.format("%.2f", valorTotal)
                    + avisoStatus;
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
            if (placaCarro == null || placaCarro.isBlank()) {
                return new ArrayList<>();
            }
            return this.crudLocacao.filtrarPorCarro(this.tabela, placaCarro.trim().toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Edita os dados de uma locacao (correcao de cadastro).
    // So permite editar enquanto a locacao esta em aberto, para nao misturar o historico.
    // Nao permite trocar o carro: o status do carro antigo e do novo ficaria errado.
    // Para corrigir o carro, o caminho e registrar a devolucao e fazer uma nova locacao.
    public String editarLocacao(int id, Locacao dadosAtualizados){
        try{
            // 1) confere os dados que vieram da tela
            if (dadosAtualizados == null) {
                return "Os dados da locacao sao obrigatorios.";
            }
            if (dadosAtualizados.getPlacaCarro() == null || dadosAtualizados.getPlacaCarro().isBlank()) {
                return "A placa do carro e obrigatoria.";
            }
            if (dadosAtualizados.getIdCliente() <= 0) {
                return "O cliente e obrigatorio.";
            }
            if (dadosAtualizados.getDataLocacao() == null) {
                return "A data da locacao e obrigatoria.";
            }
            dadosAtualizados.setPlacaCarro(dadosAtualizados.getPlacaCarro().trim().toUpperCase());

            // 2) confere a locacao atual
            Locacao atual = crudLocacao.selecionarPorId(this.tabela, id);
            if(atual == null){
                return "Locacao nao encontrada!";
            }
            if(atual.getDataDevolucao() != null){
                return "Nao e possivel editar: locacao ja foi encerrada.";
            }
            if(!atual.getPlacaCarro().equals(dadosAtualizados.getPlacaCarro())){
                return "Nao e possivel trocar o carro de uma locacao em aberto.";
            }

            // 3) o novo cliente precisa existir
            if (crudCliente.selecionarPorId(tabelaCliente, dadosAtualizados.getIdCliente()) == null) {
                return "Cliente nao encontrado!";
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