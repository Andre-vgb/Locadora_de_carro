package controller;

import model.Carro;
import model.dao.CrudCarro;
import model.dao.Conexao;

import java.sql.Statement;
import java.util.ArrayList;

public class CarroController {
    private CrudCarro crudCarro;
    private String tabela = "carro";

    public CarroController() {
    }

    public boolean conectaBD(String db){
        try {
            Conexao conn = new Conexao();
            conn.connDB(db);
            Statement s = conn.getS();
            crudCarro = new CrudCarro(s);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String cadastrarCarro(Carro novoCarro){
        try{
            return this.crudCarro.inserirCarro(this.tabela, novoCarro);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao cadastrar carro!";
    }

    public ArrayList<Carro> listarCarros(){
        try{
            return this.crudCarro.selecionarTodos(this.tabela);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Carro buscarPorPlaca(String placa){
        try{
            return this.crudCarro.selecionarPlaca(this.tabela, placa);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Carro> buscarPorModelo(String modelo){
        try{
            return this.crudCarro.buscarPorModelo(this.tabela, modelo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Carro> filtrarPorStatus(String status){
        try{
            return this.crudCarro.filtrarPorStatus(this.tabela, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String editarCarro(String placa, Carro dadosAtualizados){
        try{
            return this.crudCarro.atualizarCarro(this.tabela, placa, dadosAtualizados);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao editar carro!";
    }

    public String alterarStatus(String placa, String novoStatus){
        try{
            return this.crudCarro.atualizarStatus(this.tabela, placa, novoStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao alterar status!";
    }

    // essa é a regra "só admin" que discutimos: o controller recebe
    // a informação de quem está logado e decide se deixa passar
    public String alterarImagem(String placa, String caminhoImagem, boolean usuarioEhAdministrador){
        if(!usuarioEhAdministrador){
            return "Apenas administradores podem alterar a imagem do carro.";
        }
        try{
            return this.crudCarro.atualizarImagem(this.tabela, placa, caminhoImagem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao alterar imagem!";
    }

    public String excluirCarro(String placa){
        try{
            return this.crudCarro.deletarCarro(this.tabela, placa);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao excluir carro!";
    }
}