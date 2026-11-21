package facade;

import model.Paciente;
import model.Usuario;
import model.exception.CredenciaisInvalidasException;

import repository.PacienteRepository;
import repository.UsuarioRepository;

public class ClinicaFacade {

    private PacienteRepository pacienteRepo;
    private UsuarioRepository usuarioRepo;

    public ClinicaFacade(PacienteRepository pacienteRepo, UsuarioRepository usuarioRepo) {
        this.pacienteRepo = pacienteRepo;
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
}