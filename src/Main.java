import controller.AuthController;
import controller.ConsultaController;
import controller.PacienteController;
import controller.ProfissionalController;
import facade.ClinicaFacade;
import java.util.Scanner;
import model.Usuario;
import repository.ArquivoConsultaRepository;
import repository.ArquivoPacienteRepository;
import repository.ArquivoProfissionalRepository;
import repository.ArquivoUsuarioRepository;
import repository.ConsultaRepository;
import repository.PacienteRepository;
import repository.ProfissionalRepository;
import repository.UsuarioRepository;
import view.ConsultaView;
import view.LoginView;
import view.MenuPrincipal;
import view.PacienteView;
import view.ProfissionalView;

public class Main {

    public static void main(String[] args) {
        
        PacienteRepository pacienteRepo = new ArquivoPacienteRepository("data/pacientes.dat");
        ProfissionalRepository profissionalRepo = new ArquivoProfissionalRepository("data/profissionais.dat");
        ConsultaRepository consultaRepo = new ArquivoConsultaRepository("data/consultas.dat");
        UsuarioRepository usuarioRepo = new ArquivoUsuarioRepository("data/usuarios.dat");

        if (usuarioRepo.buscarPorLogin("admin") == null) {
            usuarioRepo.salvar(new Usuario("admin", "admin123"));
        }

        ClinicaFacade facade = new ClinicaFacade(pacienteRepo, profissionalRepo, consultaRepo, usuarioRepo);

        AuthController authController = new AuthController(facade);
        PacienteController pacienteController = new PacienteController(facade);
        ProfissionalController profissionalController = new ProfissionalController(facade);
        ConsultaController consultaController = new ConsultaController(facade);

        try (Scanner scanner = new Scanner(System.in)) {
            
            LoginView loginView = new LoginView(authController, scanner);
            PacienteView pacienteView = new PacienteView(pacienteController, scanner);
            ProfissionalView profissionalView = new ProfissionalView(profissionalController, scanner);
            ConsultaView consultaView = new ConsultaView(consultaController, profissionalController, scanner);
            MenuPrincipal menuPrincipal = new MenuPrincipal(pacienteView, profissionalView, consultaView, scanner);

            if (loginView.autenticar()) {
                menuPrincipal.exibirMenu();
            }
        }
    }
}