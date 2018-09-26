package robo.market.core.jsondatabind.requestparameters;

import com.google.gson.annotations.SerializedName;
import robo.market.core.jsondatabind.annotations.JsonRequired;

public class Customer {

    @JsonRequired
    @SerializedName("Name")
    private String name;

    @JsonRequired
    @SerializedName("Email")
    private String email;

    @JsonRequired
    @SerializedName("Phone")
    private String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
