package factory;

import java.time.LocalDateTime;
import java.util.List;

import model.Consulta;
import model.ConsultaNormal;
import model.Paciente;
import model.Profissional;
import model.exception.ClinicaException;

public class NormalCreator extends ConsultaCreator {

    @Override
    protected Consulta criarConsulta(int id, Paciente paciente, Profissional profissional, LocalDateTime dataHora, List<Consulta> historico) throws ClinicaException {
        
        return new ConsultaNormal(id, paciente, profissional, dataHora);
    
    }
}