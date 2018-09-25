package robo.market.core.jsondatabind.yareservation;

import com.google.gson.annotations.SerializedName;
import robo.market.core.jsondatabind.RobomarketRequest;
import robo.market.core.jsondatabind.annotations.JsonRequired;

public class YaReservationRequest extends RobomarketRequest {

    @JsonRequired
    @SerializedName("InvoiceId")
    private String invoiceId;

    public String getInvoiceId() {
        return invoiceId;
    }
}
