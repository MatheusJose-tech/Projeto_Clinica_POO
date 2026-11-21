package view;

import java.util.List;
import java.util.Scanner;

import controller.ProfissionalController;
import model.Profissional;
import model.exception.ClinicaException;

public class ProfissionalView {

    private final ProfissionalController controller;
    private final Scanner scanner;

    public ProfissionalView(ProfissionalController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println();
            System.out.println("===== PROFISSIONAIS =====");
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
        System.out.println("--- Cadastrar profissional ---");
        String nome = lerTexto("Nome: ");
        String cpf = lerTexto("CPF (11 dígitos, só números): ");
        String especialidade = lerTexto("Especialidade: ");

        controller.cadastrar(nome, cpf, especialidade);
        System.out.println("Profissional cadastrado com sucesso.");
    }

    private void listar() {
        List<Profissional> profissionais = controller.listar();

        if (profissionais.isEmpty()) {
            System.out.println("Nenhum profissional cadastrado.");
            return;
        }

        System.out.println("--- Profissionais cadastrados (" + profissionais.size() + ") ---");
        for (Profissional p : profissionais) {
            System.out.println(p);
        }
    }

    private void alterar() throws ClinicaException {
        System.out.println("--- Alterar profissional ---");
        String cpf = lerTexto("CPF do profissional: ");
        String nome = lerTexto("Novo nome: ");
        String especialidade = lerTexto("Nova especialidade: ");

        controller.alterar(cpf, nome, especialidade);
        System.out.println("Profissional alterado com sucesso.");
    }

    private void remover() throws ClinicaException {
        System.out.println("--- Remover profissional ---");
        String cpf = lerTexto("CPF do profissional: ");
        String confirmacao = lerTexto("Confirma a remoção? (s/n): ");

        if (!"s".equalsIgnoreCase(confirmacao)) {
            System.out.println("Remoção cancelada.");
            return;
        }

        controller.remover(cpf);
        System.out.println("Profissional removido com sucesso.");
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
