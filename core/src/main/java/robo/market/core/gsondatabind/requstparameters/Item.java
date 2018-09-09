package robo.market.core.gsondatabind.requstparameters;

import com.google.gson.annotations.SerializedName;

public class Item {
    @SerializedName("OfferId")
    private String offerId;

    @SerializedName("TotalCost")
    private double totalCost;

    @SerializedName("Quantity")
    private int quantity;

    @SerializedName("Price")
    private double price;

    @SerializedName("Title")
    private Title title;

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    class Title {

        @SerializedName("Value")
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
