package robo.market.core.exceptions;

public class NoSuchOrderException extends Exception {
    public NoSuchOrderException() {
        super();
    }

    public NoSuchOrderException(String message) {
        super(message);
    }

    public NoSuchOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
