package com.codearlystudio.homedelivery.models;

public class PojoAddress {
    String addressId, addressName, addressPhone, addresPincode, address, addressLandmark, addressCity, addressState, addressEmail, addressDefault;

    public PojoAddress(String addressId, String addressName, String addressPhone, String addresPincode, String address, String addressLandmark, String addressCity, String addressState, String addressEmail, String addressDefault) {
        this.addressId = addressId;
        this.addressName = addressName;
        this.addressPhone = addressPhone;
        this.addresPincode = addresPincode;
        this.address = address;
        this.addressLandmark = addressLandmark;
        this.addressCity = addressCity;
        this.addressState = addressState;
        this.addressEmail = addressEmail;
        this.addressDefault = addressDefault;
    }

    public String getAddressId() {
        return addressId;
    }

    public String getAddressName() {
        return addressName;
    }

    public String getAddressPhone() {
        return addressPhone;
    }

    public String getAddresPincode() {
        return addresPincode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressLandmark() {
        return addressLandmark;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public String getAddressState() {
        return addressState;
    }

    public String getAddressEmail() {
        return addressEmail;
    }

    public String getAddressDefault() {
        return addressDefault;
    }

}
