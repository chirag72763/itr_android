package com.codearlystudio.homedelivery.models;

public class PojoSimilarProducts {
    String productId, productName, productImage;

    public PojoSimilarProducts(String productId, String productName, String productImage) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
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

}
