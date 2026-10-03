package model.dao;

import model.Carro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class CrudCarro implements ICarroDAO {
    private Statement s;

    public CrudCarro(Statement s) {
        this.s = s;
    }


    // ESSE É O CREATE
    @Override
    public String inserirCarro(String tabela, Carro novoCarro){
        String SQL = "INSERT INTO "+tabela+" (placa, modelo, ano, cor, status, valor_diaria) VALUES ('"
                +novoCarro.getPlaca()+"','"+novoCarro.getModelo()+"', "
                +novoCarro.getAno()+", '"+novoCarro.getCor()+"', '"
                +novoCarro.getStatus()+"', "+novoCarro.getValorDiaria()+")";
        int linhasAfetadas = -1;
        try{
            linhasAfetadas = s.executeUpdate(SQL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Codigo retornado: "+linhasAfetadas;
    }

    // ===================== READ =====================

    @Override
    public ArrayList<Carro> selecionarTodos(String tabela){
        String SQL = "SELECT * FROM \"" + tabela + "\";";
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


    // READ (busca por chave primária)


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


    // READ (busca textual, não exata)


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

    // READ (filtro por uma condição exata)

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


    // ===================== UPDATE =====================


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
        return "Linhas afetadas: "+linhasAfetadas;
    }


    // UPDATE (de um único campo específico)

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



    // UPDATE (de um único campo específico, restrito ao admin na camada de cima)

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


    // ===================== DELETE =====================

    @Override
    public String deletarCarro(String tabela, String placa){
        Carro c = selecionarPlaca(tabela, placa);

        if(c == null){
            return "Carro não encontrado!";
        }

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

    // método auxiliar, não faz parte da interface: evita repetir o mesmo
    // bloco de "ler ResultSet e montar Carro" em cada método de SELECT

    // método auxiliar (não é CRUD, é só um utilitário interno,
    // por isso não está na interface nem tem @Override)

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