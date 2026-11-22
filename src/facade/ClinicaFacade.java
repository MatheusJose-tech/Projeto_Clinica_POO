package facade;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import factory.ConsultaCreator;
import factory.NormalCreator;
import factory.PlanoCreator;
import factory.RetornoCreator;

import model.Consulta;
import model.Paciente;
import model.Profissional;
import model.Relatorio;
import model.StatusConsulta;
import model.Usuario;

import model.exception.ClinicaException;
import model.exception.CredenciaisInvalidasException;
import model.exception.DadosInvalidosException;
import model.exception.HorarioIndisponivelException;

import repository.ConsultaRepository;
import repository.PacienteRepository;
import repository.ProfissionalRepository;
import repository.UsuarioRepository;

public class ClinicaFacade {

    private PacienteRepository pacienteRepo;
    private ProfissionalRepository profissionalRepo;
    private ConsultaRepository consultaRepo;
    private UsuarioRepository usuarioRepo;
    private Map<String, ConsultaCreator> creators;

    public ClinicaFacade(PacienteRepository pacienteRepo, ProfissionalRepository profissionalRepo,
            ConsultaRepository consultaRepo,
            UsuarioRepository usuarioRepo) {
        this.pacienteRepo = pacienteRepo;
        this.profissionalRepo = profissionalRepo;
        this.consultaRepo = consultaRepo;
        this.usuarioRepo = usuarioRepo;

        this.creators = new HashMap<>();
        this.creators.put("NORMAL", new NormalCreator());
        this.creators.put("PLANO", new PlanoCreator());
        this.creators.put("RETORNO", new RetornoCreator());
    }

    public Usuario autenticar(String login, String senha) throws CredenciaisInvalidasException {
        Usuario usuario = usuarioRepo.buscarPorLogin(login);

        if (usuario == null) {
            throw new CredenciaisInvalidasException("Usuário não encontrado");
        }

        if (!usuario.autenticar(senha)) {
            throw new CredenciaisInvalidasException("Senha inválida");
        }

        return usuario;
    };

    public void cadastrarPaciente(String nome, String cpf, String plano) throws DadosInvalidosException {
        if (pacienteRepo.buscarPorCpf(cpf) != null) {
            throw new DadosInvalidosException("Já existe um paciente cadastrado com este CPF.");
        }

        Paciente novoPaciente = new Paciente(nome, cpf, plano);
        pacienteRepo.salvar(novoPaciente);
    }

    public List<Paciente> listarPacientes() {
        return pacienteRepo.listar();
    }

    public void alterarPaciente(String cpf, String nome, String plano) throws DadosInvalidosException {
        Paciente paciente = pacienteRepo.buscarPorCpf(cpf);
        if (paciente == null) {
            throw new DadosInvalidosException("Paciente não encontrado.");
        }

        paciente.setNome(nome);
        paciente.setPlano(plano);
        pacienteRepo.salvar(paciente);
    }

    public void removerPaciente(String cpf) throws DadosInvalidosException {
        Paciente paciente = pacienteRepo.buscarPorCpf(cpf);
        if (paciente == null) {
            throw new DadosInvalidosException("Paciente não encontrado.");
        }

        pacienteRepo.remover(cpf);
    }

    public void cadastrarProfissional(String nome, String cpf, String especialidade) throws DadosInvalidosException {
        if (profissionalRepo.buscarPorCpf(cpf) != null) {
            throw new DadosInvalidosException("Já existe um profissional cadastrado com este CPF.");
        }

        Profissional novoProfissional = new Profissional(nome, cpf, especialidade);
        profissionalRepo.salvar(novoProfissional);
    }

    public List<Profissional> listarProfissionais() {
        return profissionalRepo.listar();
    }

    public void alterarProfissional(String cpf, String nome, String especialidade) throws DadosInvalidosException {
        Profissional profissional = profissionalRepo.buscarPorCpf(cpf);
        if (profissional == null) {
            throw new DadosInvalidosException("Profissional não encontrado.");
        }

        profissional.setNome(nome);
        profissional.setEspecialidade(especialidade);
        profissionalRepo.salvar(profissional);
    }

    public void removerProfissional(String cpf) throws DadosInvalidosException {
        Profissional profissional = profissionalRepo.buscarPorCpf(cpf);
        if (profissional == null) {
            throw new DadosInvalidosException("Profissional não encontrado.");
        }

        profissionalRepo.remover(cpf);
    }

    public Consulta agendar(String tipo, String cpfPaciente, String cpfProfissional, LocalDateTime dataHora)
            throws ClinicaException {
        if (tipo == null) {
            throw new DadosInvalidosException("Tipo de consulta obrigatório.");
        }

        ConsultaCreator creator = creators.get(tipo.trim().toUpperCase());
        if (creator == null) {
            throw new DadosInvalidosException("Tipo de consulta inválido: " + tipo);
        }

        Paciente paciente = pacienteRepo.buscarPorCpf(cpfPaciente);
        if (paciente == null) {
            throw new DadosInvalidosException("Paciente não encontrado.");
        }

        Profissional profissional = profissionalRepo.buscarPorCpf(cpfProfissional);
        if (profissional == null) {
            throw new DadosInvalidosException("Profissional não encontrado.");
        }

        int novoId = consultaRepo.proximoId();
        List<Consulta> historico = consultaRepo.listar();

        Consulta novaConsulta = creator.criar(novoId, paciente, profissional, dataHora, historico);
        consultaRepo.salvar(novaConsulta);

        return novaConsulta;
    }

    public List<Consulta> listarConsultas() {
        return consultaRepo.listar();
    }

    public void cancelarConsulta(int id) throws ClinicaException {
        Consulta consulta = consultaRepo.buscarPorId(id);
        if (consulta == null) {
            throw new DadosInvalidosException("Consulta não encontrada.");
        }

        consulta.cancelar();
        consultaRepo.salvar(consulta);
    }

    public void remarcarConsulta(int id, LocalDateTime novaDataHora) throws ClinicaException {
        Consulta consulta = consultaRepo.buscarPorId(id);
        if (consulta == null) {
            throw new DadosInvalidosException("Consulta não encontrada.");
        }

        if (novaDataHora == null) {
            throw new DadosInvalidosException("A nova data e hora são obrigatórias.");
        }

        String cpfProfissional = consulta.getProfissional().getCpf();
        for (Consulta outra : consultaRepo.listar()) {
            boolean mesmoProfissional = outra.getProfissional().getCpf().equals(cpfProfissional);
            boolean mesmoHorario = outra.getDataHora().equals(novaDataHora);
            boolean bloqueio = outra.getStatus() == StatusConsulta.AGENDADA;

            if (outra.getId() != consulta.getId() && mesmoProfissional && mesmoHorario && bloqueio) {
                throw new HorarioIndisponivelException("O profissional já possui uma consulta agendada neste horário.");
            }
        }

        consulta.remarcar(novaDataHora);
        consultaRepo.salvar(consulta);
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
            if (c.getStatus() == StatusConsulta.CANCELADA) {
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