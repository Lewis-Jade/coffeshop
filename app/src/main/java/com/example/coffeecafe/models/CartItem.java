package com.example.coffeecafe.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String productId;
    private String productName;
    private String productDescription;
    private double productPrice;
    private String productImageUrl;
    private int quantity;
    private int localImageResource; // For local drawable resources

    public CartItem() {
    }

    public CartItem(String productId, String productName, String productDescription, 
                   double productPrice, String productImageUrl, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productImageUrl = productImageUrl;
        this.quantity = quantity;
    }

    public double getSubtotal() {
        return productPrice * quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getLocalImageResource() {
        return localImageResource;
    }

    public void setLocalImageResource(int localImageResource) {
        this.localImageResource = localImageResource;
    }
}
