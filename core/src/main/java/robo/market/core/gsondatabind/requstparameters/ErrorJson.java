package robo.market.core.gsondatabind.requstparameters;

import com.google.gson.annotations.SerializedName;

public class ErrorJson {

    @SerializedName("ErrorCode")
    private String errorCode;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
