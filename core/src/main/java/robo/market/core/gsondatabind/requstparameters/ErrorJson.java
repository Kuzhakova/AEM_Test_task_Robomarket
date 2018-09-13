package robo.market.core.gsondatabind.requstparameters;

import com.google.gson.annotations.SerializedName;

public class ErrorJson {

    public transient static final String ERROR_CODE_FAIL = "Fail";
    public transient static final String ERROR_CODE_OK = "Ok";
    public transient static final String ERROR_CODE_NOT_ENOUGH_GOODS = "NotEnoughGoodsInStock";

    @SerializedName("ErrorCode")
    private String errorCode;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
