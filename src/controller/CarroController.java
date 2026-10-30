package controller;

import model.Carro;
import model.dao.CrudCarro;
import model.dao.Conexao;

import java.sql.Statement;
import java.time.Year;
import java.util.ArrayList;

// Controller do Carro: fica entre a view (telas) e o model (banco).
// A view nunca fala direto com o CRUD nem conhece SQL ou nome de tabela,
// ela so chama os metodos simples daqui.
// A ideia deste controller e conferir tudo ANTES de chegar no CRUD:
// se algum dado estiver errado, ele para aqui e nem chega ao banco.
public class CarroController {

    // Objeto que sabe executar os comandos SQL da tabela carro
    private CrudCarro crudCarro;

    // Nome da tabela no banco. Fica guardado aqui para a view nao precisar saber
    private String tabela = "carro";

    // Unicos status que o sistema aceita. As regras do sistema (alugar, excluir)
    // comparam o texto exato, entao qualquer outro valor quebraria essas regras.
    public static final String STATUS_DISPONIVEL = "disponivel";
    public static final String STATUS_ALUGADO = "alugado";
    public static final String STATUS_MANUTENCAO = "manutencao";

    public CarroController() {
    }

    // Abre a conexao com o banco e cria o CRUD usando o Statement dessa conexao.
    // Retorna true se deu certo, para a view decidir se pode continuar.
    // Se a conexao falhar, o Statement vem nulo e o metodo retorna false.
    public boolean conectaBD(){
        try {
            Conexao conn = new Conexao();
            conn.connDB();
            Statement s = conn.getS();
            if (s == null) {
                return false;
            }
            crudCarro = new CrudCarro(s);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Create: cadastra um carro novo no estoque da locadora.
    // Antes de gravar, confere todos os campos. Placa e status sao padronizados:
    // a placa vai em maiusculas e sem espacos, o status em minusculas.
    public String cadastrarCarro(Carro novoCarro){
        try{
            if (novoCarro == null) {
                return "Os dados do carro sao obrigatorios.";
            }

            // padroniza antes de validar, para "abc1234 " virar "ABC1234"
            if (novoCarro.getPlaca() != null) {
                novoCarro.setPlaca(novoCarro.getPlaca().trim().toUpperCase());
            }
            if (novoCarro.getStatus() != null) {
                novoCarro.setStatus(novoCarro.getStatus().trim().toLowerCase());
            }

            if (vazio(novoCarro.getPlaca())) {
                return "A placa e obrigatoria.";
            }
            String erroDados = validarDadosCadastrais(novoCarro);
            if (erroDados != null) {
                return erroDados;
            }
            if (!statusValido(novoCarro.getStatus())) {
                return mensagemStatusInvalido();
            }

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
            if (vazio(placa)) {
                return null;
            }
            return this.crudCarro.selecionarPlaca(this.tabela, placa.trim().toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Read: busca carros cujo modelo contenha o texto digitado (busca parcial)
    public ArrayList<Carro> buscarPorModelo(String modelo){
        try{
            if (modelo == null) {
                return new ArrayList<>();
            }
            return this.crudCarro.buscarPorModelo(this.tabela, modelo.trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Read: filtra os carros por status (disponivel, alugado, manutencao).
    // Um status que nao existe devolve lista vazia sem nem consultar o banco.
    public ArrayList<Carro> filtrarPorStatus(String status){
        try{
            if (status == null || !statusValido(status.trim().toLowerCase())) {
                return new ArrayList<>();
            }
            return this.crudCarro.filtrarPorStatus(this.tabela, status.trim().toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update: edita os dados cadastrais do carro (marca, modelo, ano, cor, valor da diaria).
    // A placa identifica qual carro editar e o status tem metodo proprio (alterarStatus).
    public String editarCarro(String placa, Carro dadosAtualizados){
        try{
            if (vazio(placa)) {
                return "A placa e obrigatoria.";
            }
            if (dadosAtualizados == null) {
                return "Os dados do carro sao obrigatorios.";
            }
            String erroDados = validarDadosCadastrais(dadosAtualizados);
            if (erroDados != null) {
                return erroDados;
            }
            return this.crudCarro.atualizarCarro(this.tabela, placa.trim().toUpperCase(), dadosAtualizados);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao editar carro!";
    }

    // Update: altera somente o status do carro (ex: disponivel para manutencao).
    // So aceita os tres status validos, mas o status "alugado" nao pode ser escolhido aqui:
    // ele muda sozinho quando se registra uma locacao (LocacaoController).
    // Um carro que ja esta alugado tambem nao muda por aqui: o caminho e registrar a devolucao.
    public String alterarStatus(String placa, String novoStatus){
        try{
            if (vazio(placa)) {
                return "A placa e obrigatoria.";
            }
            if (novoStatus == null || !statusValido(novoStatus.trim().toLowerCase())) {
                return mensagemStatusInvalido();
            }
            if (STATUS_ALUGADO.equals(novoStatus.trim().toLowerCase())) {
                return "O status alugado so muda ao registrar uma locacao.";
            }

            // le o carro antes de mudar, para conferir se existe e qual e o status atual
            Carro atual = this.crudCarro.selecionarPlaca(this.tabela, placa.trim().toUpperCase());
            if (atual == null) {
                return "Carro nao encontrado!";
            }
            if (STATUS_ALUGADO.equals(atual.getStatus())) {
                return "Carro alugado: registre a devolucao na locacao para libera-lo.";
            }

            return this.crudCarro.atualizarStatus(this.tabela, placa.trim().toUpperCase(), novoStatus.trim().toLowerCase());
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
            if (vazio(placa)) {
                return "A placa e obrigatoria.";
            }
            if (vazio(caminhoImagem)) {
                return "O caminho da imagem e obrigatorio.";
            }
            return this.crudCarro.atualizarImagem(this.tabela, placa.trim().toUpperCase(), caminhoImagem.trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao alterar imagem!";
    }

    // Delete: exclui um carro.
    // A regra de so excluir carro "disponivel" fica dentro do CrudCarro.deletarCarro
    public String excluirCarro(String placa){
        try{
            if (vazio(placa)) {
                return "A placa e obrigatoria.";
            }
            return this.crudCarro.deletarCarro(this.tabela, placa.trim().toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao excluir carro!";
    }

    // Metodos auxiliares de validacao (nao acessam o banco)

    // Confere os dados cadastrais que existem tanto no cadastro quanto na edicao.
    // Retorna a mensagem do primeiro erro encontrado, ou null se esta tudo certo.
    private String validarDadosCadastrais(Carro c) {
        if (vazio(c.getMarca())) {
            return "A marca e obrigatoria.";
        }
        if (vazio(c.getModelo())) {
            return "O modelo e obrigatorio.";
        }
        if (vazio(c.getCor())) {
            return "A cor e obrigatoria.";
        }
        // 1900 evita digitar um ano absurdo; o ano que vem e aceito porque
        // modelos novos costumam ser vendidos com ano seguinte
        int anoMaximo = Year.now().getValue() + 1;
        if (c.getAno() < 1900 || c.getAno() > anoMaximo) {
            return "O ano deve estar entre 1900 e " + anoMaximo + ".";
        }
        if (c.getValorDiaria() <= 0) {
            return "O valor da diaria deve ser maior que zero.";
        }
        return null;
    }

    // Texto nulo ou so com espacos conta como vazio
    private boolean vazio(String texto) {
        return texto == null || texto.isBlank();
    }

    // Confere se o status e um dos tres aceitos pelo sistema
    private boolean statusValido(String status) {
        return STATUS_DISPONIVEL.equals(status)
                || STATUS_ALUGADO.equals(status)
                || STATUS_MANUTENCAO.equals(status);
    }

    private String mensagemStatusInvalido() {
        return "Status invalido. Use: " + STATUS_DISPONIVEL + ", " + STATUS_ALUGADO
                + " ou " + STATUS_MANUTENCAO + ".";
    }
}