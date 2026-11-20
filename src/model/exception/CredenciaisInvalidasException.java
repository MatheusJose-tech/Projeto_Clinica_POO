package model.exception;

public class CredenciaisInvalidasException extends ClinicaException {

    private static final long serialVersionUID = 1L;

    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }
}