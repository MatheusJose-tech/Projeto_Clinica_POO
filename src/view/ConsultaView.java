package view;

import controller.ConsultaController;
import controller.ProfissionalController;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import model.Consulta;
import model.Profissional;
import model.Relatorio;
import model.exception.ClinicaException;

public class ConsultaView {

    private static final DateTimeFormatter DATA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final ConsultaController controller;
    private final ProfissionalController profissionalController;
    private final Leitor leitor;

    public ConsultaView(ConsultaController controller, ProfissionalController profissionalController, Scanner scanner) {
        this.controller = controller;
        this.profissionalController = profissionalController;
        this.leitor = new Leitor(scanner);
    }

    public void exibirMenu() {
        int opcao;
        
        do {
            System.out.println();
            System.out.println("===== CONSULTAS =====");
            System.out.println("1 - Agendar");
            System.out.println("2 - Listar");
            System.out.println("3 - Realizar");
            System.out.println("4 - Cancelar");
            System.out.println("5 - Remarcar");
            System.out.println("6 - Relatório por profissional");
            System.out.println("0 - Voltar");
            opcao = leitor.lerOpcao("Escolha uma opção: ");

            try {
                switch (opcao) {
                    case 1 -> agendar();
                    case 2 -> listar();
                    case 3 -> realizar();
                    case 4 -> cancelar();
                    case 5 -> remarcar();
                    case 6 -> exibirRelatorio();
                    case 0 -> System.out.println("Voltando ao menu principal...");
                    default -> System.out.println("Opção inválida. Digite um número de 0 a 6.");
                }
            } catch (ClinicaException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    public void exibirRelatorio() {
        try {
            
            System.out.println("--- Relatório por profissional ---");
            String cpfProfissional = selecionarProfissional();
            
            if (cpfProfissional == null) {
                return;
            }
            
            LocalDate inicio = lerData("Data inicial (dd/MM/aaaa): ");
            
            if (inicio == null) {
                return;
            }
            
            LocalDate fim = lerData("Data final (dd/MM/aaaa): ");
            if (fim == null) {
                return;
            }

            Relatorio relatorio = controller.gerarRelatorio(cpfProfissional, inicio, fim);
            System.out.println(relatorio);
        
        } catch (ClinicaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void agendar() throws ClinicaException {
        
        System.out.println("--- Agendar consulta ---");
        String cpfPaciente = leitor.lerTexto("CPF do paciente: ");

        Map<String, String> tipos = controller.listarTiposDisponiveis(cpfPaciente);
        List<String> disponiveis = new ArrayList<>();

        System.out.println("Tipos de consulta:");
        for (Map.Entry<String, String> entry : tipos.entrySet()) {
            if (entry.getValue() == null) {
                disponiveis.add(entry.getKey());
                System.out.println(disponiveis.size() + " - " + entry.getKey());
            } else {
                System.out.println("   - " + entry.getKey() + " (indisponível: " + entry.getValue() + ")");
            }
        }

        if (disponiveis.isEmpty()) {
            System.out.println("Nenhum tipo de consulta disponível para este paciente.");
            return;
        }

        int idxTipo = escolherIndice(disponiveis.size(), "Escolha o tipo de consulta: ");
        if (idxTipo == -1) {
            return;
        }
        String tipo = disponiveis.get(idxTipo);

        String cpfProfissional = selecionarProfissional();
        
        if (cpfProfissional == null) {
            return;
        }

        List<LocalDate> dias = controller.listarDiasDisponiveis(tipo, cpfPaciente, cpfProfissional);
        
        if (dias.isEmpty()) {
            System.out.println("Não há dias disponíveis para este profissional.");
            return;
        }

        System.out.println("Dias disponíveis:");
        
        for (int i = 0; i < dias.size(); i++) {
            System.out.println((i + 1) + " - " + dias.get(i).format(DATA_FMT));
        }

        int idxDia = escolherIndice(dias.size(), "Escolha o dia: ");
        
        if (idxDia == -1) {
            return;
        }
        LocalDate diaEscolhido = dias.get(idxDia);

        List<LocalTime> horarios = controller.listarHorariosLivres(tipo, cpfProfissional, diaEscolhido);
        
        if (horarios.isEmpty()) {
            System.out.println("Não há horários livres neste dia.");
            return;
        }

        System.out.println("Horários livres:");
        
        for (int i = 0; i < horarios.size(); i++) {
            System.out.println((i + 1) + " - " + horarios.get(i).format(HORA_FMT));
        }

        int idxHora = escolherIndice(horarios.size(), "Escolha o horário: ");
        
        if (idxHora == -1) {
            return;
        }
        LocalTime horaEscolhida = horarios.get(idxHora);

        LocalDateTime dataHora = LocalDateTime.of(diaEscolhido, horaEscolhida);
        Consulta consulta = controller.agendar(tipo, cpfPaciente, cpfProfissional, dataHora);
        System.out.println("Consulta agendada com sucesso! ID: " + consulta.getId()
                + " | Valor: R$ " + String.format("%.2f", consulta.calcularValor()));
    }

    private void listar() {
        List<Consulta> consultas = controller.listar();

        if (consultas.isEmpty()) {
            System.out.println("Nenhuma consulta cadastrada.");
            return;
        }

        System.out.println("--- Consultas cadastradas (" + consultas.size() + ") ---");
        
        for (Consulta c : consultas) {
            System.out.println(c);
        }
    }

    private void realizar() throws ClinicaException {
        
        System.out.println("--- Realizar consulta ---");
        int id = leitor.lerOpcao("ID da consulta: ");
        
        if (id <= 0) {
            System.out.println("ID inválido.");
            return;
        }

        controller.realizar(id);
        System.out.println("Consulta marcada como realizada.");
    }

    private void cancelar() throws ClinicaException {
        
        System.out.println("--- Cancelar consulta ---");
        int id = leitor.lerOpcao("ID da consulta: ");
        
        if (id <= 0) {
            System.out.println("ID inválido.");
            return;
        }

        String confirmacao = leitor.lerTexto("Confirma o cancelamento? (s/n): ");
        if (!"s".equalsIgnoreCase(confirmacao)) {
            System.out.println("Cancelamento abortado.");
            return;
        }

        controller.cancelar(id);
        System.out.println("Consulta cancelada com sucesso.");
    }

    private void remarcar() throws ClinicaException {
        
        System.out.println("--- Remarcar consulta ---");
        int id = leitor.lerOpcao("ID da consulta: ");
        
        if (id <= 0) {
            System.out.println("ID inválido.");
            return;
        }

        List<LocalDate> dias = controller.listarDiasDisponiveisRemarcacao(id);
        
        if (dias.isEmpty()) {
            System.out.println("Não há dias disponíveis para remarcação.");
            return;
        }

        System.out.println("Dias disponíveis:");
        
        for (int i = 0; i < dias.size(); i++) {
            System.out.println((i + 1) + " - " + dias.get(i).format(DATA_FMT));
        }

        int idxDia = escolherIndice(dias.size(), "Escolha o novo dia: ");
        
        if (idxDia == -1) {
            return;
        }
        LocalDate diaEscolhido = dias.get(idxDia);

        List<LocalTime> horarios = controller.listarHorariosLivresRemarcacao(id, diaEscolhido);
        
        if (horarios.isEmpty()) {
            System.out.println("Não há horários livres neste dia.");
            return;
        }

        System.out.println("Horários livres:");
        
        for (int i = 0; i < horarios.size(); i++) {
            System.out.println((i + 1) + " - " + horarios.get(i).format(HORA_FMT));
        }

        int idxHora = escolherIndice(horarios.size(), "Escolha o novo horário: ");
        
        if (idxHora == -1) {
            return;
        }
        LocalTime horaEscolhida = horarios.get(idxHora);

        controller.remarcar(id, LocalDateTime.of(diaEscolhido, horaEscolhida));
        System.out.println("Consulta remarcada com sucesso.");
    }

    private String selecionarProfissional() throws ClinicaException {
        
        List<Profissional> profissionais = profissionalController.listar();
        
        if (profissionais.isEmpty()) {
            System.out.println("Nenhum profissional cadastrado.");
            return null;
        }

        System.out.println("Profissionais disponíveis:");
        
        for (int i = 0; i < profissionais.size(); i++) {
            Profissional p = profissionais.get(i);
            System.out.println((i + 1) + " - " + p.getNome()
                    + " | Especialidade: " + p.getEspecialidade()
                    + " | CPF: " + p.getCpf());
        }

        int idx = escolherIndice(profissionais.size(), "Escolha o profissional: ");
        
        if (idx == -1) {
            return null;
        }
        return profissionais.get(idx).getCpf();
    }

    private int escolherIndice(int tamanho, String mensagem) {
        
        int escolha = leitor.lerOpcao(mensagem);
        
        if (escolha < 1 || escolha > tamanho) {
            System.out.println("Opção inválida.");
            return -1;
        }
        return escolha - 1;
    }

    private LocalDate lerData(String mensagem) {
        
        String texto = leitor.lerTexto(mensagem);
        
        try {
            return LocalDate.parse(texto, DATA_FMT);
        } catch (DateTimeParseException e) {
            System.out.println("Data inválida. Use o formato dd/MM/aaaa.");
            return null;
        }
    }
}