package com.codearlystudio.homedelivery.data;

import com.google.gson.annotations.SerializedName;

public class ServerResponse {

    @SerializedName("success")
    boolean success;
    @SerializedName("message")
    String message;


    public String getMessage() {
        return message;
    }

    public boolean getSuccess() {
        return success;
    }

}
