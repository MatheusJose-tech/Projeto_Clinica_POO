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
    public void validarElegibilidade(Paciente paciente, LocalDate data, List<Consulta> historico) throws ClinicaException {
        if (paciente == null) {
            throw new DadosInvalidosException("O paciente da consulta é obrigatório.");
        }

        if (data == null) {
            throw new DadosInvalidosException("A data da consulta é obrigatória.");
        }

        Consulta ultima = buscarUltimaRealizada(paciente, historico);

        if (ultima == null) {
            throw new RetornoForaDoPrazoException("O paciente " + paciente.getNome() + " não tem nenhuma consulta realizada para ter retorno.");
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
    protected Consulta criarConsulta(int id, Paciente paciente, Profissional profissional, LocalDateTime dataHora, List<Consulta> historico) throws ClinicaException {
        return new ConsultaRetorno(id, paciente, profissional, dataHora);
    }

    // Busca a última consulta realizada do paciente no histórico. Retorna null se não houver nenhuma consulta realizada.

    private Consulta buscarUltimaRealizada(Paciente paciente, List<Consulta> historico) { 
        
        Consulta ultima = null;
        
        if (historico == null) {
            return null;
        }
        
        for (Consulta c : historico) {
            
            boolean mesmoPaciente = c.getPaciente().getCpf().equals(paciente.getCpf());
            
            if (mesmoPaciente && c.getStatus() == StatusConsulta.REALIZADA
                    && (ultima == null || c.getDataHora().isAfter(ultima.getDataHora()))) {
                ultima = c;
            }
        }
        return ultima;
    }
}