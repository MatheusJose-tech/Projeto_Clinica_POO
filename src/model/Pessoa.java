package model;

import java.io.Serializable;

import model.exception.DadosInvalidosException;

public abstract class Pessoa implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;
    private final String cpf;

    protected Pessoa(String nome, String cpf) throws DadosInvalidosException {
        setNome(nome);
        validarCpf(cpf);
        this.cpf = cpf;
    }

    // ---------- Validações ----------

    private static void validarNome(String nome) throws DadosInvalidosException {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome não pode ser vazio.");
        }
    }

    private static void validarCpf(String cpf) throws DadosInvalidosException {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new DadosInvalidosException("CPF inválido: deve conter exatamente 11 dígitos numéricos.");
        }
    }

    // ---------- Métodos de acesso ----------

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) throws DadosInvalidosException {
        validarNome(nome);
        this.nome = nome.trim().replaceAll("\\s+", " ");
    }

    public String getCpf() {
        return cpf;
    }

    @Override
    public String toString() {
        return nome + " (CPF: " + cpf + ")";
    }
}
