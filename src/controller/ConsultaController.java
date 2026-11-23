package controller;

import facade.ClinicaFacade;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import model.Consulta;
import model.Relatorio;
import model.exception.ClinicaException;

public class ConsultaController {
    private final ClinicaFacade facade;

    public ConsultaController(ClinicaFacade facade) {
        this.facade = facade;
    }

    public Consulta agendar(String tipo, String cpfPaciente, String cpfProfissional, LocalDateTime dataHora)
            throws ClinicaException {
        return facade.agendar(tipo, cpfPaciente, cpfProfissional, dataHora);
    }

    public void cancelar(int id) throws ClinicaException {
        facade.cancelarConsulta(id);
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