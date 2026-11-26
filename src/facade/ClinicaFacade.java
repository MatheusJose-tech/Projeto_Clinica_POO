package facade;

import factory.ConsultaCreator;
import factory.NormalCreator;
import factory.PlanoCreator;
import factory.RetornoCreator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.ConfiguracaoAgenda;
import model.Consulta;
import model.Paciente;
import model.Profissional;
import model.Relatorio;
import model.StatusConsulta;
import model.Usuario;
import model.exception.ClinicaException;
import model.exception.CredenciaisInvalidasException;
import model.exception.DadosInvalidosException;
import model.exception.HorarioIndisponivelException;
import repository.ConsultaRepository;
import repository.PacienteRepository;
import repository.ProfissionalRepository;
import repository.UsuarioRepository;
import service.CadastroService;

public class ClinicaFacade {

    private CadastroService cadastroService;
    private ConsultaRepository consultaRepo;
    private UsuarioRepository usuarioRepo;
    private ConfiguracaoAgenda configuracaoAgenda;
    private Map<String, ConsultaCreator> creators;

    public ClinicaFacade(PacienteRepository pacienteRepo, ProfissionalRepository profissionalRepo,
            ConsultaRepository consultaRepo,
            UsuarioRepository usuarioRepo) {
        this(pacienteRepo, profissionalRepo, consultaRepo, usuarioRepo, new ConfiguracaoAgenda());
    }

    public ClinicaFacade(PacienteRepository pacienteRepo, ProfissionalRepository profissionalRepo,
            ConsultaRepository consultaRepo,
            UsuarioRepository usuarioRepo,
            ConfiguracaoAgenda configuracaoAgenda) {
        this.cadastroService = new CadastroService(pacienteRepo, profissionalRepo, consultaRepo);
        this.consultaRepo = consultaRepo;
        this.usuarioRepo = usuarioRepo;
        this.configuracaoAgenda = configuracaoAgenda != null ? configuracaoAgenda : new ConfiguracaoAgenda();

        this.creators = new HashMap<>();
        this.creators.put("NORMAL", new NormalCreator());
        this.creators.put("PLANO", new PlanoCreator());
        this.creators.put("RETORNO", new RetornoCreator());
    }

    public ConfiguracaoAgenda getConfiguracaoAgenda() {
        return configuracaoAgenda;
    }

    public Usuario autenticar(String login, String senha) throws CredenciaisInvalidasException {
        Usuario usuario = usuarioRepo.buscarPorLogin(login);

        if (usuario == null) {
            throw new CredenciaisInvalidasException("Usuário não encontrado.");
        }

        if (!usuario.autenticar(senha)) {
            throw new CredenciaisInvalidasException("Senha inválida.");
        }

        return usuario;
    }

    public void cadastrarPaciente(String nome, String cpf, String plano) throws DadosInvalidosException {
        cadastroService.cadastrarPaciente(nome, cpf, plano);
    }

    public Paciente buscarPacientePorCpf(String cpf) throws DadosInvalidosException {
        return cadastroService.buscarPacientePorCpf(cpf);
    }

    public List<Paciente> listarPacientes() {
        return cadastroService.listarPacientes();
    }

    public void alterarPaciente(String cpf, String nome, String plano) throws DadosInvalidosException {
        cadastroService.alterarPaciente(cpf, nome, plano);
    }

    public void removerPaciente(String cpf) throws DadosInvalidosException {
        cadastroService.removerPaciente(cpf);
    }

    public void cadastrarProfissional(String nome, String cpf, String especialidade) throws DadosInvalidosException {
        cadastroService.cadastrarProfissional(nome, cpf, especialidade);
    }

    public Profissional buscarProfissionalPorCpf(String cpf) throws DadosInvalidosException {
        return cadastroService.buscarProfissionalPorCpf(cpf);
    }

    public List<Profissional> listarProfissionais() {
        return cadastroService.listarProfissionais();
    }

    public void alterarProfissional(String cpf, String nome, String especialidade) throws DadosInvalidosException {
        cadastroService.alterarProfissional(cpf, nome, especialidade);
    }

    public void removerProfissional(String cpf) throws DadosInvalidosException {
        cadastroService.removerProfissional(cpf);
    }

