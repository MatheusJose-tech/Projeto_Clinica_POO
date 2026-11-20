package model.exception;

public class HorarioIndisponivelException extends ClinicaException {

    private static final long serialVersionUID = 1L;

    public HorarioIndisponivelException(String mensagem) {
        super(mensagem);
    }
}