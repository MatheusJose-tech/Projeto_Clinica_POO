package service;

import java.util.List;
import model.Consulta;
import model.Paciente;
import model.Profissional;
import model.StatusConsulta;
import model.exception.DadosInvalidosException;
import repository.ConsultaRepository;
import repository.PacienteRepository;
import repository.ProfissionalRepository;

public class CadastroService {

    private final PacienteRepository pacienteRepo;
    private final ProfissionalRepository profissionalRepo;
    private final ConsultaRepository consultaRepo;

    public CadastroService(PacienteRepository pacienteRepo,
            ProfissionalRepository profissionalRepo,
            ConsultaRepository consultaRepo) {
        this.pacienteRepo = pacienteRepo;
        this.profissionalRepo = profissionalRepo;
        this.consultaRepo = consultaRepo;
    }

    // ==================== PACIENTES ====================

    public void cadastrarPaciente(String nome, String cpf, String plano) throws DadosInvalidosException {
        String cpfLimpo = cpf != null ? cpf.trim() : null;

        if (pacienteRepo.buscarPorCpf(cpfLimpo) != null) {
            throw new DadosInvalidosException("Já existe um paciente cadastrado com este CPF.");
        }

        String nomeParaCadastro = nome;
        Profissional profissionalExistente = profissionalRepo.buscarPorCpf(cpfLimpo);
        if (profissionalExistente != null) {
            if (!profissionalExistente.getNome().equalsIgnoreCase(normalizarNome(nome))) {
                throw new DadosInvalidosException(
                        "Este CPF já está cadastrado como profissional com o nome \""
                                + profissionalExistente.getNome()
                                + "\". Para cadastrar como paciente, o nome deve ser idêntico.");
            }
            nomeParaCadastro = profissionalExistente.getNome();
        }

        Paciente novoPaciente = new Paciente(nomeParaCadastro, cpfLimpo, plano);
        pacienteRepo.salvar(novoPaciente);
    }

    public Paciente buscarPacientePorCpf(String cpf) throws DadosInvalidosException {
        if (cpf == null || cpf.isBlank()) {
            throw new DadosInvalidosException("O CPF do paciente é obrigatório.");
        }
        Paciente paciente = pacienteRepo.buscarPorCpf(cpf.trim());
        if (paciente == null) {
            throw new DadosInvalidosException("Paciente não encontrado.");
        }
        return paciente;
    }

    public List<Paciente> listarPacientes() {
        return pacienteRepo.listar();
    }

    public void alterarPaciente(String cpf, String nome, String plano) throws DadosInvalidosException {
        Paciente paciente = buscarPacientePorCpf(cpf);
        paciente.setNome(nome);
        paciente.setPlano(plano);
        pacienteRepo.salvar(paciente);

        Profissional profissionalVinculado = profissionalRepo.buscarPorCpf(paciente.getCpf());
        if (profissionalVinculado != null && !profissionalVinculado.getNome().equals(paciente.getNome())) {
            profissionalVinculado.setNome(paciente.getNome());
            profissionalRepo.salvar(profissionalVinculado);
        }
    }

    public void removerPaciente(String cpf) throws DadosInvalidosException {
        Paciente paciente = buscarPacientePorCpf(cpf);

        for (Consulta c : consultaRepo.listar()) {
            if (c.getStatus() == StatusConsulta.AGENDADA
                    && c.getPaciente().getCpf().equals(paciente.getCpf())) {
                throw new DadosInvalidosException(
                        "Não é possível remover o paciente: existem consultas agendadas vinculadas a ele.");
            }
        }

        pacienteRepo.remover(paciente.getCpf());
    }

    // ==================== PROFISSIONAIS ====================

    public void cadastrarProfissional(String nome, String cpf, String especialidade) throws DadosInvalidosException {
        String cpfLimpo = cpf != null ? cpf.trim() : null;

        if (profissionalRepo.buscarPorCpf(cpfLimpo) != null) {
            throw new DadosInvalidosException("Já existe um profissional cadastrado com este CPF.");
        }

        String nomeParaCadastro = nome;
        Paciente pacienteExistente = pacienteRepo.buscarPorCpf(cpfLimpo);
        if (pacienteExistente != null) {
            if (!pacienteExistente.getNome().equalsIgnoreCase(normalizarNome(nome))) {
                throw new DadosInvalidosException(
                        "Este CPF já está cadastrado como paciente com o nome \""
                                + pacienteExistente.getNome()
                                + "\". Para cadastrar como profissional, o nome deve ser idêntico.");
            }
            nomeParaCadastro = pacienteExistente.getNome();
        }

        Profissional novoProfissional = new Profissional(nomeParaCadastro, cpfLimpo, especialidade);
        profissionalRepo.salvar(novoProfissional);
    }

    public Profissional buscarProfissionalPorCpf(String cpf) throws DadosInvalidosException {
        if (cpf == null || cpf.isBlank()) {
            throw new DadosInvalidosException("O CPF do profissional é obrigatório.");
        }
        Profissional profissional = profissionalRepo.buscarPorCpf(cpf.trim());
        if (profissional == null) {
            throw new DadosInvalidosException("Profissional não encontrado.");
        }
        return profissional;
    }

    public List<Profissional> listarProfissionais() {
        return profissionalRepo.listar();
    }

    public void alterarProfissional(String cpf, String nome, String especialidade) throws DadosInvalidosException {
        Profissional profissional = buscarProfissionalPorCpf(cpf);
        profissional.setNome(nome);
        profissional.setEspecialidade(especialidade);
        profissionalRepo.salvar(profissional);

        Paciente pacienteVinculado = pacienteRepo.buscarPorCpf(profissional.getCpf());
        if (pacienteVinculado != null && !pacienteVinculado.getNome().equals(profissional.getNome())) {
            pacienteVinculado.setNome(profissional.getNome());
            pacienteRepo.salvar(pacienteVinculado);
        }
    }

    public void removerProfissional(String cpf) throws DadosInvalidosException {
        Profissional profissional = buscarProfissionalPorCpf(cpf);

        for (Consulta c : consultaRepo.listar()) {
            if (c.getStatus() == StatusConsulta.AGENDADA
                    && c.getProfissional().getCpf().equals(profissional.getCpf())) {
                throw new DadosInvalidosException(
                        "Não é possível remover o profissional: existem consultas agendadas vinculadas a ele.");
            }
        }

        profissionalRepo.remover(profissional.getCpf());
    }

    // ==================== AUXILIAR ====================

    private String normalizarNome(String nome) {
        return nome == null ? "" : nome.trim().replaceAll("\\s+", " ");
    }
}
