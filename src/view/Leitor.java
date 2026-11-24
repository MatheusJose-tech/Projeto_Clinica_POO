package view;

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
    
    if (System.console() == null) {
        System.out.print("\n".repeat(40));
        return;
    }

    System.out.print("\033[H\033[2J");
    System.out.flush();
    }
}