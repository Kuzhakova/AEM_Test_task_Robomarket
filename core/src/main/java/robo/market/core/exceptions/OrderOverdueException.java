package robo.market.core.exceptions;

public class OrderOverdueException extends Exception {
    public OrderOverdueException() {
        super();
    }

    public OrderOverdueException(String message) {
        super(message);
    }

    public OrderOverdueException(String message, Throwable cause) {
        super(message, cause);
    }
}
