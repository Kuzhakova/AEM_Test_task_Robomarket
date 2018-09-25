package robo.market.core.jsondatabind.cancellation;

import com.google.gson.annotations.SerializedName;
import robo.market.core.jsondatabind.annotations.JsonRequired;

public class CancellationRequest {

    @JsonRequired
    @SerializedName("OrderId")
    private int orderId;

    @JsonRequired
    @SerializedName("InvoiceId")
    private String invoiceId;

    public int getOrderId() {
        return orderId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }


}
