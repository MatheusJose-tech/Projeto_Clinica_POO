package factory;

import java.time.LocalDateTime;
import java.util.List;
import model.Consulta;
import model.ConsultaPlano;
import model.Paciente;
import model.Profissional;
import model.exception.ClinicaException;
import model.exception.DadosInvalidosException;

public class PlanoCreator extends ConsultaCreator {

    @Override
    protected Consulta criarConsulta(int id, Paciente paciente, Profissional profissional, LocalDateTime dataHora, List<Consulta> historico) throws ClinicaException {
        
        if (paciente == null) {
            throw new DadosInvalidosException("O paciente da consulta é obrigatório.");
        }
        
        if (!paciente.temPlano()) {
            throw new DadosInvalidosException("O paciente " + paciente.getNome() + " não possui plano de saúde cadastrado.");
        }
        
        return new ConsultaPlano(id, paciente, profissional, dataHora);
    }
}