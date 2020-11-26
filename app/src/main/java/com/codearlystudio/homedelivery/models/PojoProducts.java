package com.codearlystudio.homedelivery.models;

import org.json.JSONArray;

public class PojoProducts {
    String productId, productName, productTags, productImage, productDescription, productAvailable, productCartCount, productSaved;
    JSONArray productVariants;

    public PojoProducts(String productId, String productName, String productTags, String productImage, String productDescription, String productAvailable, String productCartCount, String productSaved, JSONArray productVariants) {
        this.productId = productId;
        this.productName = productName;
        this.productTags = productTags;
        this.productImage = productImage;
        this.productDescription = productDescription;
        this.productAvailable = productAvailable;
        this.productCartCount = productCartCount;
        this.productSaved = productSaved;
        this.productVariants = productVariants;
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

    public String getProductDescription() {
        return productDescription;
    }

    public String getProductAvailable() {
        return productAvailable;
    }

    public String getProductCartCount() {
        return productCartCount;
    }

    public String getProductSaved() {
        return productSaved;
    }

    public JSONArray getProductVariants() {
        return productVariants;
    }

}
