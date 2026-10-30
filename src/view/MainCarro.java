package view;
import controller.CarroController;
import model.Carro;

import java.util.ArrayList;
import java.util.Scanner;

public class MainCarro {
    public static void main(String[] args) {

        // ================= MENU CARRO =================
        Scanner sc = new Scanner(System.in);

        CarroController carroController = new CarroController();

        if(!carroController.conectaBD()){
            System.out.println("Erro na conexao!");
            return;
        }

        // esta tela nao tem login, entao as opcoes de admin ficam liberadas
        // (mude para false para testar como usuario comum)
        boolean admin = true;

        int opcao = -1;

        while(opcao != 0){
            System.out.println("\n===== MENU CARROS =====");
            System.out.println("1 - Listar carros");
            System.out.println("2 - Filtrar carros por status");
            System.out.println("3 - Buscar carro por modelo");
            System.out.println("4 - Cadastrar carro");
            System.out.println("5 - Editar carro");
            System.out.println("6 - Alterar status do carro");

            // essas opcoes so aparecem se for admin
            if(admin){
                System.out.println("7 - Alterar imagem do carro");
                System.out.println("8 - Excluir carro");
            }

            System.out.println("0 - Sair");
            System.out.print("Escolha: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch(opcao){

                // ===== LISTAR CARROS =====
                case 1:
                    ArrayList<Carro> todos = carroController.listarCarros();
                    if(todos == null || todos.isEmpty()){
                        System.out.println("Nao ha carros no estoque.");
                        break;
                    }
                    for(Carro c : todos){
                        System.out.println(c);
                    }
                    break;

                // ===== FILTRAR CARROS POR STATUS =====
                case 2:
                    System.out.println("1 - disponivel");
                    System.out.println("2 - manutencao");
                    System.out.println("3 - alugado");
                    System.out.print("Escolha o status: ");
                    int codigoFiltro = sc.nextInt();
                    sc.nextLine();
                    String statusFiltro;
                    if(codigoFiltro == 1){
                        statusFiltro = "disponivel";
                    } else if(codigoFiltro == 2){
                        statusFiltro = "manutencao";
                    } else if(codigoFiltro == 3){
                        statusFiltro = "alugado";
                    } else {
                        System.out.println("Opcao invalida, digite 1, 2 ou 3.");
                        break;
                    }
                    ArrayList<Carro> filtrados = carroController.filtrarPorStatus(statusFiltro);
                    if(filtrados == null || filtrados.isEmpty()){
                        System.out.println("Nao ha carros com esse status no estoque.");
                        break;
                    }
                    for(Carro c : filtrados){
                        System.out.println(c);
                    }
                    break;

                // ===== BUSCAR CARRO POR MODELO =====
                case 3:
                    System.out.print("Modelo: ");
                    String modeloBusca = sc.nextLine();
                    ArrayList<Carro> encontrados = carroController.buscarPorModelo(modeloBusca);
                    if(encontrados == null || encontrados.isEmpty()){
                        System.out.println("Esse carro nao esta no estoque.");
                        break;
                    }
                    for(Carro c : encontrados){
                        System.out.println(c);
                    }
                    break;

                // ===== CADASTRAR CARRO =====
                case 4:
                    System.out.print("Placa: ");
                    String placa = sc.nextLine();
                    System.out.print("Marca: ");
                    String marca = sc.nextLine();
                    System.out.print("Modelo: ");
                    String modelo = sc.nextLine();
                    System.out.print("Ano: ");
                    int ano = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Cor: ");
                    String cor = sc.nextLine();
                    System.out.print("Valor da diaria: ");
                    // aceita virgula ou ponto (120,50 ou 120.50)
                    double valorDiaria = Double.parseDouble(sc.nextLine().replace(',', '.'));

                    // todo carro novo entra no estoque como disponivel
                    Carro carro = new Carro();
                    carro.setPlaca(placa);
                    carro.setMarca(marca);
                    carro.setModelo(modelo);
                    carro.setAno(ano);
                    carro.setCor(cor);
                    carro.setValorDiaria(valorDiaria);
                    carro.setStatus("disponivel");
                    System.out.println(carroController.cadastrarCarro(carro));
                    break;

                // ===== EDITAR CARRO =====
                case 5:
                    System.out.print("Placa do carro a editar: ");
                    String placaEditar = sc.nextLine();
                    if(carroController.buscarPorPlaca(placaEditar) == null){
                        System.out.println("Esse carro nao esta no estoque.");
                        break;
                    }
                    System.out.print("Nova marca: ");
                    String novaMarca = sc.nextLine();
                    System.out.print("Novo modelo: ");
                    String novoModelo = sc.nextLine();
                    System.out.print("Novo ano: ");
                    int novoAno = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Nova cor: ");
                    String novaCor = sc.nextLine();
                    System.out.print("Novo valor da diaria: ");
                    double novoValor = Double.parseDouble(sc.nextLine().replace(',', '.'));

                    Carro dadosNovos = new Carro();
                    dadosNovos.setMarca(novaMarca);
                    dadosNovos.setModelo(novoModelo);
                    dadosNovos.setAno(novoAno);
                    dadosNovos.setCor(novaCor);
                    dadosNovos.setValorDiaria(novoValor);
                    System.out.println(carroController.editarCarro(placaEditar, dadosNovos));
                    break;

                // ===== ALTERAR STATUS DO CARRO =====
                case 6:
                    System.out.print("Placa do carro: ");
                    String placaStatus = sc.nextLine();
                    if(carroController.buscarPorPlaca(placaStatus) == null){
                        System.out.println("Esse carro nao esta no estoque.");
                        break;
                    }
                    System.out.println("1 - disponivel");
                    System.out.println("2 - manutencao");
                    System.out.print("Escolha o novo status: ");
                    int codigoStatus = sc.nextInt();
                    sc.nextLine();
                    String novoStatus;
                    if(codigoStatus == 1){
                        novoStatus = "disponivel";
                    } else if(codigoStatus == 2){
                        novoStatus = "manutencao";
                    } else {
                        System.out.println("Opcao invalida, digite 1 ou 2.");
                        break;
                    }
                    System.out.println(carroController.alterarStatus(placaStatus, novoStatus));
                    break;

                // ===== ALTERAR IMAGEM DO CARRO (admin) =====
                case 7:
                    if(!admin){
                        System.out.println("Opcao invalida, digite alguma das opcoes acima e tente novamente!");
                        break;
                    }
                    System.out.print("Placa do carro: ");
                    String placaImagem = sc.nextLine();
                    if(carroController.buscarPorPlaca(placaImagem) == null){
                        System.out.println("Esse carro nao esta no estoque.");
                        break;
                    }
                    System.out.print("Caminho da imagem: ");
                    String caminho = sc.nextLine();
                    System.out.println(carroController.alterarImagem(placaImagem, caminho, true));
                    break;

                // ===== EXCLUIR CARRO (admin) =====
                case 8:
                    if(!admin){
                        System.out.println("Opcao invalida, digite alguma das opcoes acima e tente novamente!");
                        break;
                    }
                    System.out.print("Placa do carro: ");
                    String placaExcluir = sc.nextLine();
                    if(carroController.buscarPorPlaca(placaExcluir) == null){
                        System.out.println("Esse carro nao esta no estoque.");
                        break;
                    }
                    System.out.println(carroController.excluirCarro(placaExcluir));
                    break;

                // ===== SAIR =====
                case 0:
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção invalida, digite alguma das opções acima!");
            }
        }
        sc.close();

    }
}