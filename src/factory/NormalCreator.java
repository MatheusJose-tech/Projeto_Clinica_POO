package factory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import model.Consulta;
import model.ConsultaNormal;
import model.Paciente;
import model.Profissional;
import model.exception.ClinicaException;
import model.exception.DadosInvalidosException;

public class NormalCreator extends ConsultaCreator {

    private static final int DURACAO = 30;

    @Override
    public int getDuracao() {
        return DURACAO;
    }

    @Override
    public void validarElegibilidade(Paciente paciente, LocalDate data, List<Consulta> historico) throws ClinicaException {
        if (paciente == null) {
            throw new DadosInvalidosException("O paciente da consulta é obrigatório.");
        }
        if (data == null) {
            throw new DadosInvalidosException("A data da consulta é obrigatória.");
        }
    }

    @Override
    protected Consulta criarConsulta(int id, Paciente paciente, Profissional profissional, LocalDateTime dataHora, List<Consulta> historico) throws ClinicaException {
        return new ConsultaNormal(id, paciente, profissional, dataHora);
    }
}