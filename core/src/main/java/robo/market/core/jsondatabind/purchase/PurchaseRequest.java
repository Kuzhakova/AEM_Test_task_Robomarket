package robo.market.core.jsondatabind.purchase;

import com.google.gson.annotations.SerializedName;
import robo.market.core.jsondatabind.RobomarketRequest;
import robo.market.core.jsondatabind.annotations.JsonRequired;

public class PurchaseRequest extends RobomarketRequest {

    @JsonRequired
    @SerializedName("InvoiceId")
    private String invoiceId;

    public String getInvoiceId() {
        return invoiceId;
    }

}
