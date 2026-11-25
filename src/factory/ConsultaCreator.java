package factory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import model.Consulta;
import model.Paciente;
import model.Profissional;
import model.StatusConsulta;
import model.exception.ClinicaException;
import model.exception.DadosInvalidosException;
import model.exception.HorarioIndisponivelException;

public abstract class ConsultaCreator {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Consulta criar(int id, Paciente paciente, Profissional profissional, LocalDateTime dataHora,
            List<Consulta> historico) throws ClinicaException {
        if (paciente != null && profissional != null && paciente.getCpf().equals(profissional.getCpf())) {
            throw new DadosInvalidosException("Um profissional não pode agendar uma consulta consigo mesmo.");
        }

        validarHorario(paciente, profissional, dataHora, historico);
        validarElegibilidade(paciente, profissional, dataHora.toLocalDate(), historico);

        return criarConsulta(id, paciente, profissional, dataHora, historico);
    }

    public abstract int getDuracao();

    public abstract void validarElegibilidade(Paciente paciente, Profissional profissional, LocalDate data,
            List<Consulta> historico) throws ClinicaException;

    protected abstract Consulta criarConsulta(int id, Paciente paciente, Profissional profissional,
            LocalDateTime dataHora, List<Consulta> historico) throws ClinicaException;

    private void validarHorario(Paciente paciente, Profissional profissional, LocalDateTime dataHora,
            List<Consulta> historico) throws ClinicaException {
        if (paciente == null || profissional == null || dataHora == null) {
            throw new DadosInvalidosException("Paciente, profissional e data/hora da consulta são obrigatórios.");
        }

        if (historico == null) {
            return;
        }

        LocalDateTime inicioNova = dataHora;
        LocalDateTime fimNova = dataHora.plusMinutes(getDuracao());
        String cpfProfissional = profissional.getCpf();
        String cpfPaciente = paciente.getCpf();

        for (Consulta existente : historico) {
            if (existente.getStatus() != StatusConsulta.AGENDADA) {
                continue;
            }

            LocalDateTime inicioExistente = existente.getDataHora();
            LocalDateTime fimExistente = inicioExistente.plusMinutes(existente.getDuracao());
            boolean sobreposicao = inicioNova.isBefore(fimExistente) && fimNova.isAfter(inicioExistente);

            if (!sobreposicao) {
                continue;
            }

            String cpfProfExistente = existente.getProfissional().getCpf();
            String cpfPacExistente = existente.getPaciente().getCpf();

            // 1. Verifica se o profissional está ocupado (atendendo ou como paciente)
            if (cpfProfExistente.equals(cpfProfissional) || cpfPacExistente.equals(cpfProfissional)) {
                throw new HorarioIndisponivelException("O profissional " + profissional.getNome()
                        + " já possui um compromisso agendado que conflita com o horário " + dataHora.format(FORMATO)
                        + ".");
            }

            // 2. Verifica se o paciente já tem consulta (como paciente ou atendendo como
            // profissional)
            if (cpfPacExistente.equals(cpfPaciente) || cpfProfExistente.equals(cpfPaciente)) {
                throw new HorarioIndisponivelException("O paciente " + paciente.getNome()
                        + " já possui um compromisso agendado que conflita com o horário " + dataHora.format(FORMATO)
                        + ".");
            }
        }
    }
}