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

    public Consulta criar(int id, Paciente paciente, Profissional profissional, LocalDateTime dataHora, List<Consulta> historico) throws ClinicaException {
        
        validarHorario(profissional, dataHora, historico);
        validarElegibilidade(paciente, dataHora.toLocalDate(), historico);
        
        return criarConsulta(id, paciente, profissional, dataHora, historico);
    }

    public abstract int getDuracao();

    public abstract void validarElegibilidade(Paciente paciente, LocalDate data, List<Consulta> historico) throws ClinicaException;

    protected abstract Consulta criarConsulta(int id, Paciente paciente, Profissional profissional, LocalDateTime dataHora, List<Consulta> historico) throws ClinicaException;

    private void validarHorario(Profissional profissional, LocalDateTime dataHora, List<Consulta> historico) throws ClinicaException {

        if (profissional == null || dataHora == null) {
            throw new DadosInvalidosException("Profissional e data/hora da consulta são obrigatórios.");
        }

        if (historico == null) {
            return;
        }

        LocalDateTime inicioNova = dataHora;
        LocalDateTime fimNova = dataHora.plusMinutes(getDuracao());

        for (Consulta existente : historico) {
            
            boolean mesmoProfissional = existente.getProfissional().getCpf().equals(profissional.getCpf());
            boolean bloqueio = existente.getStatus() == StatusConsulta.AGENDADA;

            if (mesmoProfissional && bloqueio) {
                LocalDateTime inicioExistente = existente.getDataHora();
                LocalDateTime fimExistente = inicioExistente.plusMinutes(existente.getDuracao());
                boolean sobreposicao = inicioNova.isBefore(fimExistente) && fimNova.isAfter(inicioExistente);

                if (sobreposicao) {
                    throw new HorarioIndisponivelException("O profissional " + profissional.getNome() + " já possui uma consulta agendada que conflita com o horário " + dataHora.format(FORMATO) + ".");
                }
            }
        }
    }
}