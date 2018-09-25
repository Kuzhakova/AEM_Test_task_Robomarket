package robo.market.core.jsondatabind.requstparameters;

import com.google.gson.annotations.SerializedName;

public class Delivery {

    @SerializedName("DeliveryPackage")
    private int deliveryPackage;

    @SerializedName("Price")
    private double price;

    @SerializedName("DeliveryType")
    private String deliveryType;

    @SerializedName("Region")
    private String region;

    @SerializedName("City")
    private String city;

    @SerializedName("Address")
    private String address;

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public int getDeliveryPackage() {
        return deliveryPackage;
    }

    public void setDeliveryPackage(int deliveryPackage) {
        this.deliveryPackage = deliveryPackage;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
