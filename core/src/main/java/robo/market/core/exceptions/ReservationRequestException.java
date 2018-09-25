package robo.market.core.exceptions;

public class ReservationRequestException extends Exception {

    public static final String REQUEST_TYPE = "Reservation Request";

    public ReservationRequestException() {
        super();
    }

    public ReservationRequestException(String message) {
        super(message);
    }

    public ReservationRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}