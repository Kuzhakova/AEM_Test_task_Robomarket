package robo.market.core.gsondatabind;

import com.google.gson.annotations.SerializedName;
import robo.market.core.gsondatabind.requstparameters.Customer;
import robo.market.core.gsondatabind.requstparameters.Item;

import java.util.List;

public abstract class RobomarketRequest {

    @SerializedName("OrderId")
    private int orderId;

    @SerializedName("TotalCost")
    private double totalCost;

    @SerializedName("Customer")
    private Customer customer;

    @SerializedName("CustomerComment")
    private String customerComment;

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

