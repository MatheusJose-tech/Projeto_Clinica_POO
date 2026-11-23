package view;

import controller.ConsultaController;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import model.Consulta;
import model.Relatorio;
import model.exception.ClinicaException;

public class ConsultaView {
    private final ConsultaController controller;
    private final Scanner ler;

    public ConsultaView(ConsultaController controller) {
        this.controller = controller;
        this.ler = new Scanner(System.in);
    }
    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("=== Menu de Consultas ===");
            System.out.println("1. Agendar Consulta");
            System.out.println("2. Cancelar Consulta");
            System.out.println("3. Listar Consultas");
            System.out.println("4. Remarcar Consulta");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = ler.nextInt();
            ler.nextLine();

            switch (opcao) {
                case 1:
                    try {
                        String tipo, cpfPaciente, cpfProf;  
                        LocalDateTime dataHora;
                        System.out.println("Digite o tipo da consulta: ");
                        tipo = ler.nextLine();
                        System.out.print("Digite o CPF do paciente: ");
                        cpfPaciente = ler.nextLine();
                        System.out.println("Digite o CPF do profissional: ");
                        cpfProf = ler.nextLine();
                        System.out.println("Digite a data e hora da consulta (formato: AAAA-MM-DDTHH:MM): ");
                        dataHora = LocalDateTime.parse(ler.nextLine());
                        Consulta novaConsulta = controller.agendar(tipo, cpfPaciente, cpfProf, dataHora);
                        System.out.println("Consulta agendada com sucesso!");
                    } catch (ClinicaException e) {
                        System.err.println("Erro ao agendar consulta: " + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        int id;
                        System.out.println("Digite o ID da consulta a ser cancelada: ");
                        id = ler.nextInt();
                        ler.nextLine();
                        controller.cancelar(id);
                        System.out.println("Consulta cancelada com sucesso!");
                    } catch (ClinicaException e) {
                        System.err.println("Erro ao cancelar consulta: " + e.getMessage());
                    }
                    break;
                case 3:
                    List<Consulta> listaConsultas = controller.listar();
                    if (listaConsultas.isEmpty()) {
                        System.out.println("Nenhuma consulta encontrada.");
                    } else {
                        System.out.println("=== Lista de Consultas ===");
                        for (Consulta consulta : listaConsultas) {
                            System.out.println(consulta);
                            }
                    }
                    break;
                case 4:
                    try {
                        int id;
                        LocalDateTime novaDataHora;
                        System.out.println("Digite o ID da consulta a ser remarcada: ");
                        id = ler.nextInt();
                        ler.nextLine();
                        System.out.println("Digite a nova data e hora da consulta (formato: AAAA-MM-DDTHH:MM): ");
                        novaDataHora = LocalDateTime.parse(ler.nextLine());
                        controller.remarcar(id, novaDataHora);
                        System.out.println("Consulta remarcada com sucesso!");
                    } catch (ClinicaException e) {
                        System.err.println("Erro ao remarcar consulta: " + e.getMessage());
                    }
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 0);
    }
    public void exibirRelatorio(){
        try {
            String cpfProf;
            LocalDate inicio, fim;
            System.out.println("Digite o CPF do profissional: ");
            cpfProf = ler.nextLine();
            System.out.println("Digite a data de início do relatório (formato: AAAA-MM-DD): ");
             inicio = LocalDate.parse(ler.nextLine());
            System.out.println("Digite a data de fim do relatório (formato: AAAA-MM-DD): ");
            fim = LocalDate.parse(ler.nextLine());
            Relatorio relatorio = controller.gerarRelatorio(cpfProf, inicio, fim);
            System.out.println("=== Relatório de Consultas ===");
            System.out.println(relatorio);
        } catch (ClinicaException e) {
            System.err.println("Erro ao gerar relatório: " + e.getMessage());
        }
        
    }
}
