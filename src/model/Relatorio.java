package model;

import java.time.LocalDate;

public class Relatorio {
    private final Profissional profissional;
    private final LocalDate inicio;
    private final LocalDate fim;
    private final double receitaTotal;
    private final int qtdNormal;
    private final int qtdPlano;
    private final int qtdRetorno;

    public Relatorio(Profissional profissional, LocalDate inicio, LocalDate fim,
            double receitaTotal, int qtdNormal, int qtdPlano, int qtdRetorno) {
        this.profissional = profissional;
        this.inicio = inicio;
        this.fim = fim;
        this.receitaTotal = receitaTotal;
        this.qtdNormal = qtdNormal;
        this.qtdPlano = qtdPlano;
        this.qtdRetorno = qtdRetorno;
    }

    public double getReceitaTotal() {
        return receitaTotal;
    }

    public int getTotalConsultas() {
        return qtdNormal + qtdPlano + qtdRetorno;
    }

    public int getQtdNormal() {
        return qtdNormal;
    }

    public int getQtdPlano() {
        return qtdPlano;
    }

    public int getQtdRetorno() {
        return qtdRetorno;
    }
}