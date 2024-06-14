package com.minhchien.bookstore.model;

public class Order {
    String orderId, orderTime, orderTotalCost, orderBy, orderStatus;

    public Order() {
    }

    public Order(String orderId, String orderTime, String orderTotalCost, String orderBy, String orderStatus) {
        this.orderId = orderId;
        this.orderTime = orderTime;
        this.orderTotalCost = orderTotalCost;
        this.orderBy = orderBy;
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderTotalCost() {
        return orderTotalCost;
    }

    public void setOrderTotalCost(String orderTotalCost) {
        this.orderTotalCost = orderTotalCost;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
