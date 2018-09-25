package robo.market.core.services;

import robo.market.core.exceptions.ConfirmationException;
import robo.market.core.jsondatabind.cancellation.CancellationRequest;
import robo.market.core.jsondatabind.purchase.PurchaseRequest;
import robo.market.core.jsondatabind.reservation.ReservationRequest;
import robo.market.core.jsondatabind.reservation.ReservationSuccess;
import robo.market.core.jsondatabind.yareservation.YaReservationRequest;
import robo.market.core.models.beans.RobomarketOrder;
import robo.market.core.robomarketutils.constants.RobomarketOrderStatus;

import java.util.Date;

public interface RobomarketOrdersService {

    void addRobomarketOrder(ReservationRequest reservationRequest, ReservationSuccess reservationSuccess);

    RobomarketOrder updateRobomarketOrder(PurchaseRequest purchaseRequest);

    boolean confirmRobomarketOrder(String confirmationLinkParam) throws ConfirmationException;

    void yaReserveRobomarketOrder(YaReservationRequest yaReservationRequest, ReservationSuccess reservationSuccess);

    Date getMinPaymentDueByInvoiceId(String invoiceId);

    String getConfirmationLinkParameterByInvoiceId(String invoiceId);

    RobomarketOrderStatus checkRobomarketOrderStatus(String invoiceId);

    RobomarketOrder cancelRobomarketOrder(CancellationRequest cancellationRequest);

    void cancelOverdueRobomarketOrder(String invoiceId);

}
