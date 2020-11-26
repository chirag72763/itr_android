package com.codearlystudio.homedelivery.models;

public class PojoCategoryItems {
    String sectionId, sectionName, sectionImage, sectionCount;

    public PojoCategoryItems(String sectionId, String sectionName, String sectionImage, String sectionCount) {
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.sectionImage = sectionImage;
        this.sectionCount = sectionCount;
    }

    public String getSectionId() {
        return sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getSectionImage() {
        return sectionImage;
    }

    public String getSectionCount() {
        return sectionCount;
    }

}
