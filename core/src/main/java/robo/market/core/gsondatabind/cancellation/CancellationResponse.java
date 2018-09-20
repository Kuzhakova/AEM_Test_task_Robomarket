package robo.market.core.gsondatabind.cancellation;

import com.google.gson.annotations.SerializedName;
import robo.market.core.gsondatabind.requstparameters.ErrorJson;

public class CancellationResponse {

    @SerializedName("OrderId")
    private int orderId;

    @SerializedName("InvoiceId")
    private String invoiceId;

    @SerializedName("Error")
    private ErrorJson errorJson = new ErrorJson();

    public CancellationResponse(String invoiceId, int orderId, String errorCode) {
       this.invoiceId = invoiceId;
        this.orderId = orderId;
        errorJson.setErrorCode(errorCode);
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public int getOrderId() {
        return orderId;
    }
}
