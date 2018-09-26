package robo.market.core.exceptions;

public class CancellationRequestException extends Exception {
    public CancellationRequestException() {
        super();
    }

    public CancellationRequestException(String message) {
        super(message);
    }

    public CancellationRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
