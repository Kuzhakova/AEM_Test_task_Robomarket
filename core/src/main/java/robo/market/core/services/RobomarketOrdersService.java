package robo.market.core.services;

import robo.market.core.exceptions.CancellationRequestException;
import robo.market.core.exceptions.ConfirmationException;
import robo.market.core.exceptions.PurchaseRequestException;
import robo.market.core.exceptions.ReservationRequestException;
import robo.market.core.jsondatabind.cancellation.CancellationRequest;
import robo.market.core.jsondatabind.purchase.PurchaseRequest;
import robo.market.core.jsondatabind.reservation.ReservationRequest;
import robo.market.core.jsondatabind.reservation.ReservationSuccess;
import robo.market.core.jsondatabind.yareservation.YaReservationRequest;

import java.util.Date;

public interface RobomarketOrdersService {

    void reserveRobomarketOrder(ReservationRequest reservationRequest, ReservationSuccess reservationSuccess);

    void registerPurchaseRobomarketOrder(PurchaseRequest purchaseRequest) throws PurchaseRequestException;

    void yaReserveRobomarketOrder(YaReservationRequest yaReservationRequest, Date newMinPaymentDue) throws ReservationRequestException;

    void cancelRobomarketOrder(CancellationRequest cancellationRequest) throws CancellationRequestException;

    boolean confirmRobomarketOrder(String confirmationLinkParam) throws ConfirmationException;

    String getConfirmationLinkParameterByInvoiceId(String invoiceId);

}
