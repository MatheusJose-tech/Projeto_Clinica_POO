package model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import model.exception.DadosInvalidosException;

public class ConfiguracaoAgenda implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalTime horarioAbertura;
    private LocalTime horarioFechamento;
    private int intervaloMinutos;
    private int diasAntecedencia;
    private Set<DayOfWeek> diasFuncionamento;

    public ConfiguracaoAgenda() {
        this.horarioAbertura = LocalTime.of(8, 0);
        this.horarioFechamento = LocalTime.of(18, 0);
        this.intervaloMinutos = 30;
        this.diasAntecedencia = 14;
        this.diasFuncionamento = EnumSet.allOf(DayOfWeek.class);
    }

    public ConfiguracaoAgenda(LocalTime horarioAbertura, LocalTime horarioFechamento,
            int intervaloMinutos, int diasAntecedencia, Set<DayOfWeek> diasFuncionamento)
            throws DadosInvalidosException {
        
        validarHorarios(horarioAbertura, horarioFechamento);
        validarPositivo(intervaloMinutos, "O intervalo em minutos deve ser maior que zero.");
        validarPositivo(diasAntecedencia, "A quantidade de dias de antecedência deve ser maior que zero.");
        validarDiasFuncionamento(diasFuncionamento);

        this.horarioAbertura = horarioAbertura;
        this.horarioFechamento = horarioFechamento;
        this.intervaloMinutos = intervaloMinutos;
        this.diasAntecedencia = diasAntecedencia;
        this.diasFuncionamento = EnumSet.copyOf(diasFuncionamento);
    }

    public boolean funcionaNoDia(LocalDate data) {
        
        return data != null && diasFuncionamento.contains(data.getDayOfWeek());
    }

    public LocalTime getHorarioAbertura() {
        
        return horarioAbertura;
    }

    public void setHorarioAbertura(LocalTime horarioAbertura) throws DadosInvalidosException {
        
        validarHorarios(horarioAbertura, this.horarioFechamento);
        this.horarioAbertura = horarioAbertura;
    }

    public LocalTime getHorarioFechamento() {
        
        return horarioFechamento;
    }

    public void setHorarioFechamento(LocalTime horarioFechamento) throws DadosInvalidosException {
        
        validarHorarios(this.horarioAbertura, horarioFechamento);
        this.horarioFechamento = horarioFechamento;
    }

    public int getIntervaloMinutos() {
        return intervaloMinutos;
    }

    public void setIntervaloMinutos(int intervaloMinutos) throws DadosInvalidosException {
        
        validarPositivo(intervaloMinutos, "O intervalo em minutos deve ser maior que zero.");
        this.intervaloMinutos = intervaloMinutos;
    }

    public int getDiasAntecedencia() {
        return diasAntecedencia;
    }

    public void setDiasAntecedencia(int diasAntecedencia) throws DadosInvalidosException {
        
        validarPositivo(diasAntecedencia, "A quantidade de dias de antecedência deve ser maior que zero.");
        this.diasAntecedencia = diasAntecedencia;
    }

    public Set<DayOfWeek> getDiasFuncionamento() {
        return Collections.unmodifiableSet(diasFuncionamento);
    }

    public void setDiasFuncionamento(Set<DayOfWeek> diasFuncionamento) throws DadosInvalidosException {
        
        validarDiasFuncionamento(diasFuncionamento);
        this.diasFuncionamento = EnumSet.copyOf(diasFuncionamento);
    
    }

    public void setFuncionaFimDeSemana(boolean funciona) throws DadosInvalidosException {
        EnumSet<DayOfWeek> atualizados = EnumSet.copyOf(this.diasFuncionamento);
        
        if (funciona) {
            atualizados.add(DayOfWeek.SATURDAY);
            atualizados.add(DayOfWeek.SUNDAY);
        } else {
            atualizados.remove(DayOfWeek.SATURDAY);
            atualizados.remove(DayOfWeek.SUNDAY);
        }
        validarDiasFuncionamento(atualizados);
        this.diasFuncionamento = atualizados;
    }

    private static void validarHorarios(LocalTime abertura, LocalTime fechamento) throws DadosInvalidosException {
        
        if (abertura == null || fechamento == null) {
            throw new DadosInvalidosException("Os horários de abertura e fechamento são obrigatórios.");
        }
        if (!abertura.isBefore(fechamento)) {
            throw new DadosInvalidosException("O horário de abertura deve ser anterior ao horário de fechamento.");
        }
    }

    private static void validarPositivo(int valor, String mensagem) throws DadosInvalidosException {
        
        if (valor <= 0) {
            throw new DadosInvalidosException(mensagem);
        }
    }

    private static void validarDiasFuncionamento(Set<DayOfWeek> dias) throws DadosInvalidosException {
        
        if (dias == null || dias.isEmpty()) {
            throw new DadosInvalidosException("A clínica deve funcionar em pelo menos um dia da semana.");
        }
    }
}