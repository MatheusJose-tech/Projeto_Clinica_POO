# Sistema de Clínica

Sistema de gerenciamento de clínica médica desenvolvido em **Java** como Projeto Final da disciplina de **Programação Orientada a Objetos (POO)** na **Universidade Federal de Alagoas (UFAL) — Campus Arapiraca**, sob orientação do **Prof. Patrick H. S. Brito**.

A aplicação realiza o controle de acesso de usuários, o cadastro de pacientes e profissionais, o agendamento inteligente de consultas (Normal, Plano de Saúde e Retorno), o gerenciamento do ciclo de vida dos atendimentos e a emissão de relatórios financeiros e operacionais por profissional e período.

## 📋 Funcionalidades Principais

* **Autenticação de Usuários:** Acesso protegido por login e senha com bloqueio após 3 tentativas inválidas (`LoginView` / `AuthController`).

* **Gestão de Pacientes e Profissionais (CRUD):**

  * Cadastro, listagem, alteração e remoção com validação de CPF (11 dígitos numéricos) e campos obrigatórios.

  * **CPF Cruzado Sincronizado:** Permite que uma mesma pessoa física seja cadastrada tanto como `Paciente` quanto como `Profissional`, exigindo consistência de nome no cadastro e sincronizando alterações automaticamente (`CadastroService`).

  * **Integridade Referencial:** Impede a remoção de pacientes ou profissionais que possuam consultas com status `AGENDADA`.

* **Agendamento Inteligente de Consultas:**

  * Filtragem automática das modalidades elegíveis para o paciente (`NORMAL`, `PLANO`, `RETORNO`), dias disponíveis (janela padrão de 14 dias) e horários livres (expediente padrão de 08h00 às 18h00, intervalos de 30 minutos via `ConfiguracaoAgenda`).

  * **Prevenção de Conflitos com CPF Cruzado:** Bloqueia o autoagendamento (profissional marcar consulta consigo mesmo) e detecta sobreposição de horários considerando o CPF tanto no papel de profissional quanto no de paciente (`ConsultaCreator` / `AgendaService`).

  * **Regras de Negócio por Modalidade:**

    * **Normal (`ConsultaNormal`):** R\$ 150,00 | duração de 30 minutos.

    * **Plano (`ConsultaPlano`):** R\$ 0,00 | duração de 30 minutos (exige que o paciente possua plano de saúde cadastrado).

    * **Retorno (`ConsultaRetorno`):** R\$ 0,00 | duração de 30 minutos (exige consulta `REALIZADA` anterior com o mesmo profissional no prazo máximo de 30 dias e sem retorno duplicado).

* **Ciclo de Vida da Consulta:** Controle de status (`AGENDADA`, `REALIZADA`, `CANCELADA`), permitindo realizar atendimentos, cancelar ou remarcar com revalidação completa de disponibilidade e elegibilidade.

* **Relatórios Gerenciais:** Contabilização de consultas realizadas (separadas por Normal, Plano e Retorno) e receita total gerada por um profissional em um intervalo de datas (`RelatorioService` / `Relatorio`).

## 🏗️ Arquitetura e Padrões de Projeto

O projeto adota a arquitetura em camadas **MVC + Service + Repository**, garantindo baixo acoplamento, alta coesão e isolamento entre interface de usuário, regras de negócio e persistência:

* **MVC (`view` → `controller` → `facade` / `service` / `model`):**

  * `view`: Telas de console (`LoginView`, `MenuPrincipal`, `PacienteView`, `ProfissionalView`, `ConsultaView`) apoiadas pela classe utilitária `Leitor` (aplicando o princípio **DRY** para leitura de entradas via `Scanner`, pausa e limpeza de terminal). Não possui regras de negócio nem acesso direto aos repositórios.

  * `controller`: Controladores específicos (`AuthController`, `PacienteController`, `ProfissionalController`, `ConsultaController`) que recebem as solicitações da camada de visão e acionam a fachada.

* **Facade (`facade.ClinicaFacade`):** Centraliza o ponto único de entrada para os controladores, realiza a autenticação via `UsuarioRepository` e delega as regras de negócio para `CadastroService`, `AgendaService` e `RelatorioService`.

* **Factory Method (`factory.ConsultaCreator`):** Encapsula a validação de elegibilidade, conflito de horário e instanciação das subclasses de `Consulta` por meio das fábricas concretas `NormalCreator`, `PlanoCreator` e `RetornoCreator`.

* **Repository (`repository.*`):** Abstrai o armazenamento por meio de interfaces (`PacienteRepository`, `ProfissionalRepository`, `ConsultaRepository`, `UsuarioRepository`) e implementações em arquivo (`Arquivo*Repository`).

* **Persistência Resiliente:** Serialização binária (`Serializable`) em arquivos `.dat` na pasta `data/`, utilizando escrita em arquivo temporário (`.tmp`) seguida de substituição atômica (`StandardCopyOption.ATOMIC_MOVE`) e backup automático (`.corrompido.bak`) em caso de falha na desserialização.

* **Tratamento de Exceções de Domínio (`model.exception`):** Hierarquia de exceções verificadas herdando de `ClinicaException` (`CredenciaisInvalidasException`, `DadosInvalidosException`, `HorarioIndisponivelException`, `RetornoForaDoPrazoException`).

## 📁 Estrutura do Projeto

