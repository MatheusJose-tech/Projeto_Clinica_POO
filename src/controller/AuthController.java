package controller;
import facade.ClinicaFacade;

public class AuthController {
    private final ClinicaFacade facade;

    public AuthController(ClinicaFacade facade) {
        this.facade = facade;
    }

    public void autenticar(String usuario, String senha) {
        facade.autenticar(usuario, senha);
  
    }
}