package robo.market.core.exceptions;

public class ConfirmationException extends Exception {

    public ConfirmationException() {
        super();
    }

    public ConfirmationException(String message) {
        super(message);
    }

    public ConfirmationException(String message, Throwable cause) {
        super(message, cause);
    }
}