package com.zkteco.silkiddemo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DepartmentMessage {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("dep_name")
    @Expose
    public String dep_name;
    @SerializedName("company_id")
    @Expose
    public String company_id;
    @SerializedName("dep_description")
    @Expose
    public String dep_description;
    @SerializedName("status")
    @Expose
    public Object status;

    @SerializedName("created_by")
    @Expose
    public Object created_by;

    @SerializedName("created_at")
    @Expose
    public Object created_at;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDep_name() {
        return dep_name;
    }

    public void setDep_name(String dep_name) {
        this.dep_name = dep_name;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getDep_description() {
        return dep_description;
    }

    public void setDep_description(String dep_description) {
        this.dep_description = dep_description;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public Object getCreated_by() {
        return created_by;
    }

    public void setCreated_by(Object created_by) {
        this.created_by = created_by;
    }

    public Object getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Object created_at) {
        this.created_at = created_at;
    }
}
