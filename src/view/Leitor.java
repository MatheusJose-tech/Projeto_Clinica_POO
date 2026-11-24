package view;

import java.io.IOException;
import java.util.Scanner;

public class Leitor {

    private final Scanner scanner;

    public Leitor(Scanner scanner) {
        this.scanner = scanner;
    }

    public String lerTexto(String mensagem) {
        
        System.out.print(mensagem);
        
        if (!scanner.hasNextLine()) {
            return "";
        }
        return scanner.nextLine().trim();
    }

    public int lerOpcao(String mensagem) {
        
        System.out.print(mensagem);
        
        if (!scanner.hasNextLine()) {
            return 0;
        }
        
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
        public void pausar() {
        
            System.out.print("\nPressione ENTER para continuar...");
        
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    public void limparTela() {

        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }
}