package model;

import model.exception.DadosInvalidosException;

public class Paciente extends Pessoa {

    private static final long serialVersionUID = 1L;

    private String plano;

    public Paciente(String nome, String cpf, String plano) throws DadosInvalidosException {
        super(nome, cpf);
        this.plano = normalizarPlano(plano);
    }

    public boolean temPlano() {
        return plano != null;
    }

    public String getPlano() {
        return plano;
    }

    public void setPlano(String plano) {
        this.plano = normalizarPlano(plano);
    }

    // Plano vazio ou null significa paciente sem plano.
    private static String normalizarPlano(String plano) {
        return (plano == null || plano.isBlank()) ? null : plano.trim();
    }

    @Override
    public String toString() {
        return super.toString() + " | Plano: " + (temPlano() ? plano : "sem plano");
    }
}
