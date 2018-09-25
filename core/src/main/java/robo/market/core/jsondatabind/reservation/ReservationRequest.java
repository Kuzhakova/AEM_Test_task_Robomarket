package robo.market.core.jsondatabind.reservation;

import com.google.gson.annotations.SerializedName;
import robo.market.core.jsondatabind.RobomarketRequest;
import robo.market.core.jsondatabind.annotations.JsonRequired;

import java.util.Date;

public class ReservationRequest extends RobomarketRequest {

    @JsonRequired
    @SerializedName("MinPaymentDue")
    private Date minPaymentDue;

    public Date getMinPaymentDue() {
        return minPaymentDue;
    }

}
