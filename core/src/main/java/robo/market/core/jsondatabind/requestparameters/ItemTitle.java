package robo.market.core.jsondatabind.requestparameters;

import com.google.gson.annotations.SerializedName;

public class ItemTitle {

    @SerializedName("Value")
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
