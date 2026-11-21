package controller;

import java.util.List;

import facade.ClinicaFacade;
import model.Profissional;
import model.exception.ClinicaException;

public class ProfissionalController {

    private final ClinicaFacade facade;

    public ProfissionalController(ClinicaFacade facade) {
        this.facade = facade;
    }

    public void cadastrar(String nome, String cpf, String especialidade) throws ClinicaException {
        facade.cadastrarProfissional(nome, cpf, especialidade);
    }

    public List<Profissional> listar() {
        return facade.listarProfissionais();
    }

    public void alterar(String cpf, String nome, String especialidade) throws ClinicaException {
        facade.alterarProfissional(cpf, nome, especialidade);
    }

    public void remover(String cpf) throws ClinicaException {
        facade.removerProfissional(cpf);
    }
}
