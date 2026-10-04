package controller;

import model.Carro;
import model.dao.CrudCarro;
import model.dao.Conexao;

import java.sql.Statement;
import java.util.ArrayList;

// Controller do Carro: fica entre a view (telas) e o model (banco).
// A view nunca fala direto com o CRUD nem conhece SQL ou nome de tabela,
// ela so chama os metodos simples daqui.
public class CarroController {

    // Objeto que sabe executar os comandos SQL da tabela carro
    private CrudCarro crudCarro;

    // Nome da tabela no banco. Fica guardado aqui para a view nao precisar saber
    private String tabela = "carro";

    public CarroController() {
    }

    // Abre a conexao com o banco informado (ex: "locadora") e cria o CRUD
    // usando o Statement dessa conexao.
    // Retorna true se deu certo, para a view decidir se pode continuar.
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

    // Create: cadastra um carro novo no estoque da locadora
    public String cadastrarCarro(Carro novoCarro){
        try{
            return this.crudCarro.inserirCarro(this.tabela, novoCarro);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao cadastrar carro!";
    }

    // Read: lista todos os carros cadastrados (retorna null se der erro)
    public ArrayList<Carro> listarCarros(){
        try{
            return this.crudCarro.selecionarTodos(this.tabela);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Read: busca um carro especifico pela placa (chave primaria).
    // Retorna null se nao encontrar ou se der erro
    public Carro buscarPorPlaca(String placa){
        try{
            return this.crudCarro.selecionarPlaca(this.tabela, placa);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Read: busca carros cujo modelo contenha o texto digitado (busca parcial)
    public ArrayList<Carro> buscarPorModelo(String modelo){
        try{
            return this.crudCarro.buscarPorModelo(this.tabela, modelo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Read: filtra os carros por status (disponivel, alugado, manutencao...)
    public ArrayList<Carro> filtrarPorStatus(String status){
        try{
            return this.crudCarro.filtrarPorStatus(this.tabela, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update: edita os dados cadastrais do carro (modelo, ano, cor, valor da diaria)
    public String editarCarro(String placa, Carro dadosAtualizados){
        try{
            return this.crudCarro.atualizarCarro(this.tabela, placa, dadosAtualizados);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao editar carro!";
    }

    // Update: altera somente o status do carro (ex: disponivel para alugado)
    public String alterarStatus(String placa, String novoStatus){
        try{
            return this.crudCarro.atualizarStatus(this.tabela, placa, novoStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao alterar status!";
    }

    // Update: altera o caminho da imagem do carro.
    // Regra de negocio: somente o administrador pode fazer isso.
    // O controller recebe a informacao de quem esta logado e decide se deixa passar,
    // assim a regra fica garantida mesmo que a tela esqueca de esconder o botao.
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

    // Delete: exclui um carro.
    // A regra de so excluir carro "disponivel" fica dentro do CrudCarro.deletarCarro
    public String excluirCarro(String placa){
        try{
            return this.crudCarro.deletarCarro(this.tabela, placa);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao excluir carro!";
    }
}