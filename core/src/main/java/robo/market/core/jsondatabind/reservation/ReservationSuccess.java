package robo.market.core.jsondatabind.reservation;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Random;

public class ReservationSuccess {

    private static final Random random = new Random(System.currentTimeMillis());

    @SerializedName("PaymentDue")
    private Date paymentDue;

    @SerializedName("InvoiceId")
    private String invoiceId;

    @SerializedName("OrderId")
    private int orderId;

    public ReservationSuccess(int orderId, Date paymentDue) {
        this.invoiceId = "aem-shop-" + orderId + "-" + random.nextInt(10000);
        this.orderId = orderId;
        this.paymentDue = paymentDue;
    }

    public ReservationSuccess(int orderId, String invoiceId, Date paymentDue) {
        this.invoiceId = invoiceId;
        this.orderId = orderId;
        this.paymentDue = paymentDue;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public int getOrderId() {
        return orderId;
    }

    public Date getPaymentDue() {
        return paymentDue;
    }
}
