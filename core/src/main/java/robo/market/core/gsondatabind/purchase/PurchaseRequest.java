package robo.market.core.gsondatabind.purchase;

import com.google.gson.annotations.SerializedName;

public class PurchaseRequest {

    @SerializedName("InvoiceId")
    private String invoiceId;

    public String getInvoiceId() {
        return invoiceId;
    }

}
