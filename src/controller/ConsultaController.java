package controller;
import facade.ClinicaFacade;
import java.time.LocalDateTime;
import java.util.List;

public class ConsultaController {
    private final ClinicaFacade facade;

    public ConsultaController(ClinicaFacade facade) {
        this.facade = facade;
    }
    public Consulta agendar(String tipo, String cpfPaciente, String cpfProf, LocalDateTime dataHora) {
        return facade.agendar(tipo, cpfPaciente, cpfProf, dataHora);
    }
    public void cancelar(int id) {
        facade.cancelarConsulta(id);
    }
    public List<Consulta> listar() {
        return facade.listarConsultas();
    }
    public void remarcar(int id, LocalDateTime novaDataHora) {
        facade.remarcarConsulta(id, novaDataHora);
    }
    public Relatorio gerarRelatorio(String cpfProf, LocalDateTime inicio, LocalDateTime fim) {
        return facade.gerarRelatorioConsultas(cpfProf, inicio, fim);
    }


}