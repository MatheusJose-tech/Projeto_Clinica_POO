package model.exception;

public class ClinicaException extends Exception {

    private static final long serialVersionUID = 1L;

    public ClinicaException(String mensagem) {
        super(mensagem);
    }
}