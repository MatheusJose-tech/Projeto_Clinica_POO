package model;

import java.time.LocalDate;

import model.Profissional;

public class Relatorio {

    private Profissional profissional;
    private LocalDate inicio;
    private LocalDate fim;
    private double receitaTotal;
    private int qtdNormal;
    private int qtdPlano;
    private int qtdRetorno;

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