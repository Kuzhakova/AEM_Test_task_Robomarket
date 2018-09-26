package robo.market.core.jsondatabind.purchase;

import com.google.gson.annotations.SerializedName;
import robo.market.core.jsondatabind.responseparameters.ErrorJson;

public class PurchaseResponce {

    @SerializedName("OrderId")
    private long orderId;

    @SerializedName("Error")
    private ErrorJson errorJson = new ErrorJson();

    public PurchaseResponce(long orderId, String errorCode) {
        this.orderId = orderId;
        errorJson.setErrorCode(errorCode);
    }

    public long getOrderId() {
        return orderId;
    }

    public ErrorJson getErrorJson() {
        return errorJson;
    }
}
