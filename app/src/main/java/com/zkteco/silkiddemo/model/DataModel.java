package com.zkteco.silkiddemo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DataModel {

    @SerializedName("response")
    @Expose
    private Integer response;
    @SerializedName("message")
    @Expose
    private ArrayList<Message> messages;
    @SerializedName("status")
    @Expose
    private String status;


    public Integer getResponse() {
        return response;
    }

    public void setResponse(Integer response) {
        this.response = response;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
