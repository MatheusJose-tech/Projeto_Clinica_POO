package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import model.exception.DadosInvalidosException;

public abstract class Consulta implements Serializable{

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final int id;
    private final Paciente paciente;
    private final Profissional profissional;
    private final LocalDateTime dataHora;
    private StatusConsulta status;

    protected Consulta(int id, Paciente paciente, Profissional profissional, LocalDateTime dataHora) throws DadosInvalidosException {
        
        if (paciente == null){
            throw new DadosInvalidosException("O paciente da consulta é obrigatório.");
        }
        
        if (profissional == null){
            throw new DadosInvalidosException("O profissional da consulta é obrigatório.");
        }
        
        validarDataHora(dataHora);
        
        this.id = id;
        
        this.paciente = paciente;
        
        this.profissional = profissional;
        
        this.dataHora = dataHora;
        
        this.status = StatusConsulta.AGENDADA;
    }
    
    // ---------- Métodos abstratos ----------

    public abstract double calcularValor();

    public abstract int getDuracao();

    public abstract String getTipo();

    // ---------- Consultas----------

    private void exigirAgendada(String acao) throws DadosInvalidosException {
        if (status != StatusConsulta.AGENDADA) {
            throw new DadosInvalidosException(
                    "A consulta " + id + " não pode ser " + acao + ": status atual é " + status + ".");
        }
    }

    public void realizar() throws DadosInvalidosException {
        exigirAgendada("realizada");
        status = StatusConsulta.REALIZADA;
    }
    
    public void remarcar(LocalDateTime novaDataHora) throws DadosInvalidosException {
        exigirAgendada("remarcada");
        validarDataHora(novaDataHora);
        this.dataHora = novaDataHora;
    }

    public void cancelar() throws DadosInvalidosException {
        exigirAgendada("cancelada");
        status = StatusConsulta.CANCELADA;
    }
    
    public static void validarDataHora(LocalDateTime dataHora) throws DadosInvalidosException {
        if (dataHora == null) {
            throw new DadosInvalidosException("A data e hora da consulta são obrigatórias.");
        }
    
    // ---------- Métodos de acesso ----------
    
    public int getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public StatusConsulta getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("#%d | %s | %s | %s | %s | %s | R$ %.2f",
                id, getTipo(), paciente.getNome(), profissional.getNome(),
                dataHora.format(FORMATO), status, calcularValor());
    }
}
    
