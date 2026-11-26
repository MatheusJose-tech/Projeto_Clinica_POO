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
import model.StatusConsulta;
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

public class ClinicaFacade {

    private CadastroService cadastroService;
    private AgendaService agendaService;
    private ConsultaRepository consultaRepo;
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
        this.consultaRepo = consultaRepo;
        this.usuarioRepo = usuarioRepo;
        this.configuracaoAgenda = configuracaoAgenda != null ? configuracaoAgenda : new ConfiguracaoAgenda();
        this.agendaService = new AgendaService(consultaRepo, this.cadastroService, this.configuracaoAgenda);
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
        Profissional profissional = profissionalRepo.buscarPorCpf(cpfProfissional);
        if (profissional == null) {
            throw new DadosInvalidosException("Profissional não encontrado.");
        }

        if (inicio == null || fim == null) {
            throw new DadosInvalidosException("As datas inicial e final são obrigatórias.");
        }

        if (inicio.isAfter(fim)) {
            throw new DadosInvalidosException("Periodo inválido: a data inicial não pode ser posterior a data final.");
        }

        double receitaTotal = 0.0;
        int qtdNormal = 0;
        int qtdPlano = 0;
        int qtdRetorno = 0;

        for (Consulta c : consultaRepo.listar()) {
            if (c.getStatus() == StatusConsulta.CANCELADA || c.getStatus() == StatusConsulta.AGENDADA) {
                continue;
            }

            if (!c.getProfissional().getCpf().equals(cpfProfissional)) {
                continue;
            }

            LocalDate dataConsulta = c.getDataHora().toLocalDate();
            if (dataConsulta.isBefore(inicio) || dataConsulta.isAfter(fim)) {
                continue;
            }

            receitaTotal += c.calcularValor();

            String tipo = c.getTipo();
            if ("Normal".equalsIgnoreCase(tipo)) {
                qtdNormal++;
            } else if ("Plano".equalsIgnoreCase(tipo)) {
                qtdPlano++;
            } else if ("Retorno".equalsIgnoreCase(tipo)) {
                qtdRetorno++;
            }
        }

        return new Relatorio(profissional, inicio, fim, receitaTotal, qtdNormal, qtdPlano, qtdRetorno);
    }
}