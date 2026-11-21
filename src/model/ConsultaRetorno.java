package model;

import java.time.LocalDateTime;
import model.exception.DadosInvalidosException;

public class ConsultaRetorno extends Consulta {

    private static final long serialVersionUID = 1L;
    private static final double VALOR = 0.0;
    private static final int DURACAO = 20;

    public ConsultaRetorno(int id, Paciente paciente, Profissional profissional, LocalDateTime dataHora) throws DadosInvalidosException {
        super(id, paciente, profissional, dataHora);
    }

    @Override
    public double calcularValor() {
        return VALOR;
    }

    @Override
    public int getDuracao() {
        return DURACAO;
    }

    @Override
    public String getTipo() {
        return "Retorno";
    }
}