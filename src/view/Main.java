package view;
import controller.CarroController;
import model.Carro;
import model.dao.Conexao;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Conexao conn = new Conexao();
        conn.connDB("locadora");

        CarroController controller = new CarroController();

        if(!controller.conectaBD("locadora")){
            System.out.println("Erro na conexao!");
            return;
        }

        // ===== TESTE 1: CADASTRAR =====
        Carro novoCarro = new Carro();
        novoCarro.setPlaca("BBC1234");
        novoCarro.setModelo("clio");
        novoCarro.setAno(2011);
        novoCarro.setCor("Preto");
        novoCarro.setStatus("disponivel");
        novoCarro.setValorDiaria(80.0);

        String resposta = controller.cadastrarCarro(novoCarro);
        System.out.println(resposta);

        // ===== TESTE 2: LISTAR TODOS =====
        ArrayList<Carro> lista = controller.listarCarros();
        for(Carro c : lista){
            System.out.println(c.getPlaca() + " | " + c.getModelo() + " | " + c.getStatus());
        }

        // ===== TESTE 3: BUSCAR POR PLACA =====
        Carro encontrado = controller.buscarPorPlaca("ABC1234");
        if(encontrado != null){
            System.out.println("Encontrado: " + encontrado.getModelo() + ", cor " + encontrado.getCor());
        } else {
            System.out.println("Carro não encontrado!");
        }

        // ===== TESTE 4: ALTERAR STATUS (simulando uma locação) =====
        //System.out.println(controller.alterarStatus("ABC1234", "alugado"));

        // ===== TESTE 5: TENTAR EXCLUIR UM CARRO ALUGADO (deve falhar) =====
        //System.out.println(controller.excluirCarro("ABC1234"));

        // ===== TESTE 6: DEVOLVER O CARRO E EXCLUIR DE VERDADE =====
        //System.out.println(controller.alterarStatus("ABC1234", "disponivel"));
        //System.out.println(controller.excluirCarro("ABC1234"));
    }
}