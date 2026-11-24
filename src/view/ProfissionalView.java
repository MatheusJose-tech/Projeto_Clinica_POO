package view;

import controller.ProfissionalController;
import java.util.List;
import java.util.Scanner;
import model.Profissional;
import model.exception.ClinicaException;

public class ProfissionalView {

    private final ProfissionalController controller;
    private final Leitor leitor;

    public ProfissionalView(ProfissionalController controller, Scanner scanner) {
        this.controller = controller;
        this.leitor = new Leitor(scanner);
    }

    public void exibirMenu() {
        int opcao;
        do {
            leitor.limparTela();
            System.out.println();
            System.out.println("===== PROFISSIONAIS =====");
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
        System.out.println("--- Cadastrar profissional ---");
        String nome = leitor.lerTexto("Nome: ");
        String cpf = leitor.lerTexto("CPF (11 dígitos, só números): ");
        String especialidade = leitor.lerTexto("Especialidade: ");

        controller.cadastrar(nome, cpf, especialidade);
        System.out.println("Profissional cadastrado com sucesso.");
    }

    // Declara throws para funcionar mesmo se o listar() do controller declarar ClinicaException.
    private void listar() throws ClinicaException {
        List<Profissional> profissionais = controller.listar();

        if (profissionais.isEmpty()) {
            System.out.println("Nenhum profissional cadastrado.");
            return;
        }

        System.out.println("--- Profissionais cadastrados (" + profissionais.size() + ") ---");
        for (Profissional p : profissionais) {
            System.out.println(formatar(p));
        }
    }

    private void alterar() throws ClinicaException {
        System.out.println("--- Alterar profissional ---");
        String cpf = leitor.lerTexto("CPF do profissional: ");
        String nome = leitor.lerTexto("Novo nome: ");
        String especialidade = leitor.lerTexto("Nova especialidade: ");

        controller.alterar(cpf, nome, especialidade);
        System.out.println("Profissional alterado com sucesso.");
    }

    private void remover() throws ClinicaException {
        System.out.println("--- Remover profissional ---");
        String cpf = leitor.lerTexto("CPF do profissional: ");
        String confirmacao = leitor.lerTexto("Confirma a remoção? (s/n): ");

        if (!"s".equalsIgnoreCase(confirmacao)) {
            System.out.println("Remoção cancelada.");
            return;
        }

        controller.remover(cpf);
        System.out.println("Profissional removido com sucesso.");
    }

    // ---------- Apresentação ----------

    private String formatar(Profissional p) {
        return p.getNome() + " | CPF: " + p.getCpf() + " | Especialidade: " + p.getEspecialidade();
    }
}