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
}