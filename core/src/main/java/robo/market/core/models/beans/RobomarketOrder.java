package robo.market.core.models.beans;

import robo.market.core.jsondatabind.requstparameters.Customer;
import robo.market.core.robomarketutils.constants.RobomarketOrderStatus;

import java.util.Date;

public class RobomarketOrder {

    private int orderId;
    private String invoiceId;
    private double totalCost;
    private Date minPaymentDue;
    private RobomarketOrderStatus status;
    private Customer customer;
    private String customerComment;
    private String confirmationLinkParameter;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public Date getMinPaymentDue() {
        return minPaymentDue;
    }

    public void setMinPaymentDue(Date minPaymentDue) {
        this.minPaymentDue = minPaymentDue;
    }

    public RobomarketOrderStatus getStatus() {
        return status;
    }

    public void setStatus(RobomarketOrderStatus status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getCustomerComment() {
        return customerComment;
    }

    public void setCustomerComment(String customerComment) {
        this.customerComment = customerComment;
    }

    public String getConfirmationLinkParameter() {
        return confirmationLinkParameter;
    }

    public void setConfirmationLinkParameter(String confirmationLinkParameter) {
        this.confirmationLinkParameter = confirmationLinkParameter;
    }
}