```
Projeto_Clinica_POO/
├── data/                        # Arquivos binários (.dat) de persistência gerados em execução
├── docs/                        # Diagrama UML completo e artefatos de documentação
├── src/
│   ├── Main.java                # Ponto de entrada, injeção de dependências e usuário padrão
│   ├── controller/              # Controladores MVC (Auth, Paciente, Profissional, Consulta)
│   ├── facade/                  # ClinicaFacade (interface unificada de acesso ao sistema)
│   ├── factory/                 # Factory Method (ConsultaCreator, NormalCreator, PlanoCreator, RetornoCreator)
│   ├── model/                   # Entidades de domínio (Pessoa, Paciente, Profissional, Usuario, Consulta, etc.)
│   │   └── exception/           # Exceções específicas do domínio (ClinicaException e subclasses)
│   ├── repository/              # Contratos de repositório e implementações com serialização em arquivo
│   ├── service/                 # Serviços de negócio (CadastroService, AgendaService, RelatorioService)
│   └── view/                    # Interfaces de console e utilitário Leitor (DRY)
└── README.md

```

## 🚀 Tecnologias e Execução

* **Linguagem:** Java (JDK 17 ou superior)

* **Bibliotecas Nativas:** `java.time` (`LocalDate`, `LocalTime`, `LocalDateTime`), `java.io.Serializable`, `java.nio.file` (`Files`, `Path`, `StandardCopyOption`), `java.util.Scanner`.

### Como Compilar e Executar

Na raiz do repositório, execute os comandos abaixo:

```
javac -d bin -sourcepath src src/Main.java
java -cp bin Main

```

### Acesso Inicial

Caso o arquivo `data/usuarios.dat` ainda não exista, o sistema cadastra automaticamente o administrador padrão na inicialização (`Main.java`):

* **Login:** `admin`

* **Senha:** `admin123`

## 👨‍💻 Autores

Projeto desenvolvido pela equipe de estudantes de Ciência da Computação — **UFAL Campus Arapiraca**:

* **João Vitor** ([@joaomagalhaes1-bot](https://github.com/joaomagalhaes1-bot))

  * Interface interativa de agendamento, gerenciamento e relatórios de consultas (`view.ConsultaView`).

  * Elaboração do Diagrama de Classes UML completo do sistema (`docs/`).

* **Leoni** ([@leonipadua](https://github.com/leonipadua))

  * Fluxo completo de **Pacientes** e **Profissionais**: entidades de domínio (`model.Pessoa`, `model.Paciente`, `model.Profissional`), persistência em arquivo (`repository.PacienteRepository`, `repository.ArquivoPacienteRepository`, `repository.ProfissionalRepository`, `repository.ArquivoProfissionalRepository`), controladores (`controller.PacienteController`, `controller.ProfissionalController`) e interfaces de console (`view.PacienteView`, `view.ProfissionalView`).

* **Matheus Jose** ([@MatheusJose-tech](https://github.com/MatheusJose-tech))

  * Modelagem e hierarquia de **Consultas** (`model.Consulta`, `model.ConsultaNormal`, `model.ConsultaPlano`, `model.ConsultaRetorno`, `model.StatusConsulta`).

  * Implementação do padrão **Factory Method** (`factory.ConsultaCreator`, `factory.NormalCreator`, `factory.PlanoCreator`, `factory.RetornoCreator`).

  * Controladores de autenticação e consultas (`controller.AuthController`, `controller.ConsultaController`), telas de login e navegação (`view.LoginView`, `view.MenuPrincipal`), utilitário de entrada com princípio DRY (`view.Leitor`), classe principal (`Main.java`) e documentação (`README.md`).

* **Yasmim Maria** ([@Yasmim-Coimbra](https://github.com/Yasmim-Coimbra))

  * Módulo de autenticação e usuários (`model.Usuario`, `repository.UsuarioRepository`, `repository.ArquivoUsuarioRepository`).

  * Persistência de consultas (`repository.ConsultaRepository`, `repository.ArquivoConsultaRepository`) e entidade de relatórios (`model.Relatorio`).

  * Implementação da fachada (`facade.ClinicaFacade`) e refatoração da camada de serviços (`service.CadastroService`, `service.AgendaService`, `service.RelatorioService`).

## Declaração de Uso de Inteligência Artificial

Em conformidade com as diretrizes de transparência acadêmica da disciplina, declaramos o uso de ferramentas de **Inteligência Artificial Generativa** como apoio consultivo durante o desenvolvimento deste projeto:

* **Finalidade e Escopo de Uso:**

  * **Refatoração Arquitetural:** Apoio na decisão de extrair regras de negócio que inicialmente estavam concentradas na `ClinicaFacade` para uma camada dedicada de serviços (`CadastroService`, `AgendaService` e `RelatorioService`), mantendo a fachada enxuta e respeitando o Princípio da Responsabilidade Única (SRP).

  * **Modelagem de Regras de Domínio (CPF Cruzado):** Orientação na estruturação das validações de CPF cruzado, permitindo que uma mesma pessoa física atue como `Paciente` e `Profissional` com sincronização cadastral e prevenção de conflitos de agenda e autoagendamento.

  * **Organização e Revisão:** Apoio na revisão de tratamento de exceções de domínio (`ClinicaException`) e na estruturação da documentação técnica (`README.md`).

* **Controle de Qualidade, Versionamento e Autoria:**

  * Todo código sugerido ou discutido com auxílio de IA foi **analisado, adaptado, compilado e testado manualmente** pelos integrantes da equipe.

  * O desenvolvimento seguiu rigoroso fluxo colaborativo no GitHub por meio de **Pull Requests (PRs)** com descrição explícita de cada mudança, revisão entre os membros da equipe, **Atomic Commits** e padronização com **Conventional Commits**.