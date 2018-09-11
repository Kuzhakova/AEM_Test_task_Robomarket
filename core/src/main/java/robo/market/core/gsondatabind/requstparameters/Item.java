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
    private ItemTitle title;

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

    public ItemTitle getItemTitle() {
        return title;
    }

    public void setItemTitle(ItemTitle title) {
        this.title = title;
    }
    
}
