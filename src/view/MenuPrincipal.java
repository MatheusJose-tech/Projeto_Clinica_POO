package view;

import java.util.Scanner;

public class MenuPrincipal {

    private final PacienteView pacienteView;
    private final ProfissionalView profissionalView;
    private final ConsultaView consultaView;
    private final Leitor leitor;

    public MenuPrincipal(PacienteView pacienteView, ProfissionalView profissionalView,
            ConsultaView consultaView, Scanner scanner) {
        this.pacienteView = pacienteView;
        this.profissionalView = profissionalView;
        this.consultaView = consultaView;
        this.leitor = new Leitor(scanner);
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println();
            System.out.println("===== SISTEMA DA CLÍNICA =====");
            System.out.println("1 - Pacientes");
            System.out.println("2 - Profissionais");
            System.out.println("3 - Consultas");
            System.out.println("4 - Relatório por profissional");
            System.out.println("0 - Sair");
            opcao = leitor.lerOpcao("Escolha uma opção: ");

            switch (opcao) {
                case 1 -> pacienteView.exibirMenu();
                case 2 -> profissionalView.exibirMenu();
                case 3 -> consultaView.exibirMenu();
                case 4 -> consultaView.exibirRelatorio();
                case 0 -> System.out.println("Encerrando o sistema...");
                default -> System.out.println("Opção inválida. Digite um número de 0 a 4.");
            }
        } while (opcao != 0);
    }
}