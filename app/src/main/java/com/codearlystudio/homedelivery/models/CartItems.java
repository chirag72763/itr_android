package com.codearlystudio.homedelivery.models;

public class CartItems {
    String cartId, productId, variantId, productName, productImage, variantSize, variantPrice, variantAvailable, productAvailable, cartQuanity, totalProductPrice;

    public CartItems(String cartId, String productId, String variantId, String productName, String productImage, String variantSize, String variantPrice, String variantAvailable, String productAvailable, String cartQuanity, String totalProductPrice) {
        this.cartId = cartId;
        this.productId = productId;
        this.variantId = variantId;
        this.productName = productName;
        this.productImage = productImage;
        this.variantSize = variantSize;
        this.variantPrice = variantPrice;
        this.variantAvailable = variantAvailable;
        this.productAvailable = productAvailable;
        this.cartQuanity = cartQuanity;
        this.totalProductPrice = totalProductPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getVariantId() {
        return variantId;
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

    public String getVariantAvailable() {
        return variantAvailable;
    }

    public String getCartQuanity() {
        return cartQuanity;
    }

    public String getTotalProductPrice() {
        return totalProductPrice;
    }

}
