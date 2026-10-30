package view;
import controller.CarroController;
import controller.ClienteController;
import controller.LocacaoController;
import model.Cliente;
import model.Locacao;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Scanner;

public class MainLocacao {
    public static void main(String[] args) {

        // ================= MENU LOCACAO =================
        Scanner sc = new Scanner(System.in);

        LocacaoController locacaoController = new LocacaoController();
        CarroController carroController = new CarroController(); // usado para conferir se o carro esta no estoque
        ClienteController clienteController = new ClienteController(); // usado para mostrar o nome do cliente

        if(!locacaoController.conectaBD() || !clienteController.conectaBD() || !carroController.conectaBD()){
            System.out.println("Erro na conexao!");
            return;
        }

        // esta tela nao tem login, entao as opcoes de admin ficam liberadas
        // (mude para false para testar como usuario comum)
        boolean admin = true;

        // formato de data brasileiro (dia/mes/ano). O STRICT recusa datas que nao existem, como 31/02/2026.
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

        int opcao = -1;

        while(opcao != 0){
            System.out.println("\n===== MENU LOCACAO =====");
            System.out.println("1 - Registrar locacao");
            System.out.println("2 - Registrar devolucao");
            System.out.println("3 - Listar locacoes em aberto");
            System.out.println("4 - Listar todas as locacoes");

            // essa opcao so aparece se for admin
            if(admin){
                System.out.println("5 - Excluir locacao");
            }

            System.out.println("0 - Sair");
            System.out.print("Escolha: ");
            opcao = sc.nextInt();
            sc.nextLine();

            switch(opcao){

                // ===== REGISTRAR LOCACAO =====
                case 1:
                    System.out.print("Placa do carro: ");
                    String placaLocacao = sc.nextLine();
                    if(carroController.buscarPorPlaca(placaLocacao) == null){
                        System.out.println("Esse carro nao esta no estoque.");
                        break;
                    }
                    System.out.print("Id do cliente: ");
                    int idCliente = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Data da retirada (dd/mm/aaaa, vazio = hoje): ");
                    String dataLocacaoTexto = sc.nextLine();
                    System.out.print("Data prevista para devolver o carro (dd/mm/aaaa): ");
                    String dataPrevistaTexto = sc.nextLine();

                    Locacao locacao = new Locacao();
                    locacao.setPlacaCarro(placaLocacao);
                    locacao.setIdCliente(idCliente);
                    try {
                        if(dataLocacaoTexto.isEmpty()){
                            locacao.setDataLocacao(new Date(System.currentTimeMillis()));
                        } else {
                            locacao.setDataLocacao(Date.valueOf(LocalDate.parse(dataLocacaoTexto, formato)));
                        }
                    } catch (DateTimeParseException e) {
                        System.out.println("Data invalida! Use o formato dd/mm/aaaa.");
                        break;
                    }
                    // so converte o texto em data; as regras da data prevista ficam no controller
                    try {
                        locacao.setDataPrevistaDevolucao(Date.valueOf(LocalDate.parse(dataPrevistaTexto, formato)));
                    } catch (DateTimeParseException e) {
                        System.out.println("Data prevista invalida! Use o formato dd/mm/aaaa.");
                        break;
                    }
                    System.out.println(locacaoController.registrarLocacao(locacao));
                    break;

                // ===== REGISTRAR DEVOLUCAO =====
                case 2:
                    System.out.print("Id da locacao: ");
                    int idDevolucao = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Data da devolucao (dd/mm/aaaa, vazio = hoje): ");
                    String dataDevolucaoTexto = sc.nextLine();

                    Date dataDevolucao;
                    try {
                        if(dataDevolucaoTexto.isEmpty()){
                            dataDevolucao = new Date(System.currentTimeMillis());
                        } else {
                            dataDevolucao = Date.valueOf(LocalDate.parse(dataDevolucaoTexto, formato));
                        }
                    } catch (DateTimeParseException e) {
                        System.out.println("Data invalida! Use o formato dd/mm/aaaa.");
                        break;
                    }
                    System.out.println(locacaoController.registrarDevolucao(idDevolucao, dataDevolucao));
                    break;

                // ===== LISTAR LOCACOES EM ABERTO =====
                case 3:
                    ArrayList<Locacao> abertas = locacaoController.listarEmAberto();
                    if(abertas == null || abertas.isEmpty()){
                        System.out.println("Nao ha locacoes em aberto.");
                        break;
                    }
                    for(Locacao l : abertas){
                        Cliente c = clienteController.buscarPorId(l.getIdCliente());
                        System.out.println("ID:" + l.getId() + " CARRO:" + l.getPlacaCarro() + " CLIENTE:" + (c == null ? l.getIdCliente() : c.getNome()) + " RETIRADA:" + l.getDataLocacao().toLocalDate().format(formato) + " PREVISTA:" + (l.getDataPrevistaDevolucao() == null ? "-" : l.getDataPrevistaDevolucao().toLocalDate().format(formato)));
                    }
                    break;

                // ===== LISTAR TODAS AS LOCACOES =====
                case 4:
                    ArrayList<Locacao> todas = locacaoController.listarLocacoes();
                    if(todas == null || todas.isEmpty()){
                        System.out.println("Nao ha locacoes cadastradas.");
                        break;
                    }
                    for(Locacao l : todas){
                        Cliente c = clienteController.buscarPorId(l.getIdCliente());
                        System.out.println("ID:" + l.getId() + " CARRO:" + l.getPlacaCarro() + " CLIENTE:" + (c == null ? l.getIdCliente() : c.getNome()) + " RETIRADA:" + l.getDataLocacao().toLocalDate().format(formato) + " PREVISTA:" + (l.getDataPrevistaDevolucao() == null ? "-" : l.getDataPrevistaDevolucao().toLocalDate().format(formato)) + " DEVOLUCAO:" + (l.getDataDevolucao() == null ? "em aberto" : l.getDataDevolucao().toLocalDate().format(formato)));
                    }
                    break;

                // ===== EXCLUIR LOCACAO (admin) =====
                case 5:
                    if(!admin){
                        System.out.println("Opcao invalida, digite alguma das opcoes acima e tente novamente!");
                        break;
                    }
                    System.out.print("Id da locacao: ");
                    int idExcluir = sc.nextInt();
                    sc.nextLine();
                    System.out.println(locacaoController.excluirLocacao(idExcluir));
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