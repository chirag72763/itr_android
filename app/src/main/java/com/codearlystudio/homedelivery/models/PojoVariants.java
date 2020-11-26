package com.codearlystudio.homedelivery.models;

public class PojoVariants {
    String variantId, variantSize, variantPrice, variantOriginal, variantAvailable;

    public PojoVariants(String variantId, String variantSize, String variantPrice, String variantOriginal, String variantAvailable) {
        this.variantId = variantId;
        this.variantSize = variantSize;
        this.variantPrice = variantPrice;
        this.variantOriginal = variantOriginal;
        this.variantAvailable = variantAvailable;
    }

    public String getVariantId() {
        return variantId;
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

    public String getVariantOriginal() {
        return variantOriginal;
    }

    public String getVariantAvailable() {
        return variantAvailable;
    }

}

