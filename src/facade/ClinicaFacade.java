package facade;

import java.util.List;

import model.Paciente;
import model.Profissional;
import model.Usuario;

import model.exception.ClinicaException;
import model.exception.CredenciaisInvalidasException;
import model.exception.DadosInvalidosException;

import repository.PacienteRepository;
import repository.ProfissionalRepository;
import repository.UsuarioRepository;

public class ClinicaFacade {

    private PacienteRepository pacienteRepo;
    private ProfissionalRepository profissionalRepo;
    private UsuarioRepository usuarioRepo;

    public ClinicaFacade(PacienteRepository pacienteRepo, ProfissionalRepository profissionalRepo,
            UsuarioRepository usuarioRepo) {
        this.pacienteRepo = pacienteRepo;
        this.profissionalRepo = profissionalRepo;
        this.usuarioRepo = usuarioRepo;
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
}