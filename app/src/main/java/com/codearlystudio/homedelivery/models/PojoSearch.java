package com.codearlystudio.homedelivery.models;

public class PojoSearch {

    String productId, productName, productTags, productImage, sectionName;

    public PojoSearch(String productId, String productName, String productTags, String productImage, String sectionName) {
        this.productId = productId;
        this.productName = productName;
        this.productTags = productTags;
        this.productImage = productImage;
        this.sectionName = sectionName;
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

    public String getProductImage() {
        return productImage;
    }

    public String getSectionName() {
        return sectionName;
    }
}
