package robo.market.core.gsondatabind.purchase;

import com.google.gson.annotations.SerializedName;
import robo.market.core.gsondatabind.RobomarketRequest;

public class PurchaseRequest extends RobomarketRequest {

    @SerializedName("InvoiceId")
    private String invoiceId;

    public String getInvoiceId() {
        return invoiceId;
    }

}
