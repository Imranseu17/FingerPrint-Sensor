package com.zkteco.silkiddemo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CompanyModel {
    @SerializedName("response")
    @Expose
    public int response;
    @SerializedName("message")
    @Expose
    public ArrayList<CompanyMessage> message;
    @SerializedName("status")
    @Expose
    public String status;

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public ArrayList<CompanyMessage> getMessage() {
        return message;
    }

    public void setMessage(ArrayList<CompanyMessage> message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
