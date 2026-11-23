package controller;
import facade.ClinicaFacade;
import model.exception.CredenciaisInvalidasException;

public class AuthController {
    private final ClinicaFacade facade;

    public AuthController(ClinicaFacade facade) {
        this.facade = facade;
    }

    public void autenticar(String usuario, String senha) throws CredenciaisInvalidasException {
        facade.autenticar(usuario, senha);
  
    }
}