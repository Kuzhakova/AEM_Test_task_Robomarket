package robo.market.core.jsondatabind;

import com.google.gson.annotations.SerializedName;
import robo.market.core.jsondatabind.annotations.JsonRequired;
import robo.market.core.jsondatabind.requstparameters.Customer;
import robo.market.core.jsondatabind.requstparameters.Item;

import java.util.List;

public abstract class RobomarketRequest {

    @JsonRequired
    @SerializedName("OrderId")
    private int orderId;

    @JsonRequired
    @SerializedName("TotalCost")
    private double totalCost;

    @JsonRequired
    @SerializedName("Customer")
    private Customer customer;

    @SerializedName("CustomerComment")
    private String customerComment;

    @JsonRequired
    @SerializedName("Items")
    private List<Item> items;

    public double getTotalCost() {
        return totalCost;
    }

    public int getOrderId() {
        return orderId;
    }
    public Customer getCustomer() {
        return customer;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getCustomerComment() {
        return customerComment;
    }
}

