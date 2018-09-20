package robo.market.core.gsondatabind.reservation;

import com.google.gson.annotations.SerializedName;
import robo.market.core.gsondatabind.RobomarketRequest;

import java.util.Date;

public class ReservationRequest extends RobomarketRequest {

    @SerializedName("MinPaymentDue")
    private Date minPaymentDue;

    public Date getMinPaymentDue() {
        return minPaymentDue;
    }

}
