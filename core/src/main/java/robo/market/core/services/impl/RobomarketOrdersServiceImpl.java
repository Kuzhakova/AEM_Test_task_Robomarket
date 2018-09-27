package robo.market.core.services.impl;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import robo.market.core.exceptions.CancellationRequestException;
import robo.market.core.exceptions.ConfirmationException;
import robo.market.core.exceptions.PurchaseRequestException;
import robo.market.core.exceptions.ReservationRequestException;
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

    private RobomarketOrder getRobomarketOrderByInvoiceId(String invoiceId) {
        // TODO see java 8 lamdas
        RobomarketOrder robomarketOrder = null;
        for (RobomarketOrder order : robomarketOrderList) {
            if (order.getInvoiceId().equals(invoiceId)) {
                robomarketOrder = order;
                break;
            }
        }
        /*Optional<RobomarketOrder> order = robomarketOrderList.stream().filter(o -> o.getInvoiceId().equals(invoiceId)).findAny();
        order.orElse(null);*/
        return robomarketOrder;
    }

    private Date getMinPaymentDueByInvoiceId(String invoiceId) {
        RobomarketOrder robomarketOrder = getRobomarketOrderByInvoiceId(invoiceId);
        return Objects.isNull(robomarketOrder) ? null : robomarketOrder.getMinPaymentDue();
    }

    @Override
    public String getConfirmationLinkParameterByInvoiceId(String invoiceId) {
        RobomarketOrder robomarketOrder = getRobomarketOrderByInvoiceId(invoiceId);
        return Objects.isNull(robomarketOrder) ? null : robomarketOrder.getConfirmationLinkParameter();
    }

    private RobomarketOrderStatus checkRobomarketOrderStatus(String invoiceId) {
        RobomarketOrder robomarketOrder = getRobomarketOrderByInvoiceId(invoiceId);
        return Objects.isNull(robomarketOrder) ? null : robomarketOrder.getStatus();
    }

    @Override
    public void cancelRobomarketOrder(CancellationRequest cancellationRequest) throws CancellationRequestException {
        if (!cancelRobomarketOrder(cancellationRequest.getInvoiceId())) {
            throw new CancellationRequestException("There is no such order in the reserved state");
        }
    }

    private boolean cancelRobomarketOrder(String invoiceId) {
        RobomarketOrder robomarketOrder = getRobomarketOrderByInvoiceId(invoiceId);
        if (Objects.nonNull(robomarketOrder) && (robomarketOrder.getStatus() == RobomarketOrderStatus.RESERVED)) {
            robomarketOrder.setStatus(RobomarketOrderStatus.CANCELLED);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean confirmRobomarketOrder(String confirmationLinkParam) throws ConfirmationException {
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
    public void yaReserveRobomarketOrder(YaReservationRequest yaReservationRequest, Date newMinPaymentDue) throws ReservationRequestException {
        String invoiceId = yaReservationRequest.getInvoiceId();
        Date minPaymentDue = getMinPaymentDueByInvoiceId(invoiceId);
        Date nowDate = new Date();
        if (Objects.nonNull(minPaymentDue) && nowDate.after(minPaymentDue)) {
            RobomarketOrder robomarketOrder = getRobomarketOrderByInvoiceId(invoiceId);
            robomarketOrder.setStatus(RobomarketOrderStatus.RESERVED);
            robomarketOrder.setMinPaymentDue(newMinPaymentDue);
        } else {
            throw new ReservationRequestException("There is no such order in the overdue state");
        }
    }

    @Override
    public void registerPurchaseRobomarketOrder(PurchaseRequest purchaseRequest) throws PurchaseRequestException {
        String invoiceId = purchaseRequest.getInvoiceId();
        //TODO почему дата? здесь надо обработать заказ
        Date minPaymentDue = getMinPaymentDueByInvoiceId(invoiceId);
        //TODO checkRobomarketOrderStatus to isRobomarketOrderReserved
        if (Objects.isNull(minPaymentDue) || (checkRobomarketOrderStatus(invoiceId) != RobomarketOrderStatus.RESERVED)) {
            throw new PurchaseRequestException("This order is not reserved.");
        }
        Date nowDate = new Date();
        if (nowDate.after(minPaymentDue)) {
            cancelRobomarketOrder(invoiceId);
            throw new PurchaseRequestException("Product payment overdue.");
        }

        RobomarketOrder robomarketOrder = getRobomarketOrderByInvoiceId(invoiceId);
        robomarketOrder.setStatus(RobomarketOrderStatus.PURCHASED);
    }
}
