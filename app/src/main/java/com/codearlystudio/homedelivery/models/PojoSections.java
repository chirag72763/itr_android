package com.codearlystudio.homedelivery.models;

public class PojoSections {
    String sectionId, sectionName, sectionCategory, sectionImage, sectionCount;

    public PojoSections(String sectionId, String sectionName, String sectionCategory, String sectionImage, String sectionCount) {
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.sectionCategory = sectionCategory;
        this.sectionImage = sectionImage;
        this.sectionCount = sectionCount;
    }

    public String getSectionId() {
        return sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

}
