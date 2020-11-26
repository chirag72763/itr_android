package com.codearlystudio.homedelivery.models;

import org.json.JSONArray;

public class SectionCategories {
    String sectionCategoryName, sectionDescription, sectionCategory, sectionBannerTitle, sectionBannerSubTitle, sectionBannerMessage, sectionBannerImage;
    JSONArray sectionItems, sectionExclusive;

    public SectionCategories(String sectionCategoryName, String sectionDescription, String sectionCategory, String sectionBannerTitle, String sectionBannerSubTitle, String sectionBannerMessage, String sectionBannerImage, JSONArray sectionItems, JSONArray sectionExclusive) {
        this.sectionCategoryName = sectionCategoryName;
        this.sectionDescription = sectionDescription;
        this.sectionCategory = sectionCategory;
        this.sectionBannerTitle = sectionBannerTitle;
        this.sectionBannerSubTitle = sectionBannerSubTitle;
        this.sectionBannerMessage = sectionBannerMessage;
        this.sectionBannerImage = sectionBannerImage;
        this.sectionItems = sectionItems;
        this.sectionExclusive = sectionExclusive;
    }

    public String getSectionCategoryName() {
        return sectionCategoryName;
    }

    public String getSectionDescription() {
        return sectionDescription;
    }

    public String getSectionCategory() {
        return sectionCategory;
    }

    public String getSectionBannerTitle() {
        return sectionBannerTitle;
    }

    public String getSectionBannerSubTitle() {
        return sectionBannerSubTitle;
    }

    public String getSectionBannerMessage() {
        return sectionBannerMessage;
    }

    public String getSectionBannerImage() {
        return sectionBannerImage;
    }

    public JSONArray getSectionItems() {
        return sectionItems;
    }

    public JSONArray getSectionExclusive() {
        return sectionExclusive;
    }
}
