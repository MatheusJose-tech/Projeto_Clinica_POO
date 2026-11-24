package view;

import java.util.Scanner;

import controller.AuthController;
import model.exception.CredenciaisInvalidasException;

public class LoginView {

    private static final int MAX_TENTATIVAS = 3;

    private final AuthController controller;
    private final Leitor leitor;

    public LoginView(AuthController controller, Scanner scanner) {
        this.controller = controller;
        this.leitor = new Leitor(scanner);
    }

    public boolean autenticar() {
        leitor.limparTela();
        System.out.println("===== LOGIN =====");

        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS; tentativa++) {
            String login = leitor.lerTexto("Login: ");
            String senha = leitor.lerTexto("Senha: ");

            try {
                controller.login(login, senha);
                System.out.println("Login realizado com sucesso!");
                leitor.pausar();
                return true;
            } catch (CredenciaisInvalidasException e) {
                int restantes = MAX_TENTATIVAS - tentativa;
                System.out.println("Erro: " + e.getMessage());
                if (restantes > 0) {
                    System.out.println("Tentativas restantes: " + restantes);
                }
            }
        }

        System.out.println("Número máximo de tentativas excedido. Encerrando o sistema...");
        leitor.pausar();
        leitor.limparTela();
        return false;
    }
}