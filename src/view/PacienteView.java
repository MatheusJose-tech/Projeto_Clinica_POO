package view;

import java.util.List;
import java.util.Scanner;

import controller.PacienteController;
import model.Paciente;
import model.exception.ClinicaException;

public class PacienteView {

    private final PacienteController controller;
    private final Scanner scanner;

    public PacienteView(PacienteController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println();
            System.out.println("===== PACIENTES =====");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Listar");
            System.out.println("3 - Alterar");
            System.out.println("4 - Remover");
            System.out.println("0 - Voltar");
            opcao = lerOpcao();

            try {
                switch (opcao) {
                    case 1 -> cadastrar();
                    case 2 -> listar();
                    case 3 -> alterar();
                    case 4 -> remover();
                    case 0 -> System.out.println("Voltando ao menu principal...");
                    default -> System.out.println("Opção inválida. Digite um número de 0 a 4.");
                }
            } catch (ClinicaException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    // ---------- Ações ----------

    private void cadastrar() throws ClinicaException {
        System.out.println("--- Cadastrar paciente ---");
        String nome = lerTexto("Nome: ");
        String cpf = lerTexto("CPF (11 dígitos, só números): ");
        String plano = lerTexto("Plano de saúde (deixe vazio se não tiver): ");

        controller.cadastrar(nome, cpf, plano);
        System.out.println("Paciente cadastrado com sucesso.");
    }

    private void listar() {
        List<Paciente> pacientes = controller.listar();

        if (pacientes.isEmpty()) {
            System.out.println("Nenhum paciente cadastrado.");
            return;
        }

        System.out.println("--- Pacientes cadastrados (" + pacientes.size() + ") ---");
        for (Paciente p : pacientes) {
            System.out.println(p);
        }
    }

    private void alterar() throws ClinicaException {
        System.out.println("--- Alterar paciente ---");
        String cpf = lerTexto("CPF do paciente: ");
        String nome = lerTexto("Novo nome: ");
        String plano = lerTexto("Novo plano de saúde (deixe vazio se não tiver): ");

        controller.alterar(cpf, nome, plano);
        System.out.println("Paciente alterado com sucesso.");
    }

    private void remover() throws ClinicaException {
        System.out.println("--- Remover paciente ---");
        String cpf = lerTexto("CPF do paciente: ");
        String confirmacao = lerTexto("Confirma a remoção? (s/n): ");

        if (!"s".equalsIgnoreCase(confirmacao)) {
            System.out.println("Remoção cancelada.");
            return;
        }

        controller.remover(cpf);
        System.out.println("Paciente removido com sucesso.");
    }

    // ---------- Leitura protegida ----------

    // Opção não numérica vira -1 (inválida); fim da entrada vira 0 (voltar).
    private int lerOpcao() {
        String linha = lerTexto("Escolha uma opção: ");
        if (linha == null) {
            return 0;
        }
        try {
            return Integer.parseInt(linha);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Devolve o texto digitado sem espaços nas pontas, ou null se a entrada acabou.
    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine().trim();
    }
}