    public Map<String, String> listarTiposDisponiveis(String cpfPaciente) throws DadosInvalidosException {
        Paciente paciente = buscarPacientePorCpf(cpfPaciente);
        List<Consulta> historico = consultaRepo.listar();
        LocalDate hoje = LocalDate.now();
        Map<String, String> resultado = new LinkedHashMap<>();

        for (String tipo : List.of("NORMAL", "PLANO", "RETORNO")) {
            ConsultaCreator creator = creators.get(tipo);
            boolean disponivel = false;
            String motivoIndisponivel = null;

            for (int i = 0; i < configuracaoAgenda.getDiasAntecedencia(); i++) {
                LocalDate dia = hoje.plusDays(i);
                if (!configuracaoAgenda.funcionaNoDia(dia)) {
                    continue;
                }
                try {
                    creator.validarElegibilidade(paciente, null, dia, historico);
                    disponivel = true;
                    break;
                } catch (ClinicaException e) {
                    if (motivoIndisponivel == null) {
                        motivoIndisponivel = e.getMessage();
                    }
                }
            }

            if (disponivel) {
                resultado.put(tipo, null);
            } else {
                if (motivoIndisponivel == null) {
                    try {
                        creator.validarElegibilidade(paciente, null, hoje, historico);
                    } catch (ClinicaException e) {
                        motivoIndisponivel = e.getMessage();
                    }
                }
                resultado.put(tipo, motivoIndisponivel);
            }
        }

        return resultado;
    }

    public List<LocalDate> listarDiasDisponiveis(String tipo, String cpfPaciente, String cpfProfissional)
            throws ClinicaException {
        ConsultaCreator creator = obterCreator(tipo);
        Paciente paciente = buscarPacientePorCpf(cpfPaciente);
        Profissional profissional = buscarProfissionalPorCpf(cpfProfissional);

        if (paciente.getCpf().equals(profissional.getCpf())) {
            throw new DadosInvalidosException("Um profissional não pode agendar uma consulta consigo mesmo.");
        }

        List<Consulta> historico = consultaRepo.listar();
        LocalDate hoje = LocalDate.now();
        List<LocalDate> dias = new ArrayList<>();
        boolean elegivelEmAlgumDia = false;
        ClinicaException primeiroErroElegibilidade = null;

        for (int i = 0; i < configuracaoAgenda.getDiasAntecedencia(); i++) {
            LocalDate dia = hoje.plusDays(i);
            if (!configuracaoAgenda.funcionaNoDia(dia)) {
                continue;
            }
            try {
                creator.validarElegibilidade(paciente, profissional, dia, historico);
                elegivelEmAlgumDia = true;
            } catch (ClinicaException e) {
                if (primeiroErroElegibilidade == null) {
                    primeiroErroElegibilidade = e;
                }
                continue;
            }
            if (!calcularHorariosLivres(paciente.getCpf(), profissional.getCpf(), creator.getDuracao(), dia, -1)
                    .isEmpty()) {
                dias.add(dia);
            }
        }

        if (dias.isEmpty() && !elegivelEmAlgumDia && primeiroErroElegibilidade != null) {
            throw primeiroErroElegibilidade;
        }

        return dias;
    }

    public List<LocalTime> listarHorariosLivres(String tipo, String cpfPaciente, String cpfProfissional, LocalDate dia)
            throws DadosInvalidosException {
        ConsultaCreator creator = obterCreator(tipo);
        Paciente paciente = buscarPacientePorCpf(cpfPaciente);
        Profissional profissional = buscarProfissionalPorCpf(cpfProfissional);

        if (paciente.getCpf().equals(profissional.getCpf())) {
            throw new DadosInvalidosException("Um profissional não pode agendar uma consulta consigo mesmo.");
        }

        return calcularHorariosLivres(paciente.getCpf(), profissional.getCpf(), creator.getDuracao(), dia, -1);
    }

    public List<LocalDate> listarDiasDisponiveisRemarcacao(int idConsulta) throws DadosInvalidosException {
        Consulta consulta = consultaRepo.buscarPorId(idConsulta);
        if (consulta == null) {
            throw new DadosInvalidosException("Consulta não encontrada.");
        }

        ConsultaCreator creator = obterCreator(consulta.getTipo());
        List<Consulta> historico = listarHistoricoSemConsulta(idConsulta);
        LocalDate hoje = LocalDate.now();
        List<LocalDate> dias = new ArrayList<>();

        for (int i = 0; i < configuracaoAgenda.getDiasAntecedencia(); i++) {
            LocalDate dia = hoje.plusDays(i);
            if (!configuracaoAgenda.funcionaNoDia(dia)) {
                continue;
            }
            try {
                creator.validarElegibilidade(consulta.getPaciente(), consulta.getProfissional(), dia, historico);
            } catch (ClinicaException e) {
                continue;
            }
            if (!calcularHorariosLivres(consulta.getPaciente().getCpf(), consulta.getProfissional().getCpf(),
                    consulta.getDuracao(), dia, consulta.getId()).isEmpty()) {
                dias.add(dia);
            }
        }

        return dias;
    }

