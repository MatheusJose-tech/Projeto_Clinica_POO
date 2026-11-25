package controller;

import facade.ClinicaFacade;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import model.Consulta;
import model.Paciente;
import model.Profissional;
import model.Relatorio;
import model.exception.ClinicaException;
import model.exception.DadosInvalidosException;

public class ConsultaController {
    private final ClinicaFacade facade;

    public ConsultaController(ClinicaFacade facade) {
        this.facade = facade;
    }

    public Paciente buscarPacientePorCpf(String cpf) throws DadosInvalidosException {
        return facade.buscarPacientePorCpf(cpf);
    }

    public List<Profissional> listarProfissionais() {
        return facade.listarProfissionais();
    }

    public Map<String, String> listarTiposDisponiveis(String cpfPaciente) throws ClinicaException {
        return facade.listarTiposDisponiveis(cpfPaciente);
    }

    public List<LocalDate> listarDiasDisponiveis(String tipo, String cpfPaciente, String cpfProfissional)
            throws ClinicaException {
        return facade.listarDiasDisponiveis(tipo, cpfPaciente, cpfProfissional);
    }

    public List<LocalTime> listarHorariosLivres(String tipo, String cpfPaciente, String cpfProfissional, LocalDate dia)
            throws ClinicaException {
        return facade.listarHorariosLivres(tipo, cpfPaciente, cpfProfissional, dia);
    }

    public List<LocalDate> listarDiasDisponiveisRemarcacao(int idConsulta) throws ClinicaException {
        return facade.listarDiasDisponiveisRemarcacao(idConsulta);
    }

    public List<LocalTime> listarHorariosLivresRemarcacao(int idConsulta, LocalDate dia) throws ClinicaException {
        return facade.listarHorariosLivresRemarcacao(idConsulta, dia);
    }

    public Consulta agendar(String tipo, String cpfPaciente, String cpfProfissional, LocalDateTime dataHora)
            throws ClinicaException {
        return facade.agendar(tipo, cpfPaciente, cpfProfissional, dataHora);
    }

    public void cancelar(int id) throws ClinicaException {
        facade.cancelarConsulta(id);
    }

    public void realizar(int id) throws ClinicaException {
        facade.realizarConsulta(id);
    }

    public List<Consulta> listar() {
        return facade.listarConsultas();
    }

    public void remarcar(int id, LocalDateTime novaDataHora) throws ClinicaException {
        facade.remarcarConsulta(id, novaDataHora);
    }

    public Relatorio gerarRelatorio(String cpfProfissional, LocalDate inicio, LocalDate fim) throws ClinicaException {
        return facade.gerarRelatorio(cpfProfissional, inicio, fim);
    }
}