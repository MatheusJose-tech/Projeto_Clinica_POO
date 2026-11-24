package view;

import controller.PacienteController;
import java.util.List;
import java.util.Scanner;
import model.Paciente;
import model.exception.ClinicaException;

public class PacienteView {

    private final PacienteController controller;
    private final Leitor leitor;

    public PacienteView(PacienteController controller, Scanner scanner) {
        this.controller = controller;
        this.leitor = new Leitor(scanner);
    }

    public void exibirMenu() {
        int opcao;
        do {
            leitor.limparTela();
            System.out.println();
            System.out.println("===== PACIENTES =====");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Listar");
            System.out.println("3 - Alterar");
            System.out.println("4 - Remover");
            System.out.println("0 - Voltar");
            opcao = leitor.lerOpcao("Escolha uma opção: ");

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

            if (opcao != 0) {
                leitor.pausar();
            }
        } while (opcao != 0);
    }

    // ---------- Ações ----------

    private void cadastrar() throws ClinicaException {
        System.out.println("--- Cadastrar paciente ---");
        String nome = leitor.lerTexto("Nome: ");
        String cpf = leitor.lerTexto("CPF (11 dígitos, só números): ");
        String plano = leitor.lerTexto("Plano de saúde (deixe vazio se não tiver): ");

        controller.cadastrar(nome, cpf, plano);
        System.out.println("Paciente cadastrado com sucesso.");
    }

    // Declara throws para funcionar mesmo se o listar() do controller declarar ClinicaException.
    private void listar() throws ClinicaException {
        List<Paciente> pacientes = controller.listar();

        if (pacientes.isEmpty()) {
            System.out.println("Nenhum paciente cadastrado.");
            return;
        }

        System.out.println("--- Pacientes cadastrados (" + pacientes.size() + ") ---");
        for (Paciente p : pacientes) {
            System.out.println(formatar(p));
        }
    }

    private void alterar() throws ClinicaException {
        System.out.println("--- Alterar paciente ---");
        String cpf = leitor.lerTexto("CPF do paciente: ");
        String nome = leitor.lerTexto("Novo nome: ");
        String plano = leitor.lerTexto("Novo plano de saúde (deixe vazio se não tiver): ");

        controller.alterar(cpf, nome, plano);
        System.out.println("Paciente alterado com sucesso.");
    }

    private void remover() throws ClinicaException {
        System.out.println("--- Remover paciente ---");
        String cpf = leitor.lerTexto("CPF do paciente: ");
        String confirmacao = leitor.lerTexto("Confirma a remoção? (s/n): ");

        if (!"s".equalsIgnoreCase(confirmacao)) {
            System.out.println("Remoção cancelada.");
            return;
        }

        controller.remover(cpf);
        System.out.println("Paciente removido com sucesso.");
    }

    // ---------- Apresentação ----------

    private String formatar(Paciente p) {
        String plano = p.temPlano() ? p.getPlano() : "sem plano";
        return p.getNome() + " | CPF: " + p.getCpf() + " | Plano: " + plano;
    }
}