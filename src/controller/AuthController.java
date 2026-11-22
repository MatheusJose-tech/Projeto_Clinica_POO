package controller;
import facade.ClinicaFacade;
import model.Usuario;
import model.exception.ClinicaException;

public class AuthController {
    private final ClinicaFacade facade;

    public AuthController(ClinicaFacade facade) {
        this.facade = facade;
    }

    public Usuario autenticar(String usuario, String senha) throws ClinicaException {
        return facade.autenticar(usuario, senha);

    }
    
}
