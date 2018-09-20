package robo.market.core.services;

import robo.market.core.exceptions.ConfirmationException;
import robo.market.core.gsondatabind.cancellation.CancellationRequest;
import robo.market.core.gsondatabind.purchase.PurchaseRequest;
import robo.market.core.gsondatabind.reservation.ReservationRequest;
import robo.market.core.gsondatabind.reservation.ReservationSuccess;
import robo.market.core.gsondatabind.yareservation.YaReservationRequest;
import robo.market.core.models.beans.RobomarketOrder;
import robo.market.core.robomarketutils.constants.RobomarketOrderStatus;

import java.util.Date;

public interface RobomarketOrdersService {

    void addRobomarketOrder(ReservationRequest reservationRequest, ReservationSuccess reservationSuccess);

    RobomarketOrder updateRobomarketOrder(PurchaseRequest purchaseRequest);

    boolean updateRobomarketOrder(String confirmationLinkParam) throws ConfirmationException;

    void updateRobomarketOrder(YaReservationRequest yaReservationRequest, ReservationSuccess reservationSuccess);

    Date getMinPaymentDueByInvoiceId(String invoiceId);

    String getConfirmationLinkParameterByInvoiceId(String invoiceId);

    RobomarketOrderStatus checkRobomarketOrderStatus(String invoiceId);

    RobomarketOrder cancelRobomarketOrder(CancellationRequest cancellationRequest);

    void cancelOverdueRobomarketOrder(String invoiceId);

   /* RobomarketOrder getRobomarketOrder(RobomarketPurchaseRequestJsonModel yaReservationRequest);*/

}
