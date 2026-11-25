package factory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import model.Consulta;
import model.ConsultaRetorno;
import model.Paciente;
import model.Profissional;
import model.StatusConsulta;
import model.exception.ClinicaException;
import model.exception.DadosInvalidosException;
import model.exception.RetornoForaDoPrazoException;

public class RetornoCreator extends ConsultaCreator {

    private static final int PRAZO_DIAS = 30;
    private static final int DURACAO = 30;

    @Override
    public int getDuracao() {
        return DURACAO;
    }

    @Override
    public void validarElegibilidade(Paciente paciente, Profissional profissional, LocalDate data,
            List<Consulta> historico) throws ClinicaException {
        if (paciente == null) {
            throw new DadosInvalidosException("O paciente da consulta é obrigatório.");
        }

        if (data == null) {
            throw new DadosInvalidosException("A data da consulta é obrigatória.");
        }

        Consulta ultimaGeral = buscarUltimaRealizada(paciente, null, historico);
        if (ultimaGeral == null) {
            throw new RetornoForaDoPrazoException(
                    "O paciente " + paciente.getNome() + " não tem nenhuma consulta realizada para ter retorno.");
        }

        Consulta ultima = (profissional != null)
                ? buscarUltimaRealizada(paciente, profissional, historico)
                : ultimaGeral;

        if (ultima == null) {
            throw new DadosInvalidosException(
                    "A consulta de retorno deve ser marcada com o mesmo profissional da consulta realizada (último atendimento com "
                            + ultimaGeral.getProfissional().getNome() + ").");
        }

        if (possuiRetornoParaConsulta(paciente, ultima, historico)) {
            throw new DadosInvalidosException("O paciente " + paciente.getNome()
                    + " já possui um retorno agendado ou realizado para esta consulta.");
        }

        LocalDate dataUltima = ultima.getDataHora().toLocalDate();
        long dias = ChronoUnit.DAYS.between(dataUltima, data);

        if (dias < 0) {
            throw new RetornoForaDoPrazoException("O retorno precisa ser marcado depois da última consulta realizada.");
        }

        if (dias > PRAZO_DIAS) {
            throw new RetornoForaDoPrazoException("O retorno só é permitido até " + PRAZO_DIAS
                    + " dias após a última consulta realizada (já se passaram " + dias + " dias).");
        }
    }

    @Override
    protected Consulta criarConsulta(int id, Paciente paciente, Profissional profissional, LocalDateTime dataHora,
            List<Consulta> historico) throws ClinicaException {
        return new ConsultaRetorno(id, paciente, profissional, dataHora);
    }

    private Consulta buscarUltimaRealizada(Paciente paciente, Profissional profissional, List<Consulta> historico) {
        Consulta ultima = null;

        if (historico == null) {
            return null;
        }

        for (Consulta c : historico) {
            boolean mesmoPaciente = c.getPaciente().getCpf().equals(paciente.getCpf());
            boolean mesmoProfissional = (profissional == null)
                    || c.getProfissional().getCpf().equals(profissional.getCpf());

            if (mesmoPaciente && mesmoProfissional && !(c instanceof ConsultaRetorno)
                    && c.getStatus() == StatusConsulta.REALIZADA
                    && (ultima == null || c.getDataHora().isAfter(ultima.getDataHora()))) {
                ultima = c;
            }
        }
        return ultima;
    }

    private boolean possuiRetornoParaConsulta(Paciente paciente, Consulta ultimaRealizada, List<Consulta> historico) {
        if (historico == null) {
            return false;
        }

        for (Consulta c : historico) {
            boolean mesmoPaciente = c.getPaciente().getCpf().equals(paciente.getCpf());
            boolean mesmoProfissional = c.getProfissional().getCpf()
                    .equals(ultimaRealizada.getProfissional().getCpf());
            boolean ehRetornoAtivo = (c instanceof ConsultaRetorno) && c.getStatus() != StatusConsulta.CANCELADA;

            if (mesmoPaciente && mesmoProfissional && ehRetornoAtivo
                    && !c.getDataHora().isBefore(ultimaRealizada.getDataHora())) {
                return true;
            }
        }
        return false;
    }
}