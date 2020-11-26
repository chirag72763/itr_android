package com.codearlystudio.homedelivery.models;

public class Orders {
    String orderId, orderStatus, productName, orderedOn, orderTotal, orderMessage;

    public Orders(String orderId, String orderStatus, String productName, String orderedOn, String orderTotal, String orderMessage) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.productName = productName;
        this.orderedOn = orderedOn;
        this.orderTotal = orderTotal;
        this.orderMessage = orderMessage;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getProductName() {
        return productName;
    }

    public String getOrderedOn() {
        return orderedOn;
    }

    public String getOrderTotal() {
        return orderTotal;
    }
}
