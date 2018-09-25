package robo.market.core.exceptions;

public class PurchaseRequestException extends Exception {

    public static final String REQUEST_TYPE = "Purchase Request";

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
