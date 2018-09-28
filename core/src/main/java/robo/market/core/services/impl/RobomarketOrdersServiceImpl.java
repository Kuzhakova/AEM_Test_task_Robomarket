package robo.market.core.services.impl;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import robo.market.core.exceptions.*;
import robo.market.core.jsondatabind.cancellation.CancellationRequest;
import robo.market.core.jsondatabind.purchase.PurchaseRequest;
import robo.market.core.jsondatabind.reservation.ReservationRequest;
import robo.market.core.jsondatabind.reservation.ReservationSuccess;
import robo.market.core.jsondatabind.yareservation.YaReservationRequest;
import robo.market.core.models.beans.RobomarketOrder;
import robo.market.core.robomarketutils.constants.RobomarketOrderStatus;
import robo.market.core.services.RobomarketOrdersService;

import java.util.*;

@Component(property = {Constants.SERVICE_DESCRIPTION + "=Service for operations with RM orders."},
        service = RobomarketOrdersService.class, immediate = true)
public class RobomarketOrdersServiceImpl implements RobomarketOrdersService {

    private static final List<RobomarketOrder> robomarketOrderList = new LinkedList<>();

    @Override
    public void reserveRobomarketOrder(ReservationRequest reservationRequest, ReservationSuccess reservationSuccess) {
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

    private RobomarketOrder getRobomarketOrderByInvoiceId(String invoiceId) throws NoSuchOrderException {
        Optional<RobomarketOrder> order = robomarketOrderList.stream()
                .filter(o -> o.getInvoiceId().equals(invoiceId))
                .findAny();
        RobomarketOrder robomarketOrder = order.orElse(null);
        if (Objects.isNull(robomarketOrder)) {
            throw new NoSuchOrderException("There is no such order in database");
        }
        return robomarketOrder;
    }

    @Override
    public String getConfirmationLinkParameterByInvoiceId(String invoiceId) throws NoSuchOrderException {
        RobomarketOrder robomarketOrder = getRobomarketOrderByInvoiceId(invoiceId);
        return Objects.isNull(robomarketOrder) ? null : robomarketOrder.getConfirmationLinkParameter();
    }

    @Override
    public Date getMinPaymentDueByInvoiceId(String invoiceId) throws NoSuchOrderException {
        RobomarketOrder robomarketOrder = getRobomarketOrderByInvoiceId(invoiceId);
        return robomarketOrder.getMinPaymentDue();
    }


    @Override
    public void cancelRobomarketOrder(CancellationRequest cancellationRequest) throws NoSuchOrderException {
        if (!cancelRobomarketOrder(cancellationRequest.getInvoiceId())) {
            throw new NoSuchOrderException("There is no such order in the reserved state");
        }
    }

    private boolean cancelRobomarketOrder(String invoiceId) throws NoSuchOrderException {
        RobomarketOrder robomarketOrder = getRobomarketOrderByInvoiceId(invoiceId);
        if (robomarketOrder.getStatus() == RobomarketOrderStatus.RESERVED) {
            robomarketOrder.setStatus(RobomarketOrderStatus.CANCELLED);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void confirmRobomarketOrder(String confirmationLinkParam) throws NoSuchOrderException {
        Optional<RobomarketOrder> order = robomarketOrderList.stream()
                .filter(o -> o.getConfirmationLinkParameter().equals(confirmationLinkParam))
                .findAny();
        RobomarketOrder robomarketOrder = order.orElse(null);
        if (Objects.isNull(robomarketOrder)) {
            throw new NoSuchOrderException();
        }
        robomarketOrder.setStatus(RobomarketOrderStatus.CONFIRMED);
    }

    @Override
    public void yaReserveRobomarketOrder(YaReservationRequest yaReservationRequest, Date newMinPaymentDue) throws NoSuchOrderException {
        String invoiceId = yaReservationRequest.getInvoiceId();
        RobomarketOrder robomarketOrder = getRobomarketOrderByInvoiceId(invoiceId);
        Date nowDate = new Date();
        if (nowDate.after(robomarketOrder.getMinPaymentDue())) {
            robomarketOrder.setStatus(RobomarketOrderStatus.RESERVED);
            robomarketOrder.setMinPaymentDue(newMinPaymentDue);
        } else {
            throw new NoSuchOrderException("There is no such order in the overdue state");
        }
    }

    @Override
    public void registerPurchaseRobomarketOrder(PurchaseRequest purchaseRequest) throws NoSuchOrderException, OrderOverdueException {
        String invoiceId = purchaseRequest.getInvoiceId();

        RobomarketOrder robomarketOrder = getRobomarketOrderByInvoiceId(invoiceId);
        if (robomarketOrder.getStatus() != RobomarketOrderStatus.RESERVED) {
            throw new NoSuchOrderException("This order is not reserved.");
        }
        Date nowDate = new Date();
        if (nowDate.after(robomarketOrder.getMinPaymentDue())) {
            cancelRobomarketOrder(invoiceId);
            throw new OrderOverdueException("Product payment overdue.");
        }
        robomarketOrder.setStatus(RobomarketOrderStatus.PURCHASED);
    }
}
