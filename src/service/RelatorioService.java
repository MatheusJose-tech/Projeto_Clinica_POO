package service;

import java.time.LocalDate;
import model.Consulta;
import model.Profissional;
import model.Relatorio;
import model.StatusConsulta;
import model.exception.ClinicaException;
import model.exception.DadosInvalidosException;
import repository.ConsultaRepository;

public class RelatorioService {

    private final ConsultaRepository consultaRepo;
    private final CadastroService cadastroService;

    public RelatorioService(ConsultaRepository consultaRepo, CadastroService cadastroService) {
        this.consultaRepo = consultaRepo;
        this.cadastroService = cadastroService;
    }

    public Relatorio gerarRelatorio(String cpfProfissional, LocalDate inicio, LocalDate fim) throws ClinicaException {
        Profissional profissional = cadastroService.buscarProfissionalPorCpf(cpfProfissional);

        if (inicio == null || fim == null) {
            throw new DadosInvalidosException("As datas inicial e final são obrigatórias.");
        }

        if (inicio.isAfter(fim)) {
            throw new DadosInvalidosException("Período inválido: a data inicial não pode ser posterior à data final.");
        }

        double receitaTotal = 0.0;
        int qtdNormal = 0;
        int qtdPlano = 0;
        int qtdRetorno = 0;

        for (Consulta c : consultaRepo.listar()) {
            if (c.getStatus() != StatusConsulta.REALIZADA) {
                continue;
            }

            if (!c.getProfissional().getCpf().equals(profissional.getCpf())) {
                continue;
            }

            LocalDate dataConsulta = c.getDataHora().toLocalDate();
            if (dataConsulta.isBefore(inicio) || dataConsulta.isAfter(fim)) {
                continue;
            }

            receitaTotal += c.calcularValor();

            String tipo = c.getTipo();
            if ("Normal".equalsIgnoreCase(tipo)) {
                qtdNormal++;
            } else if ("Plano".equalsIgnoreCase(tipo)) {
                qtdPlano++;
            } else if ("Retorno".equalsIgnoreCase(tipo)) {
                qtdRetorno++;
            }
        }

        return new Relatorio(profissional, inicio, fim, receitaTotal, qtdNormal, qtdPlano, qtdRetorno);
    }
}