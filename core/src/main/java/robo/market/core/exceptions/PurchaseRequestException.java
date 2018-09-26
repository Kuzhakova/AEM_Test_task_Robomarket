package robo.market.core.exceptions;

public class PurchaseRequestException extends Exception {

    public PurchaseRequestException() {
        super();
    }

    public PurchaseRequestException(String message) {
        super(message);
    }

    public PurchaseRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
