package com.zkteco.silkiddemo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EmployeeModel {
    @SerializedName("response")
    @Expose
    public int response;
    @SerializedName("message")
    @Expose
    public ArrayList<EmployeeMessage> employeeMessageArrayList;
    @SerializedName("status")
    @Expose
    public String status;

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public ArrayList<EmployeeMessage> getEmployeeMessageArrayList() {
        return employeeMessageArrayList;
    }

    public void setEmployeeMessageArrayList(ArrayList<EmployeeMessage> employeeMessageArrayList) {
        this.employeeMessageArrayList = employeeMessageArrayList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