    public List<LocalTime> listarHorariosLivresRemarcacao(int idConsulta, LocalDate dia)
            throws DadosInvalidosException {
        Consulta consulta = consultaRepo.buscarPorId(idConsulta);
        if (consulta == null) {
            throw new DadosInvalidosException("Consulta não encontrada.");
        }
        return calcularHorariosLivres(consulta.getPaciente().getCpf(), consulta.getProfissional().getCpf(),
                consulta.getDuracao(), dia, consulta.getId());
    }

    public Consulta agendar(String tipo, String cpfPaciente, String cpfProfissional, LocalDateTime dataHora)
            throws ClinicaException {
        ConsultaCreator creator = obterCreator(tipo);
        Paciente paciente = buscarPacientePorCpf(cpfPaciente);
        Profissional profissional = buscarProfissionalPorCpf(cpfProfissional);

        if (paciente.getCpf().equals(profissional.getCpf())) {
            throw new DadosInvalidosException("Um profissional não pode agendar uma consulta consigo mesmo.");
        }

        validarDentroDaAgenda(dataHora, creator.getDuracao());

        int novoId = consultaRepo.proximoId();
        List<Consulta> historico = consultaRepo.listar();

        Consulta novaConsulta = creator.criar(novoId, paciente, profissional, dataHora, historico);
        consultaRepo.salvar(novaConsulta);

        return novaConsulta;
    }

    public List<Consulta> listarConsultas() {
        return consultaRepo.listar();
    }

    public void cancelarConsulta(int id) throws ClinicaException {
        Consulta consulta = consultaRepo.buscarPorId(id);
        if (consulta == null) {
            throw new DadosInvalidosException("Consulta não encontrada.");
        }

        consulta.cancelar();
        consultaRepo.salvar(consulta);
    }

    public void realizarConsulta(int id) throws ClinicaException {
        Consulta consulta = consultaRepo.buscarPorId(id);
        if (consulta == null) {
            throw new DadosInvalidosException("Consulta não encontrada.");
        }

        consulta.realizar();
        consultaRepo.salvar(consulta);
    }

    public void remarcarConsulta(int id, LocalDateTime novaDataHora) throws ClinicaException {
        Consulta consulta = consultaRepo.buscarPorId(id);
        if (consulta == null) {
            throw new DadosInvalidosException("Consulta não encontrada.");
        }

        if (novaDataHora == null) {
            throw new DadosInvalidosException("A nova data e hora são obrigatórias.");
        }

        validarDentroDaAgenda(novaDataHora, consulta.getDuracao());

        ConsultaCreator creator = obterCreator(consulta.getTipo());
        List<Consulta> historico = listarHistoricoSemConsulta(id);
        creator.validarElegibilidade(consulta.getPaciente(), consulta.getProfissional(), novaDataHora.toLocalDate(),
                historico);

        LocalDateTime fimNova = novaDataHora.plusMinutes(consulta.getDuracao());
        if (existeConflitoHorario(consulta.getPaciente().getCpf(), consulta.getProfissional().getCpf(),
                novaDataHora, fimNova, historico, consulta.getId())) {
            throw new HorarioIndisponivelException(
                    "Conflito de horário: o profissional ou o paciente já possui compromisso neste horário.");
        }

        consulta.remarcar(novaDataHora);
        consultaRepo.salvar(consulta);
    }

