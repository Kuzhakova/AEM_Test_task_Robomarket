package robo.market.core.jsondatabind.reservation;

import com.google.gson.annotations.SerializedName;
import robo.market.core.jsondatabind.responseparameters.ErrorJson;

public class ReservationFailure {
    @SerializedName("OrderId")
    private int orderId;

    @SerializedName("ErrorCode")
    private ErrorJson errorJson = new ErrorJson();

    public ReservationFailure(int orderId, String errorCode) {
        this.orderId = orderId;
        errorJson.setErrorCode(errorCode);
    }

    public int getOrderId() {
        return orderId;
    }

    
}

