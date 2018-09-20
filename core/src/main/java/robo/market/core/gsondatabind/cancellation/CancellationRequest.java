package robo.market.core.gsondatabind.cancellation;

import com.google.gson.annotations.SerializedName;

public class CancellationRequest {

    @SerializedName("OrderId")
    private int orderId;

    @SerializedName("InvoiceId")
    private String invoiceId;

    public int getOrderId() {
        return orderId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }


}
