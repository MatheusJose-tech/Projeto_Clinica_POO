package model.exception;

public class DadosInvalidosException extends ClinicaException {

    private static final long serialVersionUID = 1L;

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
