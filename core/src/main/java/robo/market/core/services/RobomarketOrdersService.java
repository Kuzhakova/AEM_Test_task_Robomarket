package robo.market.core.services;

import robo.market.core.exceptions.*;
import robo.market.core.jsondatabind.cancellation.CancellationRequest;
import robo.market.core.jsondatabind.purchase.PurchaseRequest;
import robo.market.core.jsondatabind.reservation.ReservationRequest;
import robo.market.core.jsondatabind.reservation.ReservationSuccess;
import robo.market.core.jsondatabind.yareservation.YaReservationRequest;

import java.util.Date;

public interface RobomarketOrdersService {

    void reserveRobomarketOrder(ReservationRequest reservationRequest, ReservationSuccess reservationSuccess);

    void registerPurchaseRobomarketOrder(PurchaseRequest purchaseRequest) throws NoSuchOrderException, OrderOverdueException;

    void yaReserveRobomarketOrder(YaReservationRequest yaReservationRequest, Date newMinPaymentDue) throws NoSuchOrderException;

    void cancelRobomarketOrder(CancellationRequest cancellationRequest) throws NoSuchOrderException;

    void confirmRobomarketOrder(String confirmationLinkParam) throws NoSuchOrderException;

    String getConfirmationLinkParameterByInvoiceId(String invoiceId) throws NoSuchOrderException;

    Date getMinPaymentDueByInvoiceId(String invoiceId) throws NoSuchOrderException;

}
