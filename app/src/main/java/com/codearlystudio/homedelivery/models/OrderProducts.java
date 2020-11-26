package com.codearlystudio.homedelivery.models;

public class OrderProducts {
    String productName, productImage, variantSize, variantPrice, productQuantity, totalPrice;

    public OrderProducts(String productName, String productImage, String variantSize, String variantPrice, String productQuantity, String totalPrice) {
        this.productName = productName;
        this.productImage = productImage;
        this.variantSize = variantSize;
        this.variantPrice = variantPrice;
        this.productQuantity = productQuantity;
        this.totalPrice = totalPrice;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public String getVariantSize() {
        return variantSize;
    }

    public void setVariantSize(String variantSize) {
        this.variantSize = variantSize;
    }

    public String getVariantPrice() {
        return variantPrice;
    }

    public void setVariantPrice(String variantPrice) {
        this.variantPrice = variantPrice;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public String getTotalPrice() {
        return totalPrice;
    }
}
