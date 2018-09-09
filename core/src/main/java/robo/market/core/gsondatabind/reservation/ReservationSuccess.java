package robo.market.core.gsondatabind.reservation;

import com.google.gson.annotations.SerializedName;
import robo.market.core.services.RobomarketHandleClaimService;

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

    public ReservationSuccess(int orderId) {
        this.invoiceId = "aem-shop-" + orderId + "-" + random.nextInt(10000);
        this.orderId = orderId;
        this.paymentDue = new Date(RobomarketHandleClaimService.RESERVATION_TIME + System.currentTimeMillis());
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
