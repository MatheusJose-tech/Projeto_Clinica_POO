package facade;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import model.ConfiguracaoAgenda;
import model.Consulta;
import model.Paciente;
import model.Profissional;
import model.Relatorio;
import model.Usuario;
import model.exception.ClinicaException;
import model.exception.CredenciaisInvalidasException;
import model.exception.DadosInvalidosException;
import repository.ConsultaRepository;
import repository.PacienteRepository;
import repository.ProfissionalRepository;
import repository.UsuarioRepository;
import service.CadastroService;
import service.AgendaService;
import service.RelatorioService;

public class ClinicaFacade {

    private CadastroService cadastroService;
    private AgendaService agendaService;
    private RelatorioService relatorioService;
    private UsuarioRepository usuarioRepo;
    private ConfiguracaoAgenda configuracaoAgenda;

    public ClinicaFacade(PacienteRepository pacienteRepo, ProfissionalRepository profissionalRepo,
            ConsultaRepository consultaRepo,
            UsuarioRepository usuarioRepo) {
        this(pacienteRepo, profissionalRepo, consultaRepo, usuarioRepo, new ConfiguracaoAgenda());
    }

    public ClinicaFacade(PacienteRepository pacienteRepo, ProfissionalRepository profissionalRepo,
            ConsultaRepository consultaRepo,
            UsuarioRepository usuarioRepo,
            ConfiguracaoAgenda configuracaoAgenda) {
        this.cadastroService = new CadastroService(pacienteRepo, profissionalRepo, consultaRepo);
        this.usuarioRepo = usuarioRepo;
        this.configuracaoAgenda = configuracaoAgenda != null ? configuracaoAgenda : new ConfiguracaoAgenda();
        this.agendaService = new AgendaService(consultaRepo, this.cadastroService, this.configuracaoAgenda);
        this.relatorioService = new RelatorioService(consultaRepo, this.cadastroService);
    }

    public ConfiguracaoAgenda getConfiguracaoAgenda() {
        return configuracaoAgenda;
    }

    public Usuario autenticar(String login, String senha) throws CredenciaisInvalidasException {
        Usuario usuario = usuarioRepo.buscarPorLogin(login);

        if (usuario == null) {
            throw new CredenciaisInvalidasException("Usuário não encontrado.");
        }

        if (!usuario.autenticar(senha)) {
            throw new CredenciaisInvalidasException("Senha inválida.");
        }

        return usuario;
    }

    public void cadastrarPaciente(String nome, String cpf, String plano) throws DadosInvalidosException {
        cadastroService.cadastrarPaciente(nome, cpf, plano);
    }

    public Paciente buscarPacientePorCpf(String cpf) throws DadosInvalidosException {
        return cadastroService.buscarPacientePorCpf(cpf);
    }

    public List<Paciente> listarPacientes() {
        return cadastroService.listarPacientes();
    }

    public void alterarPaciente(String cpf, String nome, String plano) throws DadosInvalidosException {
        cadastroService.alterarPaciente(cpf, nome, plano);
    }

    public void removerPaciente(String cpf) throws DadosInvalidosException {
        cadastroService.removerPaciente(cpf);
    }

    public void cadastrarProfissional(String nome, String cpf, String especialidade) throws DadosInvalidosException {
        cadastroService.cadastrarProfissional(nome, cpf, especialidade);
    }

    public Profissional buscarProfissionalPorCpf(String cpf) throws DadosInvalidosException {
        return cadastroService.buscarProfissionalPorCpf(cpf);
    }

    public List<Profissional> listarProfissionais() {
        return cadastroService.listarProfissionais();
    }

    public void alterarProfissional(String cpf, String nome, String especialidade) throws DadosInvalidosException {
        cadastroService.alterarProfissional(cpf, nome, especialidade);
    }

    public void removerProfissional(String cpf) throws DadosInvalidosException {
        cadastroService.removerProfissional(cpf);
    }

    public Map<String, String> listarTiposDisponiveis(String cpfPaciente) throws DadosInvalidosException {
        return agendaService.listarTiposDisponiveis(cpfPaciente);
    }

    public List<LocalDate> listarDiasDisponiveis(String tipo, String cpfPaciente, String cpfProfissional)
            throws ClinicaException {
        return agendaService.listarDiasDisponiveis(tipo, cpfPaciente, cpfProfissional);
    }

    public List<LocalTime> listarHorariosLivres(String tipo, String cpfPaciente, String cpfProfissional, LocalDate dia)
            throws DadosInvalidosException {
        return agendaService.listarHorariosLivres(tipo, cpfPaciente, cpfProfissional, dia);
    }

    public List<LocalDate> listarDiasDisponiveisRemarcacao(int idConsulta) throws ClinicaException {
        return agendaService.listarDiasDisponiveisRemarcacao(idConsulta);
    }

    public List<LocalTime> listarHorariosLivresRemarcacao(int idConsulta, LocalDate dia)
            throws DadosInvalidosException {
        return agendaService.listarHorariosLivresRemarcacao(idConsulta, dia);
    }

    public Consulta agendar(String tipo, String cpfPaciente, String cpfProfissional, LocalDateTime dataHora)
            throws ClinicaException {
        return agendaService.agendar(tipo, cpfPaciente, cpfProfissional, dataHora);
    }

    public List<Consulta> listarConsultas() {
        return agendaService.listarConsultas();
    }

    public void cancelarConsulta(int id) throws ClinicaException {
        agendaService.cancelarConsulta(id);
    }

    public void realizarConsulta(int id) throws ClinicaException {
        agendaService.realizarConsulta(id);
    }

    public void remarcarConsulta(int id, LocalDateTime novaDataHora) throws ClinicaException {
        agendaService.remarcarConsulta(id, novaDataHora);
    }

    public Relatorio gerarRelatorio(String cpfProfissional, LocalDate inicio, LocalDate fim) throws ClinicaException {
        return relatorioService.gerarRelatorio(cpfProfissional, inicio, fim);
    }
}