package view;
import controller.CarroController;
import controller.ClienteController;
import controller.UsuarioController;
import model.Carro;
import model.Cliente;
import model.Usuario;
import model.dao.Conexao;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Conexao conn = new Conexao();
        conn.connDB();

        CarroController controller = new CarroController();

        if(!controller.conectaBD()){
            System.out.println("Erro na conexao!");
            return;
        }

        // ===== TESTE 1: CADASTRAR =====
        Carro novoCarro = new Carro();
        novoCarro.setPlaca("BBC1230");
        novoCarro.setModelo("clio");
        novoCarro.setAno(2011);
        novoCarro.setCor("Preto");
        novoCarro.setStatus("disponivel");
        novoCarro.setValorDiaria(80.0);

        String resposta = controller.cadastrarCarro(novoCarro);
        System.out.println(resposta);

       /* // ===== TESTE 2: LISTAR TODOS =====
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
        System.out.println(controller.alterarStatus("ABC1234", "alugado"));

        // ===== TESTE 5: TENTAR EXCLUIR UM CARRO ALUGADO (deve falhar) =====
        System.out.println(controller.excluirCarro("ABC1234"));

        // ===== TESTE 6: DEVOLVER O CARRO E EXCLUIR DE VERDADE =====
        System.out.println(controller.alterarStatus("ABC1234", "disponivel"));
        System.out.println(controller.excluirCarro("ABC1234"));

        // ==== Teste 7: CAadastrar um novo cliente====*/

        // ================= MENU CLIENTE E USUARIO =================
        Scanner sc = new Scanner(System.in);

        ClienteController clienteController = new ClienteController();
        UsuarioController usuarioController = new UsuarioController();

        if(!clienteController.conectaBD() || !usuarioController.conectaBD()){
            System.out.println("Erro na conexao!");
            return;
        }

        int opcao = -1;
        Usuario logado = null; // guarda quem fez login (null = ninguem logado)

        while(opcao != 0){
            System.out.println("\n===== MENU =====");

            // cadastrar cliente so aparece para quem esta logado
            if(logado != null){
                System.out.println("1 - Cadastrar cliente");
            }

            // cadastrar usuario nao aparece para usuario comum
            if(logado == null || logado.getTipo().equals("admin")){
                System.out.println("2 - Cadastrar usuario");
            }
            // a opcao de login so aparece se ninguem estiver logado
            if(logado == null){
                System.out.println("3 - Fazer login");
            }

            // essa opcao aparece para qualquer usuario logado (admin ou comum)
            if(logado != null){
                System.out.println("4 - Listar clientes");
            }

            // essas opcoes so aparecem se quem fez login for admin
            if(logado != null && logado.getTipo().equals("admin")){
                System.out.println("5 - Excluir cliente");
                System.out.println("6 - Listar usuarios");
                System.out.println("7 - Excluir usuario");
            }

            System.out.println("0 - Sair");
            System.out.print("Escolha: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch(opcao){

                // ===== CADASTRAR CLIENTE =====
                case 1:
                    if(logado == null){
                        System.out.println("Opcao invalida, digite alguma das opcoes acima e tente novamente!");
                        break;
                    }
                    System.out.print("Nome: ");
                    String nome = sc.nextLine();
                    System.out.print("CPF: ");
                    String cpf = sc.nextLine();
                    System.out.print("Telefone: ");
                    String telefone = sc.nextLine();
                    System.out.print("Email: ");
                    String email = sc.nextLine();

                    Cliente cliente = new Cliente(nome, cpf, telefone, email);
                    System.out.println(clienteController.cadastrarCliente(cliente));
                    break;

                // ===== CADASTRAR USUARIO =====
                case 2:
                    if(logado != null && !logado.getTipo().equals("admin")){
                        System.out.println("Opcao invalida, digite alguma das opcoes acima e tente novamente!");
                        break;
                    }
                    System.out.print("Nome: ");
                    String nomeUsuario = sc.nextLine();
                    System.out.print("Sobrenome: ");
                    String sobrenome = sc.nextLine();
                    System.out.print("Login: ");
                    String login = sc.nextLine();
                    System.out.print("Senha: ");
                    String senha = sc.nextLine();
                    System.out.print("Confirme a senha: ");
                    String confirmacao = sc.nextLine();
                    System.out.print("Tipo (admin/comum): ");
                    String tipo = sc.nextLine();

                    Usuario usuario = new Usuario(nomeUsuario, sobrenome, login, senha, tipo);
                    System.out.println(usuarioController.cadastrarUsuario(usuario, confirmacao));
                    break;

                // ===== FAZER LOGIN =====
                case 3:
                    if(logado != null){
                        System.out.println("Opcao invalida, digite alguma das opcoes acima e tente novamente!");
                        break;
                    }

                    System.out.print("Login: ");
                    String loginDigitado = sc.nextLine();
                    System.out.print("Senha: ");
                    String senhaDigitada = sc.nextLine();

                    logado = usuarioController.fazerLogin(loginDigitado, senhaDigitada);
                    if(logado != null){
                        System.out.println("Login feito! Bem vindo " + logado.getNome());
                    } else {
                        System.out.println("Login ou senha incorretos!");
                    }
                    break;

                // ===== LISTAR CLIENTES (qualquer usuario logado) =====
                case 4:
                    if(logado == null){
                        System.out.println("Opcao invalida, digite alguma das opcoes acima e tente novamente!");
                        break;
                    }
                    for(Cliente c : clienteController.listarClientes()){
                        System.out.println("ID:" + c.getId() + " NOME:" + c.getNome());
                    }
                    break;

                // ===== EXCLUIR CLIENTE (admin) =====
                case 5:
                    if(logado == null || !logado.getTipo().equals("admin")){
                        System.out.println("Opcao invalida, digite alguma das opcoes acima e tente novamente!");
                        break;
                    }
                    System.out.print("Id do cliente: ");
                    int idExcluirCliente = sc.nextInt();
                    sc.nextLine();
                    System.out.println(clienteController.excluirCliente(idExcluirCliente));
                    break;

                // ===== LISTAR USUARIOS (admin) =====
                case 6:
                    if(logado == null || !logado.getTipo().equals("admin")){
                        System.out.println("Opcao invalida, digite alguma das opcoes acima e tente novamente!");
                        break;
                    }
                    for(Usuario u : usuarioController.listarUsuarios(true)){
                        System.out.println("ID:" + u.getId()+ " NOME:" + u.getNome() + " TIPO:" + u.getTipo());
                    }
                    break;

                // ===== EXCLUIR USUARIO (admin) =====
                case 7:
                    if(logado == null || !logado.getTipo().equals("admin")){
                        System.out.println("Opcao invalida, digite alguma das opcoes acima e tente novamente!");
                        break;
                    }
                    System.out.print("Id do usuario: ");
                    int idExcluirUsuario = sc.nextInt();
                    sc.nextLine();
                    System.out.println(usuarioController.excluirUsuario(idExcluirUsuario, true));

                    // se excluiu o proprio usuario, desconecta e volta pro menu inicial
                    if(idExcluirUsuario == logado.getId()){
                        logado = null;
                        System.out.println("Voce excluiu o seu proprio usuario. Voce foi desconectado.");
                    }
                    break;

                // ===== SAIR =====
                case 0:
                    // se tem alguem logado, o sair so desconecta e volta pro menu inicial
                    if(logado != null){
                        System.out.println("Usuario " + logado.getNome() + " desconectado!");
                        logado = null;
                        opcao = -1; // muda a opcao pra o while nao terminar
                        break;
                    }
                    // se ninguem esta logado, encerra o programa
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção invalida, digite alguma das opções acima!");
            }
        }
        sc.close();

    }
}