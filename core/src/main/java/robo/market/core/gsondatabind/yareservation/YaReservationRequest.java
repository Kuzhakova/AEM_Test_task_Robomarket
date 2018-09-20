package robo.market.core.gsondatabind.yareservation;

import com.google.gson.annotations.SerializedName;
import robo.market.core.gsondatabind.RobomarketRequest;

public class YaReservationRequest extends RobomarketRequest {

    @SerializedName("InvoiceId")
    private String invoiceId;

    public String getInvoiceId() {
        return invoiceId;
    }
}