    public Relatorio gerarRelatorio(String cpfProfissional, LocalDate inicio, LocalDate fim) throws ClinicaException {
        Profissional profissional = profissionalRepo.buscarPorCpf(cpfProfissional);
        if (profissional == null) {
            throw new DadosInvalidosException("Profissional não encontrado.");
        }

        if (inicio == null || fim == null) {
            throw new DadosInvalidosException("As datas inicial e final são obrigatórias.");
        }

        if (inicio.isAfter(fim)) {
            throw new DadosInvalidosException("Periodo inválido: a data inicial não pode ser posterior a data final.");
        }

        double receitaTotal = 0.0;
        int qtdNormal = 0;
        int qtdPlano = 0;
        int qtdRetorno = 0;

        for (Consulta c : consultaRepo.listar()) {
            if (c.getStatus() == StatusConsulta.CANCELADA || c.getStatus() == StatusConsulta.AGENDADA) {
                continue;
            }

            if (!c.getProfissional().getCpf().equals(cpfProfissional)) {
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

    private ConsultaCreator obterCreator(String tipo) throws DadosInvalidosException {
        if (tipo == null || tipo.isBlank()) {
            throw new DadosInvalidosException("Tipo de consulta obrigatório.");
        }
        ConsultaCreator creator = creators.get(tipo.trim().toUpperCase());
        if (creator == null) {
            throw new DadosInvalidosException("Tipo de consulta inválido: " + tipo);
        }
        return creator;
    }

    private void validarDentroDaAgenda(LocalDateTime dataHora, int duracaoMinutos) throws DadosInvalidosException {
        if (dataHora == null) {
            throw new DadosInvalidosException("A data e hora da consulta são obrigatórias.");
        }

        if (!configuracaoAgenda.funcionaNoDia(dataHora.toLocalDate())) {
            throw new DadosInvalidosException("A clínica não funciona neste dia da semana.");
        }

        LocalDateTime abertura = LocalDateTime.of(dataHora.toLocalDate(), configuracaoAgenda.getHorarioAbertura());
        LocalDateTime fechamento = LocalDateTime.of(dataHora.toLocalDate(), configuracaoAgenda.getHorarioFechamento());
        LocalDateTime fimConsulta = dataHora.plusMinutes(duracaoMinutos);

        if (dataHora.isBefore(abertura) || fimConsulta.isAfter(fechamento)) {
            throw new DadosInvalidosException("Horário fora do expediente da clínica ("
                    + configuracaoAgenda.getHorarioAbertura() + " às "
                    + configuracaoAgenda.getHorarioFechamento() + ").");
        }
    }

    private List<LocalTime> calcularHorariosLivres(String cpfPaciente, String cpfProfissional, int duracaoMinutos,
            LocalDate dia, int idConsultaIgnorada) {
        List<LocalTime> livres = new ArrayList<>();
        if (dia == null || !configuracaoAgenda.funcionaNoDia(dia)) {
            return livres;
        }

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime inicioSlot = LocalDateTime.of(dia, configuracaoAgenda.getHorarioAbertura());
        LocalDateTime limiteFechamento = LocalDateTime.of(dia, configuracaoAgenda.getHorarioFechamento());
        int intervalo = configuracaoAgenda.getIntervaloMinutos();
        List<Consulta> historico = consultaRepo.listar();

        while (!inicioSlot.plusMinutes(duracaoMinutos).isAfter(limiteFechamento)) {
            LocalDateTime fimSlot = inicioSlot.plusMinutes(duracaoMinutos);
            if (inicioSlot.isAfter(agora)
                    && !existeConflitoHorario(cpfPaciente, cpfProfissional, inicioSlot, fimSlot, historico,
                            idConsultaIgnorada)) {
                livres.add(inicioSlot.toLocalTime());
            }
            inicioSlot = inicioSlot.plusMinutes(intervalo);
        }

        return livres;
    }

    private boolean existeConflitoHorario(String cpfPaciente, String cpfProfissional, LocalDateTime inicioNova,
            LocalDateTime fimNova, List<Consulta> historico, int idConsultaIgnorada) {
        for (Consulta existente : historico) {
            if (existente.getId() == idConsultaIgnorada) {
                continue;
            }
            if (existente.getStatus() == StatusConsulta.CANCELADA) {
                continue;
            }

            LocalDateTime inicioExistente = existente.getDataHora();
            LocalDateTime fimExistente = inicioExistente.plusMinutes(existente.getDuracao());
            boolean sobreposicao = inicioNova.isBefore(fimExistente) && fimNova.isAfter(inicioExistente);

            if (!sobreposicao) {
                continue;
            }

            String cpfProfExistente = existente.getProfissional().getCpf();
            String cpfPacExistente = existente.getPaciente().getCpf();

            boolean conflitoProfissional = cpfProfissional != null
                    && (cpfProfExistente.equals(cpfProfissional) || cpfPacExistente.equals(cpfProfissional));

            boolean conflitoPaciente = cpfPaciente != null
                    && (cpfPacExistente.equals(cpfPaciente) || cpfProfExistente.equals(cpfPaciente));

            if (conflitoProfissional || conflitoPaciente) {
                return true;
            }
        }
        return false;
    }

    private List<Consulta> listarHistoricoSemConsulta(int idConsultaIgnorada) {
        List<Consulta> filtradas = new ArrayList<>();
        for (Consulta c : consultaRepo.listar()) {
            if (c.getId() != idConsultaIgnorada) {
                filtradas.add(c);
            }
        }
        return filtradas;
    }
}