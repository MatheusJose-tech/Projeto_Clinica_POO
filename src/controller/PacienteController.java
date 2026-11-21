package controller;

import java.util.List;

import facade.ClinicaFacade;
import model.Paciente;
import model.exception.ClinicaException;

public class PacienteController {

    private final ClinicaFacade facade;

    public PacienteController(ClinicaFacade facade) {
        this.facade = facade;
    }

    public void cadastrar(String nome, String cpf, String plano) throws ClinicaException {
        facade.cadastrarPaciente(nome, cpf, plano);
    }

    public List<Paciente> listar() {
        return facade.listarPacientes();
    }

    public void alterar(String cpf, String nome, String plano) throws ClinicaException {
        facade.alterarPaciente(cpf, nome, plano);
    }

    public void remover(String cpf) throws ClinicaException {
        facade.removerPaciente(cpf);
    }
}
