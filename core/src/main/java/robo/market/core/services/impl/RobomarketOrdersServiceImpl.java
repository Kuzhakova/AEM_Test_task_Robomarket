package robo.market.core.services.impl;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import robo.market.core.exceptions.ConfirmationException;
import robo.market.core.gsondatabind.cancellation.CancellationRequest;
import robo.market.core.gsondatabind.purchase.PurchaseRequest;
import robo.market.core.gsondatabind.reservation.ReservationRequest;
import robo.market.core.gsondatabind.reservation.ReservationSuccess;
import robo.market.core.gsondatabind.yareservation.YaReservationRequest;
import robo.market.core.models.beans.RobomarketOrder;
import robo.market.core.robomarketutils.constants.RobomarketOrderStatus;
import robo.market.core.services.RobomarketOrdersService;

import java.util.*;

@Component(property = {Constants.SERVICE_DESCRIPTION + "=Service for operations with RM orders."},
        service = RobomarketOrdersService.class, immediate = true)
public class RobomarketOrdersServiceImpl implements RobomarketOrdersService {

    private static final List<RobomarketOrder> robomarketOrderList = new LinkedList<>();

    @Override
    public void addRobomarketOrder(ReservationRequest reservationRequest, ReservationSuccess reservationSuccess) {
        RobomarketOrder robomarketOrder = new RobomarketOrder();
        robomarketOrder.setOrderId(reservationRequest.getOrderId());
        robomarketOrder.setTotalCost(reservationRequest.getTotalCost());
        robomarketOrder.setStatus(RobomarketOrderStatus.RESERVED);
        robomarketOrder.setCustomer(reservationRequest.getCustomer());
        robomarketOrder.setCustomerComment(reservationRequest.getCustomerComment());
        robomarketOrder.setConfirmationLinkParameter(UUID.randomUUID().toString());

        robomarketOrder.setInvoiceId(reservationSuccess.getInvoiceId());
        robomarketOrder.setMinPaymentDue(reservationSuccess.getPaymentDue());

        robomarketOrderList.add(robomarketOrder);
    }

    private RobomarketOrder getRobomarketOrderByInvoiceId(String invoiceId) {
        RobomarketOrder robomarketOrder = null;
        for (RobomarketOrder order : robomarketOrderList) {
            if (order.getInvoiceId().equals(invoiceId)) {
                robomarketOrder = order;
                break;
            }
        }
        return robomarketOrder;
    }

    @Override
    public Date getMinPaymentDueByInvoiceId(String invoiceId) {
        RobomarketOrder robomarketOrder = getRobomarketOrderByInvoiceId(invoiceId);
        return Objects.isNull(robomarketOrder) ? null : robomarketOrder.getMinPaymentDue();
    }

    @Override
    public String getConfirmationLinkParameterByInvoiceId(String invoiceId) {
        RobomarketOrder robomarketOrder = getRobomarketOrderByInvoiceId(invoiceId);
        return Objects.isNull(robomarketOrder) ? null : robomarketOrder.getConfirmationLinkParameter();
    }

    @Override
    public RobomarketOrderStatus checkRobomarketOrderStatus(String invoiceId) {
        RobomarketOrder robomarketOrder = getRobomarketOrderByInvoiceId(invoiceId);
        return Objects.isNull(robomarketOrder) ? null : robomarketOrder.getStatus();
    }

    @Override
    public RobomarketOrder cancelRobomarketOrder(CancellationRequest cancellationRequest) {
        RobomarketOrder robomarketOrder = getRobomarketOrderByInvoiceId(cancellationRequest.getInvoiceId());
        if (Objects.nonNull(robomarketOrder) && (robomarketOrder.getStatus() == RobomarketOrderStatus.RESERVED)) {
            robomarketOrder.setStatus(RobomarketOrderStatus.CANCELLED);
            return robomarketOrder;
        }
        return null;
    }

    @Override
    public void cancelOverdueRobomarketOrder(String invoiceId) {
        RobomarketOrder robomarketOrder = getRobomarketOrderByInvoiceId(invoiceId);
        if (Objects.nonNull(robomarketOrder) && (robomarketOrder.getStatus() == RobomarketOrderStatus.RESERVED)) {
            robomarketOrder.setStatus(RobomarketOrderStatus.CANCELLED);
        }
    }

    @Override
    public boolean updateRobomarketOrder(String confirmationLinkParam) throws ConfirmationException {
        try {
            for (RobomarketOrder order : robomarketOrderList) {
                if (order.getConfirmationLinkParameter().equals(confirmationLinkParam) && (order.getStatus() != RobomarketOrderStatus.CONFIRMED)) {
                    order.setStatus(RobomarketOrderStatus.CONFIRMED);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw new ConfirmationException();
        }
    }

    @Override
    public void updateRobomarketOrder(YaReservationRequest yaReservationRequest, ReservationSuccess reservationSuccess) {
        RobomarketOrder robomarketOrder = getRobomarketOrderByInvoiceId(yaReservationRequest.getInvoiceId());
        robomarketOrder.setStatus(RobomarketOrderStatus.RESERVED);
        robomarketOrder.setMinPaymentDue(reservationSuccess.getPaymentDue());
    }

    @Override
    public RobomarketOrder updateRobomarketOrder(PurchaseRequest purchaseRequest) {
        RobomarketOrder robomarketOrder = getRobomarketOrderByInvoiceId(purchaseRequest.getInvoiceId());
        robomarketOrder.setStatus(RobomarketOrderStatus.PURCHASED);
        return null;
    }
}
