package facade;

import model.Usuario;
import model.exception.CredenciaisInvalidasException;

import repository.UsuarioRepository;

public class ClinicaFacade {

    private UsuarioRepository usuarioRepo;

    public ClinicaFacade(UsuarioRepository usuarioRepo) {
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
}