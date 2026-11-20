package factory;

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
        
        return criarConsulta(id, paciente, profissional, dataHora, historico);
    }

    protected abstract Consulta criarConsulta(int id, Paciente paciente, Profissional profissional, LocalDateTime dataHora, List<Consulta> historico) throws ClinicaException;

    private void validarHorario(Profissional profissional, LocalDateTime dataHora, List<Consulta> historico) throws ClinicaException {
        if (profissional == null && dataHora == null) {
            throw new DadosInvalidosException("Profissional e data/hora da consulta são obrigatórios.");
        }

        if (historico == null) {
            return;
        }

        for (Consulta existente : historico) {
            
            boolean mesmoProfissional = existente.getProfissional().getCpf().equals(profissional.getCpf());
            boolean mesmoHorario = existente.getDataHora().equals(dataHora);
            boolean bloqueio = existente.getStatus() == StatusConsulta.AGENDADA;

            if (mesmoProfissional && mesmoHorario && bloqueio) {
                throw new HorarioIndisponivelException("O profissional " + profissional.getNome() + " já possui uma consulta agendada para o horário " + dataHora.format(FORMATO) + ".");
            }
        }
    }
}