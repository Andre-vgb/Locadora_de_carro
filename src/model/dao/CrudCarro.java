package model.dao;

import model.Carro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class CrudCarro implements ICarroDAO {

    // Objeto usado para enviar os comandos SQL ao banco
    private Statement s;

    // Recebe o Statement ja criado pela Conexao (via controller)
    public CrudCarro(Statement s) {
        this.s = s;
    }

    // Create: insere um carro novo na tabela.
    // O SQL e montado juntando texto com os valores do objeto Carro.
    // Textos (placa, modelo, cor, status) vao entre aspas simples, numeros (ano, valor) nao.
    // Retorna "Codigo retornado: 1" quando deu certo (1 linha inserida) e -1 se deu erro.
    @Override
    public String inserirCarro(String tabela, Carro novoCarro){
        String SQL = "INSERT INTO "+tabela+" (placa, modelo, ano, cor, status, valor_diaria) VALUES ('"
                +novoCarro.getPlaca()+"','"+novoCarro.getModelo()+"', "
                +novoCarro.getAno()+", '"+novoCarro.getCor()+"', '"
                +novoCarro.getStatus()+"', "+novoCarro.getValorDiaria()+")";
        int linhasAfetadas = -1;
        try{
            // executeUpdate e usado para INSERT, UPDATE e DELETE (retorna quantas linhas mudaram)
            linhasAfetadas = s.executeUpdate(SQL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Codigo retornado: "+linhasAfetadas;
    }

    // Read: retorna todos os carros da tabela.
    // O ResultSet e um "ponteiro" que anda linha por linha no resultado do SELECT.
    // Para cada linha, monta um objeto Carro e coloca na lista.
    // Retorna null se der erro.
    @Override
    public ArrayList<Carro> selecionarTodos(String tabela){
        String SQL = "SELECT * FROM \"" + tabela + "\";";
        try {
            // executeQuery e usado para SELECT (devolve um ResultSet)
            ResultSet rset = s.executeQuery(SQL);
            ArrayList<Carro> lista = new ArrayList<>();
            while(rset.next()){
                lista.add(montarCarro(rset));
            }
            return lista;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // Read (busca por chave primaria): procura um unico carro pela placa.
    // Como a placa e a PK, vem no maximo uma linha, entao usa if em vez de while.
    // Retorna null se nao achar ou se der erro.
    @Override
    public Carro selecionarPlaca(String tabela, String placa){
        String SQL = "SELECT * FROM \"" + tabela + "\" WHERE placa = '"+placa+"';";
        try {
            ResultSet rset = s.executeQuery(SQL);
            if(rset.next()){
                return montarCarro(rset);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // Read (busca textual, nao exata): acha carros cujo modelo CONTENHA o texto.
    // ILIKE ignora maiusculas e minusculas (so existe no PostgreSQL).
    // Os % significam "qualquer coisa antes e depois", entao "oni" acha "Onix".
    @Override
    public ArrayList<Carro> buscarPorModelo(String tabela, String modelo){
        String SQL = "SELECT * FROM \"" + tabela + "\" WHERE modelo ILIKE '%"+modelo+"%';";
        try {
            ResultSet rset = s.executeQuery(SQL);
            ArrayList<Carro> lista = new ArrayList<>();
            while(rset.next()){
                lista.add(montarCarro(rset));
            }
            return lista;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // Read (filtro por uma condicao exata): lista so os carros com o status informado
    // (por exemplo "disponivel" ou "alugado").
    @Override
    public ArrayList<Carro> filtrarPorStatus(String tabela, String status){
        String SQL = "SELECT * FROM \"" + tabela + "\" WHERE status = '"+status+"';";
        try {
            ResultSet rset = s.executeQuery(SQL);
            ArrayList<Carro> lista = new ArrayList<>();
            while(rset.next()){
                lista.add(montarCarro(rset));
            }
            return lista;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // Update: altera os dados cadastrais de um carro (modelo, ano, cor e valor da diaria).
    // O WHERE garante que so o carro dessa placa seja alterado.
    // Placa, status e imagem nao mudam aqui, cada um tem seu proprio metodo.
    @Override
    public String atualizarCarro(String tabela, String placa, Carro dadosAtualizados){
        String SQL = "UPDATE " + tabela + " SET modelo='" + dadosAtualizados.getModelo()
                + "', ano=" + dadosAtualizados.getAno()
                + ", cor='" + dadosAtualizados.getCor()
                + "', valor_diaria=" + dadosAtualizados.getValorDiaria()
                + " WHERE placa = '" + placa + "';";
        int linhasAfetadas = 0;
        try{
            linhasAfetadas = s.executeUpdate(SQL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // "Linhas afetadas: 0" significa que nenhuma placa bateu (carro nao existe)
        return "Linhas afetadas: "+linhasAfetadas;
    }

    // Update (de um unico campo especifico): muda so o status do carro.
    // Usado, por exemplo, quando o carro e alugado ou devolvido.
    @Override
    public String atualizarStatus(String tabela, String placa, String novoStatus){
        String SQL = "UPDATE " + tabela + " SET status='" + novoStatus
                + "' WHERE placa = '" + placa + "';";
        int linhasAfetadas = 0;
        try{
            linhasAfetadas = s.executeUpdate(SQL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Linhas afetadas: "+linhasAfetadas;
    }

    // Update (de um unico campo especifico, restrito ao admin na camada de cima):
    // grava o caminho do arquivo de imagem do carro (so o texto do caminho, nao a imagem).
    // A checagem de "so administrador" e feita no CarroController, nao aqui.
    @Override
    public String atualizarImagem(String tabela, String placa, String caminhoImagem){
        String SQL = "UPDATE " + tabela + " SET caminho_imagem='" + caminhoImagem
                + "' WHERE placa = '" + placa + "';";
        int linhasAfetadas = 0;
        try{
            linhasAfetadas = s.executeUpdate(SQL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Linhas afetadas: "+linhasAfetadas;
    }

    // Delete: exclui um carro, mas so se ele estiver "disponivel".
    // Antes de deletar, faz uma leitura (Read) para conferir se o carro existe
    // e qual e o status dele. Carro alugado ou em manutencao nao pode ser excluido.
    @Override
    public String deletarCarro(String tabela, String placa){
        Carro c = selecionarPlaca(tabela, placa);

        // Nao achou nenhum carro com essa placa
        if(c == null){
            return "Carro não encontrado!";
        }

        // Regra de negocio: so exclui se estiver disponivel
        if(!c.getStatus().equals("disponivel")){
            return "Não é possível excluir: carro está " + c.getStatus() + ".";
        }

        String SQL = "DELETE FROM "+tabela+" WHERE placa = '"+placa+"';";
        int codigoRetornado = -1;
        try{
            codigoRetornado = s.executeUpdate(SQL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Codigo retornado: "+codigoRetornado;
    }

    // Metodo auxiliar, nao faz parte do CRUD nem da interface (por isso nao tem @Override
    // e e private). Evita repetir em cada SELECT o bloco de "ler as colunas do ResultSet
    // e montar um Carro". Le a linha atual do ResultSet e devolve um Carro preenchido.
    // Os nomes entre aspas sao os nomes das colunas no banco.
    private Carro montarCarro(ResultSet rset) throws Exception {
        Carro c = new Carro();
        c.setPlaca(rset.getString("placa"));
        c.setModelo(rset.getString("modelo"));
        c.setAno(rset.getInt("ano"));
        c.setCor(rset.getString("cor"));
        c.setStatus(rset.getString("status"));
        c.setValorDiaria(rset.getDouble("valor_diaria"));
        c.setCaminhoImagem(rset.getString("caminho_imagem"));
        return c;
    }
}