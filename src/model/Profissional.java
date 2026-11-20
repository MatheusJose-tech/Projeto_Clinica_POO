package model;

import model.exception.DadosInvalidosException;

public class Profissional extends Pessoa {

    private static final long serialVersionUID = 1L;

    private String especialidade;

    public Profissional(String nome, String cpf, String especialidade) throws DadosInvalidosException {
        super(nome, cpf);
        this.especialidade = validarEspecialidade(especialidade);
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) throws DadosInvalidosException {
        this.especialidade = validarEspecialidade(especialidade);
    }

    private static String validarEspecialidade(String especialidade) throws DadosInvalidosException {
        if (especialidade == null || especialidade.isBlank()) {
            throw new DadosInvalidosException("A especialidade não pode ser vazia.");
        }
        return especialidade.trim();
    }

    @Override
    public String toString() {
        return super.toString() + " | Especialidade: " + especialidade;
    }
}
