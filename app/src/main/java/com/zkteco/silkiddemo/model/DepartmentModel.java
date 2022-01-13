package com.zkteco.silkiddemo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DepartmentModel {
    @SerializedName("response")
    @Expose
    public int response;
    @SerializedName("message")
    @Expose
    public ArrayList<DepartmentMessage> departmentMessages;
    @SerializedName("status")
    @Expose
    public String status;

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public ArrayList<DepartmentMessage> getDepartmentMessages() {
        return departmentMessages;
    }

    public void setDepartmentMessages(ArrayList<DepartmentMessage> departmentMessages) {
        this.departmentMessages = departmentMessages;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
